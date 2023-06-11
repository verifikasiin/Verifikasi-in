package com.example.verifikasiin.network

import com.example.verifikasiin.network.request.LoginRequest
import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/api/register")
    fun register(
        @Field("nik") nik: String,
        @Field("password") password: String,
        @Field("confPassword") confPassword: String
    ) : Call<RegisterResponse>


    @Headers("Content-Type: application/json")
    @POST("/api/login")
    fun login(
        @Body request: LoginRequest
    ) : Call<LoginResponse>


}