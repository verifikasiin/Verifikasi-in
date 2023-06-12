package com.example.verifikasiin.ui.auth

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.verifikasiin.R
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.data.UsersPreference
import com.example.verifikasiin.databinding.FragmentLoginBinding
import com.example.verifikasiin.ui.ViewModelFactory
import com.example.verifikasiin.ui.main.MainActivity


class LoginFragment : Fragment(), View.OnClickListener, LoginViewModel.LoginCallback {

    private lateinit var loginBinding: FragmentLoginBinding
    private lateinit var user : UserModel
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(requireContext().applicationContext as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAuth()
        loginViewModel.loading.observe(requireActivity()){
            showLoading(it)
        }
        loginBinding.btnMasuk.setOnClickListener(this)
        loginBinding.btnDaftar.setOnClickListener(this)
        loginViewModel.loginCallback = this
    }



    private fun checkAuth() {
        val userPreferences = UsersPreference(requireContext())
        user = userPreferences.getUser()
        if(user.nik != "") startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private fun showLoading(loading : Boolean) {
        loginBinding.loading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_masuk -> {
                showNIKError()
                showPasswordError()
                if(showNIKError() &&  showPasswordError()) {
                    loginViewModel.login(loginBinding.edtNik.text.toString(), loginBinding.edtPassword.text.toString())
                }
            }
            R.id.btn_daftar -> {
                val registerFragment = RegisterFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().apply {
                    replace(R.id.frame_container, registerFragment, RegisterFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    fun showNIKError() : Boolean {
        if(loginBinding.edtNik.text?.isNullOrEmpty() == true){
            loginBinding.edtNik.error = "Masukkan NIK"
            return false
        }
        return true
    }
    fun showPasswordError() : Boolean{
        if(loginBinding.edtPassword.text?.isNullOrEmpty() == true){
            loginBinding.edtPassword.error = "Masukkan Password"
            return false
        }
        return true

    }

    override fun onLoginSuccess() {
        Log.e(TAG, "Test")
        Toast.makeText(requireContext(), "Berhasil masuk", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    override fun onLoginError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}