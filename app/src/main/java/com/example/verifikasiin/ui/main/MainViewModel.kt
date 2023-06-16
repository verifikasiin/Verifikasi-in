package com.example.verifikasiin.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.auth.LoginViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _user = MutableLiveData<GetUserByIDResponse>()
    val user : LiveData<GetUserByIDResponse> = _user

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService(BASE_URL)

    private lateinit var userModel : GetUserByIDResponse
    var getUserCallback : GetUserCallback? = null

    init {
        Log.e(TAG, usersPreference.getUser().nik.toString())
        getUser(usersPreference.getUser().nik.toString())
    }

    private fun getUser(nik: String) {
        viewModelScope.launch {
            _loading.value = true
            val call = apiService.getUserById(nik)
            call.enqueue(object : Callback<GetUserByIDResponse> {
                override fun onResponse(
                    call: Call<GetUserByIDResponse>,
                    response: Response<GetUserByIDResponse>,
                ) {
                    _loading.value = false
                    if(response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d(TAG, responseBody.toString())
                        if(responseBody?.fotoKtp.isNullOrEmpty()) {
                            getUserCallback?.onGetSuccess(false)
                        }
                        else {
                            userModel = GetUserByIDResponse(
                                nik = responseBody?.nik.toString(),
                                nama = responseBody?.nama.toString(),
                                wajahVerified = responseBody?.wajahVerified
                            )
                            _user.value = userModel
                            getUserCallback?.onGetSuccess(true)
                        }

                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}" )
                        getUserCallback?.onGetError("gagal")
                    }
                }

                override fun onFailure(call: Call<GetUserByIDResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}" )
                    getUserCallback?.onGetError(t.message.toString())
                }
            })
        }
    }



   interface GetUserCallback {
        fun onGetSuccess(ktpVerified: Boolean)
        fun onGetError(errorMessage: String)
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val BASE_URL = "https://verifikasiin.uc.r.appspot.com/"
    }
}