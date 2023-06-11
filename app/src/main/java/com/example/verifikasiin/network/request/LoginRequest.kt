package com.example.verifikasiin.network.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

	@field:SerializedName("nik")
	val nik: String,

	@field:SerializedName("password")
	val password: String
)
