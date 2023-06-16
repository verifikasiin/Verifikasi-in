package com.example.verifikasiin.ui.verifikasi

import android.app.Application
import android.os.Build
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verifikasiin.data.SessionManager
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.network.ApiConfig
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.OCRResponse
import com.example.verifikasiin.network.response.UploadKTPResponse
import com.example.verifikasiin.util.reduceImageSize
import com.example.verifikasiin.util.rotateFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.File

class KtpVerificationViewModel(application: Application) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _file = MutableLiveData<File?>()
    val file : LiveData<File?> = _file

    private val context = application.applicationContext
    private val usersPreference = UsersPreference(context)

    private val apiConfig = ApiConfig(usersPreference)
    private val apiService = apiConfig.getApiService(BASE_URL)
    private val apiServiceUpload = apiConfig.getApiService(UPLOAD_BASE_URL)

    var ocrCallback : OCRCallback? = null
    var uploadCallback : UploadCallback? = null
    var refreshTokenCallback : RefreshTokenCallback? = null

    fun uploadKtp(userId : String, image: File) {
        val fileImage = reduceImageSize(image)
        val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile
        )
        viewModelScope.launch {
            _loading.value =  true
            val call = apiServiceUpload.uploadKtp(userId, imageMultipart)
            call.enqueue(object : Callback<UploadKTPResponse> {
                override fun onResponse(
                    call: Call<UploadKTPResponse>,
                    response: Response<UploadKTPResponse>,
                ) {
                    _loading.value =  false
                    if(response.isSuccessful) {
                        uploadCallback?.onUploadSuccess(response.message())
                    } else if (response.message().equals("Forbidden")){
                        getToken()
                    } else{
                        Log.e(TAG, "onFailure: ${response.message()}")
                        uploadCallback?.onUploadError(response.message())
                    }
                }

                override fun onFailure(call: Call<UploadKTPResponse>, t: Throwable) {
                    _loading.value =  false
                    Log.e(TAG, "onFailure: ${t.message}")
                    uploadCallback?.onUploadError(t.message.toString())
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
    fun ocr(image : File) {
        val fileImage = reduceImageSize(image)
        val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            image.name,
            requestImageFile
        )
        viewModelScope.launch {
            _loading.value = true
            val call = apiService.ocr(imageMultipart)
            call.enqueue(object : Callback<OCRResponse> {
                override fun onResponse(call: Call<OCRResponse>, response: Response<OCRResponse>) {
                    _loading.value = false
                    if(response.isSuccessful){
                        val responsebody = response.body()?.data
                        val user = GetUserByIDResponse(
                            nik = responsebody?.nik.toString(),
                            nama = responsebody?.nama.toString(),
                            tempatTanggalLahir = "${responsebody?.tempatLahir}, ${responsebody?.tanggalLahir}",
                            jenisKelamin = responsebody?.jenisKelamin.toString()
                        )
                        ocrCallback?.onOCRSuccess(user)
                    }
                    else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        ocrCallback?.onOCRError(response.message())
                    }
                }

                override fun onFailure(call: Call<OCRResponse>, t: Throwable) {
                    _loading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    ocrCallback?.onOCRError(t.message.toString())
                }

            })
        }
    }

    fun setPhotoFile(file: File) {
        _file.value = file
    }

    interface RefreshTokenCallback {
        fun onRefreshSuccess(token:String)
        fun onRefreshError(errorMessage: String)
    }
    interface OCRCallback{
        fun onOCRSuccess(user : GetUserByIDResponse)
        fun onOCRError(errorMessage: String)
    }

    interface UploadCallback{
        fun onUploadSuccess(message: String)
        fun onUploadError(errorMessage: String)
    }

    companion object {
        private const val TAG = "KtpVerificationViewModel"
        private const val UPLOAD_BASE_URL = "https://verifikasiin.uc.r.appspot.com/"
        private const val BASE_URL = "https://verifikasiin-ml-api-chuotcahoa-uc.a.run.app/"
    }

}