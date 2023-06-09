package com.example.verifikasiin.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedSessionPreferences : SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedSessionPreferences.edit()

    fun saveSession(user: UserModel){
        editor.putString(EMAIL, user.email)
        editor.putString(NIK, user.nik)
        editor.putString(PASSWORD, user.password)
        editor.apply()
    }

    fun getSession() : UserModel {
        val model = UserModel(
            email = sharedSessionPreferences.getString(EMAIL, ""),
            nik = sharedSessionPreferences.getString(NIK, ""),
            password = sharedSessionPreferences.getString(PASSWORD, ""),
        )
        return model
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "session_pref"
        private const val EMAIL = "email"
        private const val NIK = "nik"
        private const val PASSWORD = "password"
    }
}