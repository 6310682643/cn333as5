package com.example.mynotes.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.database.AppDatabase
import com.example.mynotes.database.DbMapper
import com.example.mynotes.database.Repository
import com.example.mynotes.domain.model.ColorModel
import com.example.mynotes.domain.model.PhoneModel
import com.example.mynotes.routing.MyPhonesRouter
import com.example.mynotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val phonesNotInTrash: LiveData<List<PhoneModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneModel())

    val phoneEntry: LiveData<PhoneModel> = _phoneEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val phonesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedPhones = MutableLiveData<List<PhoneModel>>(listOf())

    val selectedPhones: LiveData<List<PhoneModel>> = _selectedPhones

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewPhoneClick() {
        _phoneEntry.value = PhoneModel()
        MyPhonesRouter.navigateTo(Screen.SaveNote)
    }

    fun onPhoneClick(phone: PhoneModel) {
        _phoneEntry.value = phone
        MyPhonesRouter.navigateTo(Screen.SaveNote)
    }

    fun onPhoneCheckedChange(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)
        }
    }

    fun onPhoneSelected(phone: PhoneModel) {
        _selectedPhones.value = _selectedPhones.value!!.toMutableList().apply {
            if (contains(phone)) {
                remove(phone)
            } else {
                add(phone)
            }
        }
    }

    fun restorePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restorePhonesFromTrash(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun permanentlyDeletePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun onPhoneEntryChange(phone: PhoneModel) {
        _phoneEntry.value = phone
    }

    fun savePhone(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneModel()
            }
        }
    }

    fun movePhoneToTrash(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(phone.id)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)
            }
        }
    }
}