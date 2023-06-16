package com.example.verifikasiin.network.response

import com.google.gson.annotations.SerializedName

data class FaceVerificationResponse(

	@field:SerializedName("message")
	val message: Message? = null
)

data class Message(

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("prediction")
	val prediction: String? = null
)
