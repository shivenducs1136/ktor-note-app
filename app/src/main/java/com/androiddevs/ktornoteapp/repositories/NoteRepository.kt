package com.androiddevs.ktornoteapp.repositories


import android.app.Application
import androidx.room.Delete
import com.androiddevs.ktornoteapp.data.local.NoteDao
import com.androiddevs.ktornoteapp.data.local.entities.LocallyDeletedNoteId
import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.data.remote.NoteApi
import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequests
import com.androiddevs.ktornoteapp.data.remote.requests.AddOwnerRequest
import com.androiddevs.ktornoteapp.data.remote.requests.DeleteNoteRequest
import com.androiddevs.ktornoteapp.other.Resource
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import com.androiddevs.ktornoteapp.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.addNote(note)
        } catch(e: Exception) {
            null
        }
        if(response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            noteDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    suspend fun getNoteById(noteID: String) = noteDao.getNoteById(noteID)

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                noteApi.getNotes()
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    insertNotes(it.onEach { note-> note.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    private var curNotesResponse: Response<List<Note>>? = null


    suspend fun syncNotes(){
        val locallyDeletedNoteIds = noteDao.getAllLocallyDeletedNoteId()
        locallyDeletedNoteIds.forEach{id->
            deleteNote(id.deletedNoteId)
        }
        val unsyncedNotes = noteDao.getAllUnsyncedNotes()
        unsyncedNotes.forEach{note->
            insertNote(note)
        }
        curNotesResponse = noteApi.getNotes()
        curNotesResponse?.body()?.let {note->
            noteDao.deleteAllNotes()
            insertNotes(note.onEach {
                it.isSynced = true
            })

        }
    }

    suspend fun addOwnerToNote(owner: String, noteId: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.addOwnerToNote(AddOwnerRequest(owner, noteId))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequests(email, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    suspend fun deleteNote(noteId:String){
        val response = try{
            noteApi.deleteNote(DeleteNoteRequest(noteId))

        }catch (e:Exception){
            null
        }
        noteDao.deleteNodebyId(noteId)
        if(response == null || !response.isSuccessful){
            noteDao.insertLocallyDeletedNoteID(LocallyDeletedNoteId(noteId))
        }
        else{
            deleteLocallyDeletedNoteId(noteId)
        }
    }
    suspend fun deleteLocallyDeletedNoteId(deletedNoteId:String){
        noteDao.deleteLocallyDeletedNoteId(deletedNoteId)
    }

    fun observeNoteById(noteId:String) = noteDao.observeNoteById(noteId)

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequests(email, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }
}