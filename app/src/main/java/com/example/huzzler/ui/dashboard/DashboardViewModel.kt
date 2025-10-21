package com.example.huzzler.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.AssignmentCategory
import com.example.huzzler.data.model.AssignmentDifficulty
import com.example.huzzler.data.model.AssignmentPriority
import com.example.huzzler.data.model.AssignmentStatus
import com.example.huzzler.data.model.SubmissionType
import com.example.huzzler.data.model.Notification
import com.example.huzzler.data.model.NotificationType
import com.example.huzzler.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Sealed interface for one-time dashboard events
 * Following event-driven architecture best practices
 */
sealed interface DashboardEvent {
    data class AssignmentCompleted(
        val assignmentTitle: String,
        val pointsEarned: Int,
        val totalPoints: Int
    ) : DashboardEvent
    
    data class NavigateToDetail(
        val assignment: Assignment
    ) : DashboardEvent
    
    data class NavigateToNotifications(
        val notifications: List<Notification>
    ) : DashboardEvent
    
    data class ShowError(
        val message: String
    ) : DashboardEvent
}

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _assignments = MutableLiveData<List<Assignment>>()
    val assignments: LiveData<List<Assignment>> = _assignments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // SharedFlow for one-time events (navigation, snackbars)
    private val _events = MutableSharedFlow<DashboardEvent>(replay = 0)
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()
    
    // Store notifications for demo
    private val mockNotifications = mutableListOf<Notification>()
    
    // LiveData for notifications (observable for NotificationsFragment)
    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications
    
    // Method to mark notification as read
    fun markNotificationAsRead(notificationId: String) {
        val index = mockNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            mockNotifications[index] = mockNotifications[index].copy(isRead = true)
            _notifications.value = mockNotifications.toList()
        }
    }

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
            
            calendar.add(Calendar.DAY_OF_MONTH, 5)
            val dueDate3 = calendar.time
            
            _assignments.value = listOf(
                Assignment(
                    id = "1",
                    title = "Binary Search Tree Implementation",
                    course = "CS 250 - Data Structures",
                    points = 150,
                    dueDate = dueDate1,
                    timeLeft = "36h",
                    priority = AssignmentPriority.PRIME,
                    difficulty = AssignmentDifficulty.MEDIUM,
                    category = AssignmentCategory.GAMING,
                    status = AssignmentStatus.PENDING,
                    submissionType = SubmissionType.REQUIRES_SUBMISSION
                ),
                Assignment(
                    id = "2",
                    title = "Database Design Project",
                    course = "CS 250 - Data Structures",
                    points = 200,
                    dueDate = dueDate2,
                    timeLeft = "12h",
                    priority = AssignmentPriority.GOTTA_DO,
                    difficulty = AssignmentDifficulty.MEDIUM,
                    category = AssignmentCategory.ACADEMIC,
                    status = AssignmentStatus.PENDING,
                    submissionType = SubmissionType.REQUIRES_SUBMISSION
                ),
                Assignment(
                    id = "3",
                    title = "Research Paper: AI Ethics in Gaming",
                    course = "CS 310 - Software Engineering",
                    points = 250,
                    dueDate = dueDate3,
                    timeLeft = "5d",
                    priority = AssignmentPriority.PRIME,
                    difficulty = AssignmentDifficulty.HARD,
                    category = AssignmentCategory.PRODUCTIVITY,
                    status = AssignmentStatus.PENDING,
                    submissionType = SubmissionType.REQUIRES_SUBMISSION
                )
            )
            
            // Generate mock notifications
            generateMockNotifications()
            
            _isLoading.value = false
        }
    }

    /**
     * Handle assignment card click - navigate to detail screen
     */
    fun onAssignmentClicked(assignment: Assignment) {
        viewModelScope.launch {
            _events.emit(
                DashboardEvent.NavigateToDetail(assignment)
            )
        }
    }
    
    /**
     * Handle notification bell click
     */
    fun onNotificationClicked() {
        viewModelScope.launch {
            _events.emit(
                DashboardEvent.NavigateToNotifications(mockNotifications)
            )
        }
    }
    
    /**
     * Complete assignment with validation and user feedback
     */
    fun completeAssignment(assignment: Assignment) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch
            val currentAssignments = _assignments.value ?: return@launch
            
            // Check if already completed
            if (assignment.status == AssignmentStatus.COMPLETED) {
                _events.emit(
                    DashboardEvent.ShowError("This assignment is already completed!")
                )
                return@launch
            }
            
            // Update assignment status
            val updatedAssignments = currentAssignments.map { 
                if (it.id == assignment.id) {
                    it.copy(status = AssignmentStatus.COMPLETED)
                } else {
                    it
                }
            }
            
            // Update user points
            val newTotalPoints = currentUser.points + assignment.points
            val updatedUser = currentUser.copy(points = newTotalPoints)
            
            // Update state
            _assignments.value = updatedAssignments
            _user.value = updatedUser
            
            // Emit success event for snackbar
            _events.emit(
                DashboardEvent.AssignmentCompleted(
                    assignmentTitle = assignment.title,
                    pointsEarned = assignment.points,
                    totalPoints = newTotalPoints
                )
            )
            
            // Add notification for completion
            val completionNotification = Notification(
                id = "notif_${System.currentTimeMillis()}",
                type = NotificationType.POINTS_EARNED,
                title = "Assignment Completed!",
                message = "You earned ${assignment.points} points for completing ${assignment.title}",
                timestamp = Date(),
                isRead = false
            )
            mockNotifications.add(0, completionNotification)
        }
    }
    
    private fun generateMockNotifications() {
        mockNotifications.clear()
        
        val calendar = Calendar.getInstance()
        
        // Notification 1: Due soon
        calendar.add(Calendar.MINUTE, -30)
        mockNotifications.add(
            Notification(
                id = "1",
                type = NotificationType.ASSIGNMENT_DUE_SOON,
                title = "Assignment Due Soon",
                message = "Binary Search Tree Implementation is due in 36 hours",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Notification 2: Streak milestone
        calendar.add(Calendar.HOUR, -2)
        mockNotifications.add(
            Notification(
                id = "2",
                type = NotificationType.STREAK_MILESTONE,
                title = "🔥 3 Day Streak!",
                message = "You're on fire! Keep up the great work to maintain your streak.",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Notification 3: New reward
        calendar.add(Calendar.HOUR, -5)
        mockNotifications.add(
            Notification(
                id = "3",
                type = NotificationType.NEW_REWARD_AVAILABLE,
                title = "New Reward Unlocked",
                message = "You can now redeem the MLBB Battle Pass with your points!",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 4: Points earned
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        mockNotifications.add(
            Notification(
                id = "4",
                type = NotificationType.POINTS_EARNED,
                title = "Points Earned",
                message = "You earned 50 bonus points for early submission!",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 5: Assignment graded
        calendar.add(Calendar.DAY_OF_MONTH, -2)
        mockNotifications.add(
            Notification(
                id = "5",
                type = NotificationType.ASSIGNMENT_GRADED,
                title = "Assignment Graded",
                message = "Your Web Development Project has been graded. Score: 95/100",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 6: Overdue assignment
        calendar.add(Calendar.HOUR, -6)
        mockNotifications.add(
            Notification(
                id = "6",
                type = NotificationType.ASSIGNMENT_OVERDUE,
                title = "⚠️ Assignment Overdue",
                message = "Your Data Structures Quiz is now overdue. Submit as soon as possible!",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Notification 7: Points milestone
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        mockNotifications.add(
            Notification(
                id = "7",
                type = NotificationType.POINTS_EARNED,
                title = "🎉 Points Milestone!",
                message = "Congratulations! You've reached 1,000 total points.",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 8: New Valorant reward
        calendar.add(Calendar.HOUR, -8)
        mockNotifications.add(
            Notification(
                id = "8",
                type = NotificationType.NEW_REWARD_AVAILABLE,
                title = "New Gaming Reward",
                message = "Valorant VP 500 is now available in the rewards store!",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Notification 9: Streak reminder
        calendar.add(Calendar.DAY_OF_MONTH, -4)
        mockNotifications.add(
            Notification(
                id = "9",
                type = NotificationType.STREAK_MILESTONE,
                title = "🔥 7 Day Streak Achievement!",
                message = "Amazing! You've maintained a 7-day study streak. Keep it going!",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 10: Assignment due today
        calendar.add(Calendar.HOUR, -12)
        mockNotifications.add(
            Notification(
                id = "10",
                type = NotificationType.ASSIGNMENT_DUE_SOON,
                title = "Due Today",
                message = "Mobile App Development Assignment is due in 6 hours!",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Notification 11: Bonus points
        calendar.add(Calendar.DAY_OF_MONTH, -5)
        mockNotifications.add(
            Notification(
                id = "11",
                type = NotificationType.POINTS_EARNED,
                title = "Bonus Points Earned",
                message = "You earned 100 bonus points for completing all Prime assignments this week!",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 12: System update
        calendar.add(Calendar.DAY_OF_MONTH, -6)
        mockNotifications.add(
            Notification(
                id = "12",
                type = NotificationType.SYSTEM_UPDATE,
                title = "App Update Available",
                message = "Version 2.5.0 is available with new features and improvements.",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 13: Another graded assignment
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        mockNotifications.add(
            Notification(
                id = "13",
                type = NotificationType.ASSIGNMENT_GRADED,
                title = "Perfect Score!",
                message = "Your Algorithm Analysis assignment received 100/100. Excellent work!",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 14: Academic reward unlocked
        calendar.add(Calendar.DAY_OF_MONTH, -8)
        mockNotifications.add(
            Notification(
                id = "14",
                type = NotificationType.NEW_REWARD_AVAILABLE,
                title = "Academic Reward Unlocked",
                message = "Premium Study Materials bundle is now available for redemption.",
                timestamp = calendar.time,
                isRead = true
            )
        )
        
        // Notification 15: Urgent deadline
        calendar.add(Calendar.MINUTE, -15)
        mockNotifications.add(
            Notification(
                id = "15",
                type = NotificationType.ASSIGNMENT_DUE_SOON,
                title = "⏰ Final Reminder",
                message = "Machine Learning Project is due in 2 hours. Submit now!",
                timestamp = calendar.time,
                isRead = false
            )
        )
        
        // Update LiveData
        _notifications.value = mockNotifications.toList()
    }
}
