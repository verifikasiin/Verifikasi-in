package com.example.verifikasiin.network.response

import com.google.gson.annotations.SerializedName

data class GetUserByIDResponse(
	@field:SerializedName("nik")
	val nik: String,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("agama")
	val agama: String? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: String? = null,

	@field:SerializedName("status_perkawinan")
	val statusPerkawinan: String? = null,

	@field:SerializedName("kewarganegaraan")
	val kewarganegaraan: String? = null,

	@field:SerializedName("pekerjaan")
	val pekerjaan: String? = null,

	@field:SerializedName("tempat_tanggal_lahir")
	val tempatTanggalLahir: String? = null,

	@field:SerializedName("golongan_darah")
	val golonganDarah: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("rtRw")
	val rtRw: String? = null,

	@field:SerializedName("kelurahan")
	val kelurahan: String? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("foto_ktp")
	val fotoKtp: String? = null,

	@field:SerializedName("berlaku_hingga")
	val berlakuHingga: String? = null,

	@field:SerializedName("wajah_verified")
	val wajahVerified: Boolean? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
