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
import androidx.lifecycle.ViewModel
import com.example.verifikasiin.R
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.ActivityVerificationBinding
import com.example.verifikasiin.network.response.GetUserByIDResponse
import com.example.verifikasiin.ui.ViewModelFactory
import com.example.verifikasiin.ui.camera.CameraActivity
import com.example.verifikasiin.ui.main.MainActivity
import com.example.verifikasiin.util.reduceImageSize
import com.example.verifikasiin.util.rotateFile
import java.io.File

class VerificationActivity : AppCompatActivity(), View.OnClickListener, VerificationViewModel.GetUserCallback, VerificationViewModel.FaceVerificationCallback, VerificationViewModel.RefreshTokenCallback, VerificationViewModel.UpdateUserCallback {

    private lateinit var verificationBinding : ActivityVerificationBinding
    private var getFile : File? = null
    private var getFile2 : String? = null
    private val verificationViewModel by viewModels<VerificationViewModel>{
        ViewModelFactory(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verificationBinding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(verificationBinding.root)
        verificationBinding.btnCamera.setOnClickListener(this)
        verificationBinding.btnUpload.setOnClickListener(this)

        verificationViewModel.verificationCallback = this
        verificationViewModel.updateUserCallback = this
        verificationViewModel.getUserCallback = this
        verificationViewModel.refreshTokenCallback = this

        supportActionBar?.title = TITLE

        verificationViewModel.file.observe(this) {
            setPhotoFile(it)
        }

        verificationViewModel.file2.observe(this){
            setPhotoFile2(it)
        }

        verificationViewModel.loading.observe(this) {
            showLoading(it)
        }

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun showLoading(loading : Boolean) {
        verificationBinding.loading.visibility = if (loading) View.VISIBLE else View.GONE
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
                verificationViewModel.setPhotoFile(file)
            }
        }
    }

    fun setPhotoFile(file: File) {
        verificationBinding.ivVerificationPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
        getFile = file
    }

    fun setPhotoFile2(url: String) {
        getFile2 = url
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
        private const val TAG = "VerificationActivity"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TITLE = "Face Verification"
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_camera -> {
                startCameraX()
            }
            R.id.btn_upload -> {
                if(getFile != null && !getFile2.isNullOrEmpty()) {
                    verificationViewModel.verify(getFile!!, getFile2!!)
                } else {
                    if(getFile == null) {
                        Toast.makeText(this, "File1 is missing", Toast.LENGTH_SHORT).show()
                    }
                    if(getFile2 == null) {
                        Toast.makeText(this, "File2 is missing", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    override fun onUpdateSuccess() {
        startActivity(Intent(this@VerificationActivity, MainActivity::class.java))
    }

    override fun onUpdateError(errorMessage: String) {
        Toast.makeText(this, "Gagal update user: ${errorMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun onGetSuccess(url: String, ktpVerified: Boolean) {
        if(ktpVerified){
            setPhotoFile2(url.substring(30))
        } else {
            startActivity(Intent(this@VerificationActivity, KtpVerificationActivity::class.java))
        }
    }

    override fun onGetError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onRefreshSuccess(token: String) {
        val usersPreference = UsersPreference(this)
        usersPreference.updateToken(token)
    }

    override fun onRefreshError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onVerifSuccess(nik: String, b: Boolean) {
        Toast.makeText(this, "Hasil verifikasi : ${b.toString()}", Toast.LENGTH_LONG).show()
        val request = GetUserByIDResponse(
            nik = nik,
            wajahVerified = b
        )
        verificationViewModel.updateUser(nik, request)
    }

    override fun onVerifFailed(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}