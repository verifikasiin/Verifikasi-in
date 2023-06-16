package com.example.verifikasiin.network.response

import com.google.gson.annotations.SerializedName

data class UploadKTPResponse(

	@field:SerializedName("foto_ktp")
	val fotoKtp: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
