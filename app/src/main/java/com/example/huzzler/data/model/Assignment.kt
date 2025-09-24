package com.example.huzzler.data.model

import java.util.Date

data class Assignment(
    val id: String,
    val title: String,
    val course: String,
    val points: Int,
    val dueDate: Date,
    val timeLeft: String,
    val priority: AssignmentPriority,
    val status: AssignmentStatus = AssignmentStatus.PENDING
)

enum class AssignmentPriority {
    PRIME, GOTTA_DO, MEDIUM, LOW
}

enum class AssignmentStatus {
    PENDING, IN_PROGRESS, COMPLETED
}
