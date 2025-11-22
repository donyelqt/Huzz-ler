package com.example.huzzler.ui.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private val emailErrorView: TextView get() = binding.tvEmailError
    private val passwordErrorView: TextView get() = binding.tvPasswordError
    private val confirmPasswordErrorView: TextView get() = binding.tvConfirmPasswordError

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
                        confirmPassword = etConfirmPassword.text.toString()
                    )
                }
            }

            ivPasswordToggle.setOnClickListener {
                isPasswordVisible = togglePasswordVisibility(etPassword, ivPasswordToggle, isPasswordVisible)
            }

            ivConfirmPasswordToggle.setOnClickListener {
                isConfirmPasswordVisible = togglePasswordVisibility(
                    etConfirmPassword,
                    ivConfirmPasswordToggle,
                    isConfirmPasswordVisible
                )
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
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
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
        }
    }

    private fun validateForm(): Boolean {
        binding.apply {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            var isValid = true

            // Email validation
            if (!isValidEmail(email)) {
                showError(emailErrorView, "Please enter a valid email")
                isValid = false
            } else {
                clearError(emailErrorView)
            }

            // Password validation
            if (password.length < 6) {
                showError(passwordErrorView, "Password must be at least 6 characters")
                isValid = false
            } else {
                clearError(passwordErrorView)
            }

            // Confirm password validation
            if (password != confirmPassword) {
                showError(confirmPasswordErrorView, "Passwords do not match")
                isValid = false
            } else {
                clearError(confirmPasswordErrorView)
            }

            return isValid
        }
    }

    private fun showError(textView: TextView, message: String) {
        textView.text = message
        textView.visibility = View.VISIBLE
    }

    private fun clearError(textView: TextView) {
        textView.text = ""
        textView.visibility = View.GONE
    }

    private fun togglePasswordVisibility(
        editText: EditText,
        toggleView: ImageView,
        currentVisibilityState: Boolean
    ): Boolean {
        val shouldShowPassword = !currentVisibilityState
        editText.inputType = if (shouldShowPassword) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text?.length ?: 0)
        toggleView.setImageResource(
            if (shouldShowPassword) android.R.drawable.ic_menu_close_clear_cancel
            else android.R.drawable.ic_menu_view
        )
        return shouldShowPassword
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
