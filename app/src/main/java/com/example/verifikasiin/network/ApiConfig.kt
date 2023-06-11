package com.example.verifikasiin.network

import android.content.SharedPreferences
import androidx.viewbinding.BuildConfig
import com.example.verifikasiin.data.UsersPreference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig (private val sharedPreferences: UsersPreference) {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val AUTH_PREFIX = "Bearer "
    }

    fun getApiService() : ApiService {
        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val tokenInterceptor = Interceptor {chain ->
            val originalRequest = chain.request()
            val token = sharedPreferences.getUser().token.toString()
            val request = if (token != null && token.isNotEmpty()) {
                originalRequest.newBuilder()
                    .header(AUTH_HEADER, AUTH_PREFIX + token)
                    .build()
            } else {
                originalRequest
            }

            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
        val retrofit =Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }


}