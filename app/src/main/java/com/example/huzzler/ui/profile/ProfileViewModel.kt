package com.example.huzzler.ui.profile

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
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loadUserProfile() {
        viewModelScope.launch {
            // Simulate API call
            delay(500)
            
            // Mock user data
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser == null) {
                _user.value = User()
                return@launch
            }

            val userId = firebaseUser.uid
            val result = userRepository.getUserProfile(userId)

            val loadedUser = result.fold(
                onSuccess = { existing ->
                    existing ?: User(
                        id = userId,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: "",
                        points = 0,
                        streak = 0,
                        primeRate = 0,
                        rank = "Scholar"
                    )
                },
                onFailure = {
                    User(
                        id = userId,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: "",
                        points = 0,
                        streak = 0,
                        primeRate = 0,
                        rank = "Scholar"
                    )
                }
            )

            _user.value = loadedUser

            // Ensure profile exists in Firestore if it was missing
            if (result.getOrNull() == null) {
                userRepository.upsertUserProfile(loadedUser)
            }
        }
    }
    
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            // Simulate API call
            delay(300)
            
            // Update user name
            val current = _user.value
            if (current != null && current.id.isNotBlank()) {
                userRepository.updateUserName(current.id, newName)
                _user.value = current.copy(name = newName)
            } else {
                _user.value = current?.copy(name = newName)
            }
        }
    }

    fun logout() {
        authRepository.signOut()
        _user.value = User()
    }
}
