package com.example.verifikasiin.ui.auth

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.verifikasiin.R
import com.example.verifikasiin.data.SessionManager
import com.example.verifikasiin.data.UserModel
import com.example.verifikasiin.databinding.FragmentRegisterBinding
import com.example.verifikasiin.ui.ViewModelFactory

class RegisterFragment : Fragment(), View.OnClickListener, RegisterViewModel.RegisterCallback {

    private lateinit var registerBinding : FragmentRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory(requireContext().applicationContext as Application)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return registerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModel.registerCallback = this
        registerViewModel.loading.observe(requireActivity()) {
            showLoading(it)
        }
        registerBinding.btnDaftar.setOnClickListener(this)
        registerBinding.btnMasuk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_masuk -> {
                redirectToLoginPage()
            }
            R.id.btn_daftar -> {
                registerViewModel.register(registerBinding.edtNik.text.toString(), registerBinding.edtPassword.text.toString(), registerBinding.edtConfirmPassword.text.toString())
            }
        }
    }

    private fun showLoading(loading : Boolean) {
        registerBinding.loading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun redirectToLoginPage() {
        val loginFragment = LoginFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    override fun onRegisterSuccess() {
        Toast.makeText(requireContext(), SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show()
        redirectToLoginPage()
    }

    override fun onRegisterError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegisterFragment"
        private val SUCCESS_MESSAGE = "Pendaftaran berhasil, mengarahkan kembali ke menu login"
    }
}