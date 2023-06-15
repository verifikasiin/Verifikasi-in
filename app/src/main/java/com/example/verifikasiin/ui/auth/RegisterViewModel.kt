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
import com.example.verifikasiin.network.request.LoginRequest
import com.example.verifikasiin.network.request.RegisterRequest
import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(application: Application) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService(BASE_URL)

    var registerCallback : RegisterCallback? = null

    fun register(nik: String, password: String, confirmPassword : String) {
        val registerRequest = RegisterRequest(nik, password, confirmPassword)
        viewModelScope.launch {
            _loading.value = true
            val call =  apiService.register(registerRequest)
            call.enqueue(object: Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>,
                ) {
                    _loading.value = false
                    if(response.isSuccessful) {
                        registerCallback?.onRegisterSuccess()
                    } else {
                        val message = "NIK sudah terdaftar"
                        registerCallback?.onRegisterError(message)
                        Log.e(TAG, "onFailure: ${response.message()}" )
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registerCallback?.onRegisterError(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message}" )
                }

            })
        }

    }

    interface RegisterCallback {
        fun onRegisterSuccess()
        fun onRegisterError(errorMessage: String)
    }

    companion object {
        private const val TAG = "RegisterViewModel"
        private const val BASE_URL = "https://verifikasiin.uc.r.appspot.com/"
    }
}