package com.androiddevs.ktornoteapp.ui.notes


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.Resource
import com.androiddevs.ktornoteapp.repositories.NoteRepository
import kotlinx.coroutines.launch

class NotesViewModel @ViewModelInject constructor(
    private val repository: NoteRepository
): ViewModel()  {


    private val _forceUpdate = MutableLiveData<Boolean> (false)

    private val _allNotes = _forceUpdate.switchMap {
        repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }
    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

    fun syncAllNotes() = _forceUpdate.postValue(true)

    fun deleteNote(noteid:String) = viewModelScope.launch {
        repository.deleteNote(noteid)
    }
    fun deleteLocallyDeletedNoteId(deletedNoteId:String) = viewModelScope.launch {
        repository.deleteLocallyDeletedNoteId(deletedNoteId)
    }


}