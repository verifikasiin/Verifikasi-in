package com.example.verifikasiin.ui.verifikasi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.verifikasiin.R
import com.example.verifikasiin.databinding.ActivityKtpVerificationBinding
import com.example.verifikasiin.ui.ViewModelFactory
import com.example.verifikasiin.ui.camera.CameraActivity
import com.example.verifikasiin.util.reduceImageSize
import com.example.verifikasiin.util.rotateFile
import java.io.File

class KtpVerificationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var ktpBinding : ActivityKtpVerificationBinding
    private val ktpVerificationViewModel by viewModels<KtpVerificationViewModel> {
        ViewModelFactory(application)
    }
    private var getFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ktpBinding = ActivityKtpVerificationBinding.inflate(layoutInflater)
        setContentView(ktpBinding.root)

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        ktpVerificationViewModel.file.observe(this) {
            if (it != null) {
                setPhotoFile(it)
            }
        }

        ktpBinding.btnCamera.setOnClickListener(this)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it?.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it?.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                reduceImageSize(file)
                getFile = file
                setPhotoFile(file)
                ktpVerificationViewModel.setPhotoFile(file)
            }
        }
    }

    fun setPhotoFile(file: File) {
        ktpBinding.ivVerificationPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak mendapatkan izin.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "KtpVerificationActivity"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_camera -> {
                startCameraX()
            }
            R.id.btn_verifikasi -> {

            }
        }
    }
}