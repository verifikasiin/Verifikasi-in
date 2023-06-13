package com.example.verifikasiin.network

import com.example.verifikasiin.network.request.LoginRequest
import com.example.verifikasiin.network.request.RegisterRequest
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.network.response.LoginResponse
import com.example.verifikasiin.network.response.RegisterResponse
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

    @GET("/api/users/{userId}")
    fun getUserById(
        @Path("userId") userId: String
    ) : Call<List<GetUserByIDResponse>>
}