package com.example.verifikasiin.ui.verifikasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class VerificationViewModel : ViewModel() {
    private var _file = MutableLiveData<File>()
    var file : LiveData<File> = _file


}