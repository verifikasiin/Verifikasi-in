package com.example.verifikasiin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.verifikasiin.R
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.ActivityMainBinding
import com.example.verifikasiin.ui.auth.AuthActivity
import com.example.verifikasiin.ui.edit.EditProfileActivity
import com.example.verifikasiin.ui.verifikasi.VerificationActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUbahProfil.setOnClickListener(this)
        binding.btnVerifikasi.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
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
                Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
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



}