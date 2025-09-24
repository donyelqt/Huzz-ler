package com.example.huzzler.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.huzzler.MainActivity
import com.example.huzzler.databinding.ActivitySignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.apply {
            // Canvas sign in
            btnSignInCanvas.setOnClickListener {
                viewModel.signInWithCanvas()
            }

            // Email sign in
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                if (validateForm(email, password)) {
                    viewModel.signIn(email, password)
                }
            }

            // Sign up redirect
            tvSignUp.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.btnSignIn.isEnabled = false
                    binding.btnSignIn.text = "Signing in..."
                    binding.btnSignInCanvas.isEnabled = false
                }
                is AuthState.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.text = "Sign in"
                    binding.btnSignInCanvas.isEnabled = true
                    // Show error message
                }
                else -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.text = "Sign in"
                    binding.btnSignInCanvas.isEnabled = true
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true

        if (!isValidEmail(email)) {
            binding.tilEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Please enter your password"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
