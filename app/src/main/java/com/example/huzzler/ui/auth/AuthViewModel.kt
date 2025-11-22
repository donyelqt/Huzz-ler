package com.example.huzzler.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.User
import com.example.huzzler.data.repository.auth.AuthRepository
import com.example.huzzler.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun signUp(fullName: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (password != confirmPassword) {
                _authState.value = AuthState.Error("Passwords do not match")
                return@launch
            }

            _authState.value = AuthState.Loading
            
            // Simulate API call
            // Mock validation
            val authResult = authRepository.signUp(email, password)
            if (authResult.isFailure) {
                val throwable = authResult.exceptionOrNull()
                _authState.value = AuthState.Error(throwable?.message ?: "Sign up failed")
                return@launch
            }

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _authState.value = AuthState.Error("Failed to create user profile")
                return@launch
            }

            val userProfile = User(
                id = currentUser.uid,
                email = currentUser.email ?: email,
                name = if (fullName.isNotBlank()) fullName else (currentUser.displayName ?: ""),
                points = 0,
                streak = 0,
                primeRate = 0,
                rank = "Scholar"
            )

            val profileResult = userRepository.upsertUserProfile(userProfile)
            _authState.value = profileResult.fold(
                onSuccess = { AuthState.Success },
                onFailure = { throwable ->
                    AuthState.Error(throwable.message ?: "Account created, but failed to save profile")
                }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Simulate API call
            // Mock validation
            val result = authRepository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { throwable ->
                    AuthState.Error(throwable.message ?: "Invalid credentials")
                }
            )
        }
    }

    fun checkCurrentUser() {
        val currentUser = authRepository.getCurrentUser()
        _authState.value = if (currentUser != null) {
            AuthState.Success
        } else {
            AuthState.Idle
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.signInWithGoogle(idToken)
            if (result.isFailure) {
                val throwable = result.exceptionOrNull()
                _authState.value = AuthState.Error(throwable?.message ?: "Google sign-in failed")
                return@launch
            }

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _authState.value = AuthState.Error("Google sign-in failed: no user")
                return@launch
            }

            val userId = currentUser.uid
            val profileResult = userRepository.getUserProfile(userId)

            val userProfile = profileResult.getOrNull() ?: User(
                id = userId,
                email = currentUser.email ?: "",
                name = currentUser.displayName ?: "",
                points = 0,
                streak = 0,
                primeRate = 0,
                rank = "Scholar"
            )

            val saveResult = if (profileResult.getOrNull() == null) {
                userRepository.upsertUserProfile(userProfile)
            } else {
                Result.success(Unit)
            }

            _authState.value = saveResult.fold(
                onSuccess = { AuthState.Success },
                onFailure = { throwable ->
                    AuthState.Error(throwable.message ?: "Signed in with Google, but failed to load profile")
                }
            )
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
