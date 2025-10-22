package com.example.huzzler.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loadUserProfile() {
        viewModelScope.launch {
            // Simulate API call
            delay(500)
            
            // Mock user data
            _user.value = User(
                id = "1",
                email = "daa6681@students.uc-bcf.edu.ph",
                name = "Doniele Arys",
                points = 1240,
                streak = 3,
                primeRate = 87,
                rank = "Scholar"
            )
        }
    }
    
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            // Simulate API call
            delay(300)
            
            // Update user name
            _user.value = _user.value?.copy(name = newName)
        }
    }
}
