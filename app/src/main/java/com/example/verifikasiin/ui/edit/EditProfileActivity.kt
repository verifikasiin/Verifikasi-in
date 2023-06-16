package com.example.verifikasiin.ui.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.example.verifikasiin.R
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.ActivityEditProfileBinding
import com.example.verifikasiin.databinding.ActivityMainBinding
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.ViewModelFactory

class EditProfileActivity : AppCompatActivity(), EditProfileViewModel.GetUserCallback, EditProfileViewModel.UpdateUserCallback, EditProfileViewModel.RefreshTokenCallback {

    private lateinit var editProfileBinding : ActivityEditProfileBinding
    private val editProfileViewModel by viewModels<EditProfileViewModel> {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)

        editProfileViewModel.updateUserCallback = this
        editProfileViewModel.getUserCallback = this
        editProfileViewModel.updateUserCallback = this

        supportActionBar?.title = TITLE

        editProfileViewModel.user.observe(this) {
            setUserData(it)
        }

        editProfileViewModel.loading.observe(this) {
            showLoading(it)
        }

        editProfileBinding.btnSimpan.setOnClickListener {
            val request = GetUserByIDResponse(
                nik = editProfileBinding.edtNik.text.toString(),
                nama = if (editProfileBinding.edtNama.text?.isNotEmpty() == true) editProfileBinding.edtNama.text.toString() else null,
                agama = if (editProfileBinding.edtAgama.text?.isNotEmpty() == true) editProfileBinding.edtAgama.text.toString() else null,
                jenisKelamin = if (editProfileBinding.edtJk.text?.isNotEmpty() == true) editProfileBinding.edtJk.text.toString() else null,
                statusPerkawinan = if (editProfileBinding.edtKawin.text?.isNotEmpty() == true) editProfileBinding.edtKawin.text.toString() else null,
                kewarganegaraan = if (editProfileBinding.edtKewarganegaraan.text?.isNotEmpty() == true) editProfileBinding.edtKewarganegaraan.text.toString() else null,
                pekerjaan = if (editProfileBinding.edtPekerjaan.text?.isNotEmpty() == true) editProfileBinding.edtPekerjaan.text.toString() else null,
                tempatTanggalLahir = if (editProfileBinding.edtTempatLahir.text?.isNotEmpty() == true  && editProfileBinding.edtTtl.text?.isNotEmpty() == true) "${editProfileBinding.edtTempatLahir.text}, ${editProfileBinding.edtTtl.text}" else null,
                golonganDarah = if (editProfileBinding.edtGoldar.text?.isNotEmpty() == true) editProfileBinding.edtGoldar.text.toString() else null,
                alamat = if (editProfileBinding.edtAlamat.text?.isNotEmpty() == true) editProfileBinding.edtAlamat.text.toString() else null,
                rtRw = if (editProfileBinding.edtRt.text?.isNotEmpty() == true  && editProfileBinding.edtRw.text?.isNotEmpty() == true  ) "${editProfileBinding.edtRt.text}/${editProfileBinding.edtRw.text}" else null,
                kelurahan = if (editProfileBinding.edtKelurahan.text?.isNotEmpty() == true) editProfileBinding.edtKelurahan.text.toString() else null,
                kecamatan = if (editProfileBinding.edtKecamatan.text?.isNotEmpty() == true) editProfileBinding.edtKecamatan.text.toString() else null,
                berlakuHingga = if (editProfileBinding.edtBerlaku.text?.isNotEmpty() == true) editProfileBinding.edtBerlaku.text.toString() else null
            )
            showNIKError()
            if(showNIKError()){
                editProfileViewModel.updateUser(editProfileBinding.edtNik.text.toString(), request)
            }
        }
    }

    private fun showLoading(loading : Boolean) {
        editProfileBinding.loading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun setUserData(user : GetUserByIDResponse) {
        val ttl = user.tempatTanggalLahir?.split(",")
        val rtrw = user.rtRw?.split("/")
        editProfileBinding.edtNik.setText(user.nik)
        editProfileBinding.edtNama.setText(user.nama)
        editProfileBinding.edtTempatLahir.setText(ttl?.get(0) ?: "")
        editProfileBinding.edtTtl.setText(ttl?.get(1)?.trim() ?: "")
        editProfileBinding.edtJk.setText(user.jenisKelamin)
        editProfileBinding.edtGoldar.setText(user.golonganDarah)
        editProfileBinding.edtAlamat.setText(user.alamat)
        editProfileBinding.edtRt.setText(rtrw?.get(0)?:"")
        editProfileBinding.edtRw.setText(rtrw?.get(1)?:"")
        editProfileBinding.edtKelurahan.setText(user.kelurahan)
        editProfileBinding.edtKecamatan.setText(user.kecamatan)
        editProfileBinding.edtAgama.setText(user.agama)
        editProfileBinding.edtKawin.setText(user.statusPerkawinan)
        editProfileBinding.edtPekerjaan.setText(user.pekerjaan)
        editProfileBinding.edtKewarganegaraan.setText(user.kewarganegaraan)
        editProfileBinding.edtBerlaku.setText(user.berlakuHingga)
    }

    override fun onRefreshSuccess(token: String) {
        val usersPreference = UsersPreference(this)
        usersPreference.updateToken(token)
    }

    fun showNIKError() : Boolean {
        if(editProfileBinding.edtNik.text?.isNullOrEmpty() == true || editProfileBinding.edtNik.text.toString().length != 16){
            editProfileBinding.edtNik.error = "Tolong masukkan 16 angka NIK pada KTP anda"
            return false
        }
        return true
    }

    override fun onRefreshError(errorMessage: String) {
        Toast.makeText(this, "Gagal mendapatkan token : $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateSuccess(nik: String) {
        Toast.makeText(this, "Berhasil memperbaharui informasi", Toast.LENGTH_SHORT).show()
        editProfileViewModel.getUser(nik)
    }

    override fun onUpdateError(errorMessage: String) {
        Toast.makeText(this, "Gagal memperbaharui data data : $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onGetSuccess() {

    }

    override fun onGetError(errorMessage: String) {
        Toast.makeText(this, "Gagal mendapatkan data : $errorMessage", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TITLE = "Edit Profile"
    }

}