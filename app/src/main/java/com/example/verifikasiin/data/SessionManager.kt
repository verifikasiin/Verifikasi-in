package com.example.verifikasiin.data

import android.content.Context
import android.content.SharedPreferences
import com.example.verifikasiin.network.response.GetUserByIDResponse

class SessionManager(context: Context) {
    private val sharedSessionPreferences : SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedSessionPreferences.edit()

    fun saveSession(user: GetUserByIDResponse){
        editor.putString(NIK, user.nik)
        editor.putString(NAMA, user.nama)
        editor.putString(TEMPAT_TANGGAL_LAHIR, user.tempatTanggalLahir)
        editor.putString(JENIS_KELAMIN, user.jenisKelamin)
        editor.apply()
    }

    fun getSession() : GetUserByIDResponse {
        val model = GetUserByIDResponse(
            nik = sharedSessionPreferences.getString(NIK, "").toString(),
            nama = sharedSessionPreferences.getString(NAMA, ""),
            tempatTanggalLahir = sharedSessionPreferences.getString(TEMPAT_TANGGAL_LAHIR, ""),
            jenisKelamin = sharedSessionPreferences.getString(JENIS_KELAMIN, "")
        )
        return model
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "session_pref"
        private const val NIK = "nik"
        private const val NAMA = "nama"
        private const val TEMPAT_TANGGAL_LAHIR = "tempat_tanggal_lahir"
        private const val JENIS_KELAMIN = "jenis_kelamin"
    }
}