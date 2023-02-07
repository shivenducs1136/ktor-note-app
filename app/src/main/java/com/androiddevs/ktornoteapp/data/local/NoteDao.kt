package com.androiddevs.ktornoteapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.ktornoteapp.data.local.entities.LocallyDeletedNoteId
import com.androiddevs.ktornoteapp.data.local.entities.Note
import org.intellij.lang.annotations.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:Note)

    @Query("DELETE FROM notes WHERE id = :noteID")
    suspend fun deleteNodebyId(noteID:String){

    }

    @Query("DELETE FROM notes WHERE isSynced = 1")
    suspend fun deleteAllSyncedNotes()

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun observeNoteById(noteId:String):LiveData<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId:String):Note?

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNotes():kotlinx.coroutines.flow.Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getAllUnsyncedNotes():List<Note>

    @Query("SELECT * FROM locally_deleted_note_id")
    suspend fun getAllLocallyDeletedNoteId():List<LocallyDeletedNoteId>

    @Query("DELETE FROM locally_deleted_note_id WHERE deletedNoteId = :deletedNoteId")
    suspend fun deleteLocallyDeletedNoteId(deletedNoteId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocallyDeletedNoteID(locallyDeletedNoteId: LocallyDeletedNoteId)
}