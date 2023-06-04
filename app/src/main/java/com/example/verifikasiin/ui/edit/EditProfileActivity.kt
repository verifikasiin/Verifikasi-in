package com.example.verifikasiin.ui.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.verifikasiin.R
import com.example.verifikasiin.databinding.ActivityEditProfileBinding
import com.example.verifikasiin.databinding.ActivityMainBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileBinding : ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
    }

}