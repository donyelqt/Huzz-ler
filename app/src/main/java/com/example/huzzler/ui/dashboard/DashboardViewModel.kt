package com.example.huzzler.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.AssignmentPriority
import com.example.huzzler.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _assignments = MutableLiveData<List<Assignment>>()
    val assignments: LiveData<List<Assignment>> = _assignments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Simulate API call
            delay(1000)
            
            // Mock user data
            _user.value = User(
                id = "1",
                email = "daa6681@students.uc-bcf.edu.ph",
                name = "Doniele Arys",
                points = 1240,
                streak = 3,
                primeRate = 87
            )
            
            // Mock assignments data
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 2)
            val dueDate1 = calendar.time
            
            calendar.add(Calendar.DAY_OF_MONTH, -5)
            val dueDate2 = calendar.time
            
            _assignments.value = listOf(
                Assignment(
                    id = "1",
                    title = "Binary Search Tree Implementation",
                    course = "CS 250 - Data Structures",
                    points = 150,
                    dueDate = dueDate1,
                    timeLeft = "36h left",
                    priority = AssignmentPriority.PRIME
                ),
                Assignment(
                    id = "2",
                    title = "Database Design Project",
                    course = "CS 250 - Data Structures",
                    points = 200,
                    dueDate = dueDate2,
                    timeLeft = "12h left",
                    priority = AssignmentPriority.GOTTA_DO
                )
            )
            
            _isLoading.value = false
        }
    }

    fun onAssignmentClicked(assignment: Assignment) {
        // Handle assignment click - could navigate to detail screen
    }
}
