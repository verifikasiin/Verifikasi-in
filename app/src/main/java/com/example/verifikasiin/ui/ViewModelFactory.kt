package com.example.verifikasiin.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifikasiin.ui.auth.LoginViewModel
import com.example.verifikasiin.ui.auth.RegisterViewModel
import com.example.verifikasiin.ui.main.MainViewModel

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
        }
        throw java.lang.IllegalArgumentException("Unknown View Model class: ${modelClass.name}")
    }
}