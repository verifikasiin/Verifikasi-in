package com.example.verifikasiin.ui.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.example.verifikasiin.R
import com.example.verifikasiin.databinding.ActivityEditProfileBinding
import com.example.verifikasiin.databinding.ActivityMainBinding
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.ViewModelFactory

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileBinding : ActivityEditProfileBinding
    private val editProfileViewModel by viewModels<EditProfileViewModel> {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)

        editProfileViewModel.user.observe(this) {
            setUserData(it)
        }
    }

    private fun setUserData(user : GetUserByIDResponse) {
        editProfileBinding.edtNik.setText(user.nik)
    }


}