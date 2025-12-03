package com.example.huzzler.data.model

import java.util.Date
import java.util.UUID

/**
 * Represents an AI-generated study plan
 */
data class StudyPlan(
    val id: String = UUID.randomUUID().toString(),
    val generatedAt: Date = Date(),
    val sessions: List<StudySession> = emptyList(),
    val totalEstimatedHours: Float = 0f,
    val aiInsights: String = ""
)

/**
 * Individual study session in the plan
 */
data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val assignmentId: String,
    val assignmentTitle: String,
    val suggestedDate: Date,
    val suggestedTimeSlot: TimeSlot,
    val durationMinutes: Int,
    val priority: StudyPriority,
    val reason: String // AI explanation for why this time
)

enum class TimeSlot(val displayName: String, val startHour: Int, val endHour: Int) {
    EARLY_MORNING("Early Morning", 6, 9),
    MORNING("Morning", 9, 12),
    AFTERNOON("Afternoon", 12, 17),
    EVENING("Evening", 17, 21),
    NIGHT("Night", 21, 24)
}

enum class StudyPriority {
    URGENT,      // Due very soon
    HIGH,        // Important/difficult
    MEDIUM,      // Regular priority
    LOW          // Can be flexible
}
