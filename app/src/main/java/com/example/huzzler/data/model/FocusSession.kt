package com.example.huzzler.data.model

import java.util.Date
import java.util.UUID

/**
 * Represents a Pomodoro focus session
 */
data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val assignmentId: String? = null,
    val assignmentTitle: String? = null,
    val durationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val status: FocusSessionStatus = FocusSessionStatus.NOT_STARTED,
    val pointsEarned: Int = 0,
    val sessionsCompleted: Int = 0
)

enum class FocusSessionStatus {
    NOT_STARTED,
    FOCUSING,
    ON_BREAK,
    PAUSED,
    COMPLETED,
    CANCELLED
}

/**
 * Timer state for UI
 */
data class TimerState(
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isBreak: Boolean = false,
    val sessionCount: Int = 0,
    val progress: Float = 1f // 1f = full, 0f = empty
)
