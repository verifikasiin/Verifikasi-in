package com.example.verifikasiin.network.response

import com.google.gson.annotations.SerializedName

data class GetUserByIDResponse(

	@field:SerializedName("tempat_tanggal_lahir")
	val tempatTanggalLahir: Any,

	@field:SerializedName("golongan_darah")
	val golonganDarah: Any,

	@field:SerializedName("agama")
	val agama: Any,

	@field:SerializedName("foto_ktp")
	val fotoKtp: Any,

	@field:SerializedName("kelurahan")
	val kelurahan: Any,

	@field:SerializedName("alamat")
	val alamat: Any,

	@field:SerializedName("berlaku_hingga")
	val berlakuHingga: Any,

	@field:SerializedName("wajah_verified")
	val wajahVerified: Any,

	@field:SerializedName("kewarganegaraan")
	val kewarganegaraan: Any,

	@field:SerializedName("nik")
	val nik: String,

	@field:SerializedName("refresh_token")
	val refreshToken: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("rtRw")
	val rtRw: Any,

	@field:SerializedName("nama")
	val nama: Any,

	@field:SerializedName("pekerjaan")
	val pekerjaan: Any,

	@field:SerializedName("kecamatan")
	val kecamatan: Any,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: Any,

	@field:SerializedName("status_perkawinan")
	val statusPerkawinan: Any,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
