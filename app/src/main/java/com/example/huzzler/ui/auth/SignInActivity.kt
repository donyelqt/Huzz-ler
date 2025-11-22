package com.example.huzzler.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.huzzler.MainActivity
import com.example.huzzler.R
import com.example.huzzler.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGoogleSignIn()
        setupUI()
        setupObservers()
        viewModel.checkCurrentUser()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupUI() {
        binding.apply {
            // Google sign in (custom MaterialButton with Google logo)
            btnSignInCanvas.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }

            // Email sign in
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                if (validateForm(email, password)) {
                    viewModel.signIn(email, password)
                }
            }

            // Password toggle functionality
            ivPasswordToggle.setOnClickListener {
                togglePasswordVisibility()
            }

            // Sign up redirect
            tvSignUp.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }
        }
    }

    private fun togglePasswordVisibility() {
        binding.apply {
            if (etPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_view)
            } else {
                // Show password
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            }
            // Keep cursor at the end
            etPassword.setSelection(etPassword.text.length)
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
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            binding.etPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
