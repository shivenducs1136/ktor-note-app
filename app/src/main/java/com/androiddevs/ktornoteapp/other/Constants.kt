package com.androiddevs.ktornoteapp.other

import androidx.security.crypto.EncryptedSharedPreferences

object Constants {
    val ignoreAuthUrls= listOf("/login","/register")
    const val DATABASE_NAME = "NotesDB"
    const val BASE_URL = "http://18.182.5.202:8001"
    const val ENCRYPTED = "enc_shared_pref"
    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"
    const val NO_EMAIL = "NO_EMAIL"
    const val NO_PASSWORD = "NO_PASSWORD"
    const val DEFAULT_COLOR_NOTES = "ffA500"
    const val ADD_OWNER_DIALOG = "ADD_OWNER_DIALOG"
}