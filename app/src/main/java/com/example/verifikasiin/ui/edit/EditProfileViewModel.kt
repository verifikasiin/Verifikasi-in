package com.example.verifikasiin.ui.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.response.GetUserByIDResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel(application: Application) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _user = MutableLiveData<GetUserByIDResponse>()
    val user : LiveData<GetUserByIDResponse> = _user

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService()

    private lateinit var userModel : GetUserByIDResponse
    var getUserCallback : GetUserCallback? = null

    init {
        getUser(usersPreference.getUser().nik.toString())
    }

    private fun getUser(nik: String) {
        viewModelScope.launch {
            _loading.value = true
            val call = apiService.getUserById(nik)
            call.enqueue(object : Callback<List<GetUserByIDResponse>> {
                override fun onResponse(
                    call: Call<List<GetUserByIDResponse>>,
                    response: Response<List<GetUserByIDResponse>>,
                ) {
                    _loading.value = false
                    if(response.isSuccessful) {
                        val responseBody = response.body()?.get(0)

                        if (responseBody != null) {
                            userModel = responseBody
                        }
                        _user.value = userModel
                        getUserCallback?.onGetSuccess()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}" )
                        getUserCallback?.onGetError("gagal")
                    }
                }

                override fun onFailure(call: Call<List<GetUserByIDResponse>>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}" )
                    getUserCallback?.onGetError(t.message.toString())
                }
            })
        }
    }

    interface GetUserCallback {
        fun onGetSuccess()
        fun onGetError(errorMessage: String)
    }

    companion object {
        private const val TAG = "EditProfileViewModel"
    }

}