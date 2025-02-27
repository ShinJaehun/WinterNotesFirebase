package com.shinjaehun.winternotesfirebase.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shinjaehun.winternotesfirebase.R
import com.shinjaehun.winternotesfirebase.common.makeToast
import com.shinjaehun.winternotesfirebase.databinding.FragmentLoginBinding

class LoginView: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            LoginInjector(requireActivity().application).provideLoginViewModelFactory()
        ).get(LoginViewModel::class.java)

        setUpClickListener()
        observeViewModel()

        viewModel.handleEvent(LoginEvent.OnStart)
    }

    private fun setUpClickListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                makeToast("email and password must note be empty")
                return@setOnClickListener
            }

            viewModel.handleEvent(LoginEvent.OnLoginButtonClick(email, password))
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(
            viewLifecycleOwner,
            Observer { user ->
                if (user != null) {
                    findNavController().navigate(R.id.noteListView)
                }
            }
        )
    }
}