package com.example.verifikasiin.ui.verifikasi

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.ApiService
import com.example.verifikasiin.network.response.FaceVerificationResponse
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.OCRResponse
import com.example.verifikasiin.ui.edit.EditProfileViewModel
import com.example.verifikasiin.ui.main.MainViewModel
import com.example.verifikasiin.util.reduceImageSize
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.util.concurrent.Executors

class VerificationViewModel(application: Application) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private var _file = MutableLiveData<File>()
    var file : LiveData<File> = _file

    private val _file2 = MutableLiveData<String>()
    var file2 : LiveData<String> = _file2

    private val _user = MutableLiveData<GetUserByIDResponse>()
    val user : LiveData<GetUserByIDResponse> = _user

    private lateinit var userModel : GetUserByIDResponse

    fun setPhotoFile(file: File) {
        _file.value = file
    }

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService(BASE_URL)
    private val apiServiceUpload = apiConfig.getApiService(UPLOAD_BASE_URL)

    var verificationCallback : FaceVerificationCallback? = null
    var getUserCallback : GetUserCallback? = null
    var refreshTokenCallback : RefreshTokenCallback? = null
    var updateUserCallback : UpdateUserCallback? = null

    init {
        Log.d(TAG, usersPreference.getUser().nik.toString())
        getUser(usersPreference.getUser().nik.toString())
    }
    private fun getUser(nik: String) {
        viewModelScope.launch {
            _loading.value = true
            val call = apiServiceUpload.getUserById(nik)
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
                            getUserCallback?.onGetSuccess(b = false, url = "")
                        } else if(response.message().equals("Forbidden")){
                            getToken()
                        }
                        else {
                            _file2.value = responseBody?.fotoKtp.toString()
                            getUserCallback?.onGetSuccess(b = true, url = responseBody?.fotoKtp.toString())
                        }

                    } else {
                        Log.e(TAG, "onFailure: ${response.message()} disini" )
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

    fun getToken() {
        viewModelScope.launch {
            _loading.value = true
            val call = apiServiceUpload.refreshToken()
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
                    _loading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    refreshTokenCallback?.onRefreshError(t.message.toString())
                }

            })
        }
    }

    fun downloadFile(fileUrl: String, destinationFile: File, onFileDownloaded: (Boolean) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://storage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val fileDownloadService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseBody> = fileDownloadService.downloadFile(fileUrl)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val inputStream: InputStream? = response.body()?.byteStream()
                    val outputStream: OutputStream = FileOutputStream(destinationFile)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                            output.flush()
                        }
                    }

                    onFileDownloaded(true)
                } else {
                    onFileDownloaded(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                onFileDownloaded(false)
            }
        })
    }

    fun updateUser(nik: String, user : GetUserByIDResponse) {
        viewModelScope.launch {
            _loading.value = true
            val call = apiServiceUpload.updateUser(nik, user)
            call.enqueue(object : Callback<GetUserByIDResponse>{
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
                    updateUserCallback?.onUpdateError(t.message.toString())
                }

            })
        }
    }

    fun verify(file1 : File, file2Url: String){
        val fileImage = reduceImageSize(file1)
        val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData(
            "file1",
            file1.name,
            requestImageFile
        )
        val destinationFile = File(context.getExternalFilesDir(null)?.absolutePath, "file2.jpg")
        downloadFile(file2Url, destinationFile) { success ->
            if (success) {
                val fileImage2 = reduceImageSize(destinationFile)
                val requestImageFile2 = fileImage2.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart2: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file2",
                    destinationFile.name,
                    requestImageFile2
                )

                viewModelScope.launch {
                    _loading.value = true
                    val call = apiService.faceverify(imageMultipart, imageMultipart2)
                    call.enqueue(object : Callback<FaceVerificationResponse> {
                        override fun onResponse(
                            call: Call<FaceVerificationResponse>,
                            response: Response<FaceVerificationResponse>,
                        ) {
                            _loading.value = false
                            if (response.isSuccessful) {
                                if (response.body()?.message?.prediction == "True") {
                                    verificationCallback?.onVerifSuccess(
                                        usersPreference.getUser().nik.toString(),
                                        true
                                    )
                                } else {
                                    verificationCallback?.onVerifSuccess(
                                        usersPreference.getUser().nik.toString(),
                                        false
                                    )
                                }
                            } else {
                                Log.e(TAG, "onFailure: ${response.message()} disini")
                                verificationCallback?.onVerifFailed(response.message())
                            }
                        }

                        override fun onFailure(call: Call<FaceVerificationResponse>, t: Throwable) {
                            _loading.value = false
                            Log.e(TAG, "onFailure: ${t.message}")
                            verificationCallback?.onVerifFailed(t.message.toString())
                        }
                    })
                }
            } else {
                verificationCallback?.onVerifFailed("Failed to download file2")
            }
        }
    }

    interface UpdateUserCallback {
        fun onUpdateSuccess()
        fun onUpdateError(errorMessage: String)
    }
    interface GetUserCallback {
        fun onGetSuccess(url: String, b: Boolean)
        fun onGetError(errorMessage: String)
    }
    interface RefreshTokenCallback {
        fun onRefreshSuccess(token:String)
        fun onRefreshError(errorMessage: String)
    }
    interface FaceVerificationCallback{
        fun onVerifSuccess(nik : String, b : Boolean)
        fun onVerifFailed(errorMessage: String)
    }

    companion object {
        private const val TAG = "VerificationViewModel"
        private const val UPLOAD_BASE_URL = "https://verifikasiin.uc.r.appspot.com/"
        private const val BASE_URL = "https://verifikasiin-ml-api-chuotcahoa-uc.a.run.app/"
    }


}