package com.example.verifikasiin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.verifikasiin.R
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.ActivityMainBinding
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.ViewModelFactory
import com.example.verifikasiin.ui.auth.AuthActivity
import com.example.verifikasiin.ui.edit.EditProfileActivity
import com.example.verifikasiin.ui.verifikasi.KtpVerificationActivity
import com.example.verifikasiin.ui.verifikasi.VerificationActivity

class MainActivity : AppCompatActivity(), View.OnClickListener, MainViewModel.GetUserCallback {

    private lateinit var binding : ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.getUserCallback = this
        mainViewModel.user.observe(this) {
            setUserData(it)
        }
        binding.btnUbahProfil.setOnClickListener(this)
        binding.btnVerifikasi.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun setUserData(user : GetUserByIDResponse) {
        binding.tvNik.text = "NIK: ${user.nik}"
        binding.tvName.text = user.nama
        binding.tvStatus.text = if(user.wajahVerified == true) STATUS_TRUE else STATUS_FALSE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                val usersPreference = UsersPreference(this)
                usersPreference.clearUser()
                Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AuthActivity::class.java))
                return true
            }
            else -> return true
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_ubah_profil -> {
                startActivity(Intent(this@MainActivity, EditProfileActivity::class.java))
            }
            R.id.btn_verifikasi -> {
                startActivity(Intent(this@MainActivity, VerificationActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onGetSuccess(ktpVerified: Boolean) {
        if(!ktpVerified) {
            startActivity(Intent(this@MainActivity, KtpVerificationActivity::class.java))
        } else return
    }

    override fun onGetError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val STATUS_TRUE = "Wajah terverifikasi"
        private const val STATUS_FALSE = "Wajah tidak terverifikasi"
        private const val STATUS_NULL = "Wajah belum diverifikasi"
    }

}