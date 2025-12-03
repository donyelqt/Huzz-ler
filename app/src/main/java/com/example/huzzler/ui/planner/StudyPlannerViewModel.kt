package com.example.huzzler.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.StudyPlan
import com.example.huzzler.data.model.StudyPriority
import com.example.huzzler.data.model.StudySession
import com.example.huzzler.data.model.TimeSlot
import com.example.huzzler.data.repository.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class PlannerUiState(
    val isLoading: Boolean = false,
    val studyPlan: StudyPlan? = null,
    val error: String? = null,
    val assignments: List<Assignment> = emptyList()
)

@HiltViewModel
class StudyPlannerViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    fun generateStudyPlan(assignments: List<Assignment>) {
        if (assignments.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = "No assignments to plan for!"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                assignments = assignments
            )

            try {
                // Generate smart study plan based on assignments
                val studyPlan = createSmartStudyPlan(assignments)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    studyPlan = studyPlan
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to generate study plan: ${e.message}"
                )
            }
        }
    }

    private fun createSmartStudyPlan(assignments: List<Assignment>): StudyPlan {
        val sessions = mutableListOf<StudySession>()
        val calendar = Calendar.getInstance()
        
        // Sort assignments by due date and priority
        val sortedAssignments = assignments.sortedWith(
            compareBy<Assignment> { it.dueDate }
                .thenByDescending { it.priority.ordinal }
                .thenByDescending { it.difficulty.ordinal }
        )

        var totalHours = 0f

        sortedAssignments.forEachIndexed { index, assignment ->
            val daysUntilDue = calculateDaysUntilDue(assignment.dueDate)
            val estimatedMinutes = estimateStudyTime(assignment)
            val sessionsNeeded = (estimatedMinutes / 25).coerceAtLeast(1) // Pomodoro sessions
            
            // Distribute sessions across available days
            val sessionsPerDay = if (daysUntilDue > 0) {
                (sessionsNeeded / daysUntilDue).coerceAtLeast(1)
            } else {
                sessionsNeeded
            }

            repeat(sessionsNeeded.coerceAtMost(4)) { sessionIndex ->
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, sessionIndex)
                
                val timeSlot = suggestTimeSlot(index, sessionIndex)
                val priority = calculatePriority(assignment, daysUntilDue)
                
                sessions.add(
                    StudySession(
                        id = UUID.randomUUID().toString(),
                        assignmentId = assignment.id,
                        assignmentTitle = assignment.title,
                        suggestedDate = calendar.time,
                        suggestedTimeSlot = timeSlot,
                        durationMinutes = 25, // Pomodoro
                        priority = priority,
                        reason = generateReason(assignment, timeSlot, daysUntilDue)
                    )
                )
            }
            
            totalHours += estimatedMinutes / 60f
        }

        // Sort sessions by date and time
        val sortedSessions = sessions.sortedWith(
            compareBy<StudySession> { it.suggestedDate }
                .thenBy { it.suggestedTimeSlot.startHour }
        )

        return StudyPlan(
            sessions = sortedSessions,
            totalEstimatedHours = totalHours,
            aiInsights = generateInsights(assignments, sortedSessions)
        )
    }


    private fun calculateDaysUntilDue(dueDate: Date): Int {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance().apply { time = dueDate }
        val diffMillis = due.timeInMillis - now.timeInMillis
        return (diffMillis / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
    }

    private fun estimateStudyTime(assignment: Assignment): Int {
        // Base time based on difficulty
        val baseMinutes = when (assignment.difficulty) {
            com.example.huzzler.data.model.AssignmentDifficulty.EASY -> 30
            com.example.huzzler.data.model.AssignmentDifficulty.MEDIUM -> 60
            com.example.huzzler.data.model.AssignmentDifficulty.HARD -> 120
        }
        
        // Adjust based on points (higher points = more work)
        val pointsMultiplier = (assignment.points / 100f).coerceIn(0.5f, 2f)
        
        return (baseMinutes * pointsMultiplier).toInt()
    }

    private fun suggestTimeSlot(assignmentIndex: Int, sessionIndex: Int): TimeSlot {
        // Distribute across different time slots for variety
        val slots = TimeSlot.values()
        return slots[(assignmentIndex + sessionIndex) % slots.size]
    }

    private fun calculatePriority(assignment: Assignment, daysUntilDue: Int): StudyPriority {
        return when {
            daysUntilDue <= 1 -> StudyPriority.URGENT
            daysUntilDue <= 3 && assignment.priority == com.example.huzzler.data.model.AssignmentPriority.PRIME -> StudyPriority.URGENT
            daysUntilDue <= 5 -> StudyPriority.HIGH
            assignment.difficulty == com.example.huzzler.data.model.AssignmentDifficulty.HARD -> StudyPriority.HIGH
            else -> StudyPriority.MEDIUM
        }
    }

    private fun generateReason(assignment: Assignment, timeSlot: TimeSlot, daysUntilDue: Int): String {
        return when {
            daysUntilDue <= 1 -> "⚠️ Due very soon! Focus on this first."
            daysUntilDue <= 3 -> "📅 Due in $daysUntilDue days. ${timeSlot.displayName} is great for focused work."
            assignment.difficulty == com.example.huzzler.data.model.AssignmentDifficulty.HARD -> 
                "🧠 Complex task - ${timeSlot.displayName} offers peak mental clarity."
            timeSlot == TimeSlot.MORNING -> "☀️ Morning sessions boost productivity by 20%!"
            timeSlot == TimeSlot.EVENING -> "🌙 Evening review helps memory consolidation."
            else -> "📚 Consistent study sessions lead to better retention."
        }
    }

    private fun generateInsights(assignments: List<Assignment>, sessions: List<StudySession>): String {
        val urgentCount = sessions.count { it.priority == StudyPriority.URGENT }
        val totalSessions = sessions.size
        
        return buildString {
            append("📊 Your personalized study plan:\n\n")
            
            if (urgentCount > 0) {
                append("🔴 $urgentCount urgent session(s) need immediate attention\n")
            }
            
            append("📝 $totalSessions total Pomodoro sessions planned\n")
            append("⏱️ Each session is 25 minutes of focused work\n\n")
            
            append("💡 Pro tip: Complete morning sessions first for maximum productivity!")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
