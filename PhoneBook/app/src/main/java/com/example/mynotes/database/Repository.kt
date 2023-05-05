package com.example.mynotes.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.mynotes.domain.model.ColorModel
import com.example.mynotes.domain.model.PhoneModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val phoneDao: PhoneDao,
    private val colorDao: ColorDao,
    private val dbMapper: DbMapper
) {

    // Working Notes
    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesNotInTrash(): LiveData<List<PhoneModel>> = phonesNotInTrashLiveData

    // Deleted Notes
    private val phonesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesInTrash(): LiveData<List<PhoneModel>> = phonesInTrashLiveData

    init {
        initDatabase(this::updatePhonesLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                colorDao.insertAll(*colors)
            }

            // Prepopulate notes
            val phones = PhoneDbModel.DEFAULT_NOTES.toTypedArray()
            val dbPhones = phoneDao.getAllSync()
            if (dbPhones.isNullOrEmpty()) {
                phoneDao.insertAll(*phones)
            }

            postInitAction.invoke()
        }
    }

    // get list of working notes or deleted notes
    private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val dbPhones: List<PhoneDbModel> =
            phoneDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapPhones(dbPhones, colorDbModels)
    }

    fun insertPhone(phone: PhoneModel) {
        phoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    fun deletePhones(phoneIds: List<Long>) {
        phoneDao.delete(phoneIds)
        updatePhonesLiveData()
    }

    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = phoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isInTrash = true)
        phoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun restorePhonesFromTrash(phoneIds: List<Long>) {
        val dbPhonesInTrash = phoneDao.getNotesByIdsSync(phoneIds)
        dbPhonesInTrash.forEach {
            val newDbPhone = it.copy(isInTrash = false)
            phoneDao.insert(newDbPhone)
        }
        updatePhonesLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    private fun updatePhonesLiveData() {
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }
}