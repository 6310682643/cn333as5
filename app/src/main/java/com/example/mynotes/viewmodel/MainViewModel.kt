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
import com.example.mynotes.routing.MyNotesRouter
import com.example.mynotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val notesNotInTrash: LiveData<List<PhoneModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _noteEntry = MutableLiveData(PhoneModel())

    val noteEntry: LiveData<PhoneModel> = _noteEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val notesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedNotes = MutableLiveData<List<PhoneModel>>(listOf())

    val selectedNotes: LiveData<List<PhoneModel>> = _selectedNotes

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewNoteClick() {
        _noteEntry.value = PhoneModel()
        MyNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteClick(note: PhoneModel) {
        _noteEntry.value = note
        MyNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteCheckedChange(note: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(note)
        }
    }

    fun onNoteSelected(note: PhoneModel) {
        _selectedNotes.value = _selectedNotes.value!!.toMutableList().apply {
            if (contains(note)) {
                remove(note)
            } else {
                add(note)
            }
        }
    }

    fun restoreNotes(notes: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restorePhonesFromTrash(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedNotes.value = listOf()
            }
        }
    }

    fun permanentlyDeleteNotes(notes: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedNotes.value = listOf()
            }
        }
    }

    fun onNoteEntryChange(note: PhoneModel) {
        _noteEntry.value = note
    }

    fun saveNote(note: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(note)

            withContext(Dispatchers.Main) {
                MyNotesRouter.navigateTo(Screen.Notes)

                _noteEntry.value = PhoneModel()
            }
        }
    }

    fun moveNoteToTrash(note: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(note.id)

            withContext(Dispatchers.Main) {
                MyNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }
}