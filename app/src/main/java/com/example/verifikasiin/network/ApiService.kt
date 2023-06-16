package com.example.verifikasiin.network

import com.example.verifikasiin.network.request.LoginRequest
import com.example.verifikasiin.network.request.RegisterRequest
import com.example.verifikasiin.network.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/register")
    fun register(
        @Body request: RegisterRequest
    ) : Call<RegisterResponse>


    @Headers("Content-Type: application/json")
    @POST("/api/login")
    fun login(
        @Body request: LoginRequest
    ) : Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @PATCH("/api/users/{userId}")
    fun updateUser(
        @Path("userId") userId: String,
        @Body request: GetUserByIDResponse
    ): Call<GetUserByIDResponse>

    @GET("/api/users/{userId}")
    fun getUserById(
        @Path("userId") userId: String
    ) : Call<GetUserByIDResponse>

    @POST("/api/token")
    fun refreshToken(): Call<LoginResponse>

    @Multipart
    @POST("/api/upload/{userId}")
    fun uploadKtp(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part
    ) : Call<UploadKTPResponse>

    @Multipart
    @POST("/ocr")
    fun ocr(
        @Part file: MultipartBody.Part
    ) : Call<OCRResponse>
}