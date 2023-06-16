package com.example.verifikasiin.data

import android.content.Context

class UsersPreference(context : Context) {

    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value : UserModel) {
        val editor = preference.edit()
        editor.putString(NAME, value.name)
        editor.putString(PASSWORD, value.password)
        editor.putString(NIK, value.nik)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun updateToken(token : String) {
        val editor = preference.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }
    fun getUser() : UserModel {
        val model = UserModel(
            name = preference.getString(NAME, ""),
            nik = preference.getString(NIK, ""),
            password = preference.getString(PASSWORD, ""),
            token = preference.getString(TOKEN, "")
        )
        return model
    }

    fun clearUser() {
        val editor = preference.edit()
        editor.clear()
        editor.commit()
    }


    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val PASSWORD = "password"
        private const val NAME = "name"
        private const val TOKEN = "token"
        private const val NIK = "nik"
    }
}