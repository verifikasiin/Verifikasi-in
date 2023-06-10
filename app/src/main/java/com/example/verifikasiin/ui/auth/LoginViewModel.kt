package com.example.verifikasiin.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginViewModel(application: Application) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _user = MutableLiveData<UserModel>()
    val user : LiveData<UserModel> = _user

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService()

    private lateinit var userInfo : UserModel

    var loginCallback : LoginCallback? = null

    fun login(nik: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val call =  apiService.login(nik, password)
            call.enqueue(object: Callback<LoginResponse>, retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    _loading.value = false
                    if(response.isSuccessful){
                        userInfo = UserModel()
                        userInfo.nik = nik
                        userInfo.password = password
                        saveUserLogin(userInfo)
                        loginCallback?.onLoginSuccess()
                    }
                    else {
                        val message = "NIK atau password yang anda masukkan salah"
                        loginCallback?.onLoginError(message)
                        Log.e(TAG, "onFailure: ${response.message()}" )
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    loginCallback?.onLoginError("onFailure: ${t.message}")
                }

            })
        }

    }

    fun saveUserLogin(user : UserModel) {
        userInfo = UserModel()
        userInfo.nik = user.email
        userInfo.password = user.password
        usersPreference.setUser(userInfo)
    }

    interface LoginCallback {
        fun onLoginSuccess()
        fun onLoginError(errorMessage: String)
    }


    companion object {
        private const val TAG = "LoginViewModel"
    }
}