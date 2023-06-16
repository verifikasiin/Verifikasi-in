package com.example.verifikasiin.ui.verifikasi

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.network.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyDataViewModel(application: Application) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _user = MutableLiveData<GetUserByIDResponse>()
    val user : LiveData<GetUserByIDResponse> = _user

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService(BASE_URL)

    private lateinit var userModel : GetUserByIDResponse
    var updateUserCallback : UpdateUserCallback? = null
    var refreshTokenCallback : RefreshTokenCallback? = null

    fun updateUser(nik: String, user : GetUserByIDResponse) {
        viewModelScope.launch {
            _loading.value = true
            val call = apiService.updateUser(nik, user)
            call.enqueue(object : Callback<GetUserByIDResponse> {
                override fun onResponse(
                    call: Call<GetUserByIDResponse>,
                    response: Response<GetUserByIDResponse>
                ) {
                    _loading.value = false
                    if(response.isSuccessful){
                        updateUserCallback?.onUpdateSuccess()
                    }
                    else if(response.message().equals("Forbidden")) {
                        getToken()
                    }
                    else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        updateUserCallback?.onUpdateError(response.message())
                    }
                }

                override fun onFailure(call: Call<GetUserByIDResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    updateUserCallback?.onUpdateError(t.message.toString() + "AAAAAAAA")
                }

            })
        }
    }

    fun getToken() {
        viewModelScope.launch {
            _loading.value = true
            val call = apiService.refreshToken()
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _loading.value = false
                    if(response.isSuccessful) {
                        refreshTokenCallback?.onRefreshSuccess(response.body()?.accessToken.toString())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        refreshTokenCallback?.onRefreshError(response.message())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    refreshTokenCallback?.onRefreshError(t.message.toString())
                }

            })
        }
    }


    interface RefreshTokenCallback {
        fun onRefreshSuccess(token:String)
        fun onRefreshError(errorMessage: String)
    }
    interface UpdateUserCallback {
        fun onUpdateSuccess()
        fun onUpdateError(errorMessage: String)
    }

    companion object {
        private const val TAG = "VerifyDataViewModel"
        private const val BASE_URL = "https://verifikasiin.uc.r.appspot.com/"
    }
}