package com.androiddevs.ktornoteapp.ui.notedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.Resource
import com.androiddevs.ktornoteapp.repositories.NoteRepository
import kotlinx.coroutines.launch

class NoteDetailViewModel @ViewModelInject constructor(
    private val repository: NoteRepository
) : ViewModel()  {

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()

    val addOwnerStatus : LiveData<Event<Resource<String>>> = _addOwnerStatus

    fun addOwnerToNote(owner: String,noteId:String){
        _addOwnerStatus.postValue(Event(Resource.loading(null)))
        if(owner.isEmpty()|| noteId.isEmpty()){
            _addOwnerStatus.postValue(Event(Resource.error("The owner can't be empty",null)))
            return
        }
        viewModelScope.launch {
            val result = repository.addOwnerToNote(owner,noteId)
            _addOwnerStatus.postValue(Event(result))
        }
    }

    fun observeNoteById(noteId: String) = repository.observeNoteById(noteId)



}