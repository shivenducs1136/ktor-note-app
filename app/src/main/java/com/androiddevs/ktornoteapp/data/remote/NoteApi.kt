package com.androiddevs.ktornoteapp.data.remote

import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequests
import com.androiddevs.ktornoteapp.data.remote.requests.AddOwnerRequest
import com.androiddevs.ktornoteapp.data.remote.requests.DeleteNoteRequest
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NoteApi {

    @POST("/register")
    suspend fun register(
     @Body registerRequests: AccountRequests
    ):Response<SimpleResponse>

    @POST("/login")
    suspend fun login(
        @Body loginRequests: AccountRequests
    ):Response<SimpleResponse>

    @POST("/addNotes")
    suspend fun addNote(
        @Body note: Note
    ):Response<ResponseBody>

    @POST("/deleteNotes")
    suspend fun deleteNote(
        @Body deleteNoteRequest: DeleteNoteRequest
    ):Response<ResponseBody>

    @POST("/addOwnerToNote")
    suspend fun addOwnerToNote(
        @Body addOwnerRequest: AddOwnerRequest
    ):Response<SimpleResponse>

    @GET("/getNotes")
    suspend fun getNotes():Response<List<Note>>

}