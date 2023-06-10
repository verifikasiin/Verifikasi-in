package com.example.verifikasiin.network

import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("/api/register")
    fun register(
        @Field("nik") nik: String,
        @Field("password") password: String,
        @Field("confPassword") confPassword: String
    ) : Call<RegisterResponse>


    @FormUrlEncoded
    @POST("/api/login")
    fun login(
        @Field("nik") nik: String,
        @Field("password") password: String
    ) : Call<LoginResponse>


}