package com.example.verifikasiin.ui.verifikasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.verifikasiin.R
import com.example.verifikasiin.data.SessionManager
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.ActivityEditProfileBinding
import com.example.verifikasiin.databinding.ActivityVerifyDataBinding
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.ViewModelFactory
import com.example.verifikasiin.ui.edit.EditProfileViewModel
import com.example.verifikasiin.ui.main.MainActivity
import com.google.gson.Gson

class VerifyDataActivity : AppCompatActivity(), VerifyDataViewModel.UpdateUserCallback, VerifyDataViewModel.RefreshTokenCallback {
    private lateinit var verifyDataBinding : ActivityVerifyDataBinding
    private val verifyDataViewModel by viewModels<VerifyDataViewModel> {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyDataBinding = ActivityVerifyDataBinding.inflate(layoutInflater)
        setContentView(verifyDataBinding.root)

        val session = SessionManager(this)
        val user = session.getSession()

        setUserData(user)

        verifyDataViewModel.loading.observe(this) {
            showLoading(it)
        }

        supportActionBar?.title = TITLE

        verifyDataViewModel.updateUserCallback = this
        verifyDataViewModel.refreshTokenCallback = this

        verifyDataBinding.btnSimpan.setOnClickListener {
            val request = GetUserByIDResponse(
                nik = verifyDataBinding.edtNik.text.toString(),
                nama = if (verifyDataBinding.edtNama.text?.isNotEmpty() == true) verifyDataBinding.edtNama.text.toString() else null,
                agama = if (verifyDataBinding.edtAgama.text?.isNotEmpty() == true) verifyDataBinding.edtAgama.text.toString() else null,
                jenisKelamin = if (verifyDataBinding.edtJk.text?.isNotEmpty() == true) verifyDataBinding.edtJk.text.toString() else null,
                statusPerkawinan = if (verifyDataBinding.edtKawin.text?.isNotEmpty() == true) verifyDataBinding.edtKawin.text.toString() else null,
                kewarganegaraan = if (verifyDataBinding.edtKewarganegaraan.text?.isNotEmpty() == true) verifyDataBinding.edtKewarganegaraan.text.toString() else null,
                pekerjaan = if (verifyDataBinding.edtPekerjaan.text?.isNotEmpty() == true) verifyDataBinding.edtPekerjaan.text.toString() else null,
                tempatTanggalLahir = if (verifyDataBinding.edtTempatLahir.text?.isNotEmpty() == true  && verifyDataBinding.edtTtl.text?.isNotEmpty() == true) "${verifyDataBinding.edtTempatLahir.text}, ${verifyDataBinding.edtTtl.text}" else null,
                golonganDarah = if (verifyDataBinding.edtGoldar.text?.isNotEmpty() == true) verifyDataBinding.edtGoldar.text.toString() else null,
                alamat = if (verifyDataBinding.edtAlamat.text?.isNotEmpty() == true) verifyDataBinding.edtAlamat.text.toString() else null,
                rtRw = if (verifyDataBinding.edtRt.text?.isNotEmpty() == true  && verifyDataBinding.edtRw.text?.isNotEmpty() == true  ) "${verifyDataBinding.edtRt.text}/${verifyDataBinding.edtRw.text}" else null,
                kelurahan = if (verifyDataBinding.edtKelurahan.text?.isNotEmpty() == true) verifyDataBinding.edtKelurahan.text.toString() else null,
                kecamatan = if (verifyDataBinding.edtKecamatan.text?.isNotEmpty() == true) verifyDataBinding.edtKecamatan.text.toString() else null
            )
            if(showNIKError()){
                verifyDataViewModel.updateUser(verifyDataBinding.edtNik.text.toString(), request)
            }
        }
    }

    private fun showLoading(loading : Boolean) {
        verifyDataBinding.loading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun setUserData(user : GetUserByIDResponse) {
        val ttl = user.tempatTanggalLahir?.split(",")
        val rtrw = user.rtRw?.split("/")
        verifyDataBinding.edtNik.setText(user.nik)
        verifyDataBinding.edtNama.setText(user.nama)
        verifyDataBinding.edtTempatLahir.setText(ttl?.get(0) ?: "")
        verifyDataBinding.edtTtl.setText(ttl?.get(1) ?: "")
        verifyDataBinding.edtTtl.setText(user.jenisKelamin)
    }

    fun showNIKError() : Boolean {
        if(verifyDataBinding.edtNik.text?.isNullOrEmpty() == true || verifyDataBinding.edtNik.text.toString().length != 16){
            verifyDataBinding.edtNik.error = "Tolong masukkan 16 angka NIK pada KTP anda"
            return false
        }
        return true
    }

    override fun onRefreshSuccess(token: String) {
        val usersPreference = UsersPreference(this)
        Toast.makeText(this, "Token refreshed", Toast.LENGTH_SHORT).show()
        usersPreference.updateToken(token)
    }

    override fun onRefreshError(errorMessage: String) {
        Toast.makeText(this, "Gagal mendapatkan token : $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateSuccess() {
        Toast.makeText(this, "Berhasil memperbaharui informasi", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@VerifyDataActivity, MainActivity::class.java))
    }

    override fun onUpdateError(errorMessage: String) {
        Toast.makeText(this, "Gagal memperbaharui data: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "VerifyDataActivity"
        private const val TITLE = "Lengkapi Profil KTP"
    }

}