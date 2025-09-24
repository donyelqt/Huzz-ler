package com.example.huzzler.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.huzzler.MainActivity
import com.example.huzzler.R
import com.example.huzzler.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupValidation()
    }

    private fun setupUI() {
        binding.apply {
            // Sign in redirect
            tvSignIn.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }

            // Sign up button
            btnSignUp.setOnClickListener {
                if (validateForm()) {
                    viewModel.signUp(
                        email = etEmail.text.toString().trim(),
                        password = etPassword.text.toString(),
                        confirmPassword = etConfirmPassword.text.toString(),
                        verificationCode = etVerificationCode.text.toString()
                    )
                }
            }

            // Send verification code
            btnSendCode.setOnClickListener {
                val email = etEmail.text.toString().trim()
                if (isValidEmail(email)) {
                    viewModel.sendVerificationCode(email)
                    btnSendCode.text = "Code Sent"
                    btnSendCode.isEnabled = false
                    btnSendCode.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.gray_light))
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.btnSignUp.isEnabled = false
                    binding.btnSignUp.text = "Signing up..."
                }
                is AuthState.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.text = "Sign up"
                    // Show error message
                }
                else -> {
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.text = "Sign up"
                }
            }
        }
    }

    private fun setupValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.apply {
            etEmail.addTextChangedListener(textWatcher)
            etPassword.addTextChangedListener(textWatcher)
            etConfirmPassword.addTextChangedListener(textWatcher)
            etVerificationCode.addTextChangedListener(textWatcher)
        }
    }

    private fun validateForm(): Boolean {
        binding.apply {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val verificationCode = etVerificationCode.text.toString()

            var isValid = true

            // Email validation
            if (!isValidEmail(email)) {
                tilEmail.error = "Please enter a valid email"
                isValid = false
            } else {
                tilEmail.error = null
            }

            // Password validation
            if (password.length < 6) {
                tilPassword.error = "Password must be at least 6 characters"
                isValid = false
            } else {
                tilPassword.error = null
            }

            // Confirm password validation
            if (password != confirmPassword) {
                tilConfirmPassword.error = "Passwords do not match"
                isValid = false
            } else {
                tilConfirmPassword.error = null
            }

            // Verification code validation
            if (verificationCode.isEmpty()) {
                tilVerificationCode.error = "Please enter verification code"
                isValid = false
            } else {
                tilVerificationCode.error = null
            }

            return isValid
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
