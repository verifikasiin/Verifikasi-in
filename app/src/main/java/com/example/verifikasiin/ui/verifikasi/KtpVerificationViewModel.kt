package com.example.verifikasiin.ui.verifikasi

import android.app.Application
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.util.reduceImageSize
import com.example.verifikasiin.util.rotateFile
import java.io.File

class KtpVerificationViewModel(application: Application) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _file = MutableLiveData<File?>()
    val file : LiveData<File?> = _file

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService()

    fun setPhotoFile(file: File) {
        _file.value = file
    }

}