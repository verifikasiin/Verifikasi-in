package com.example.verifikasiin.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifikasiin.ui.auth.LoginViewModel
import com.example.verifikasiin.ui.auth.RegisterViewModel
import com.example.verifikasiin.ui.edit.EditProfileViewModel
import com.example.verifikasiin.ui.main.MainViewModel
import com.example.verifikasiin.ui.verifikasi.KtpVerificationViewModel
import com.example.verifikasiin.ui.verifikasi.VerificationViewModel
import com.example.verifikasiin.ui.verifikasi.VerifyDataViewModel

class ViewModelFactory(
    private val mApp : Application
) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(KtpVerificationViewModel::class.java)) {
            return KtpVerificationViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(VerifyDataViewModel::class.java)) {
            return VerifyDataViewModel(mApp) as T
        } else if(modelClass.isAssignableFrom(VerificationViewModel::class.java)) {
            return VerificationViewModel(mApp) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model class: ${modelClass.name}")
    }
}