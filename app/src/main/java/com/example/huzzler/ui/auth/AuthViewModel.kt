package com.example.huzzler.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun signUp(email: String, password: String, confirmPassword: String, verificationCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Simulate API call
            delay(2000)
            
            // Mock validation
            if (password == confirmPassword && verificationCode.isNotEmpty()) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Sign up failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Simulate API call
            delay(1500)
            
            // Mock validation
            if (email.isNotEmpty() && password.isNotEmpty()) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid credentials")
            }
        }
    }

    fun signInWithCanvas() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Simulate Canvas OAuth flow
            delay(2000)
            
            _authState.value = AuthState.Success
        }
    }

    fun sendVerificationCode(email: String) {
        viewModelScope.launch {
            // Simulate sending verification code
            delay(1000)
            // In real implementation, this would call an API
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
