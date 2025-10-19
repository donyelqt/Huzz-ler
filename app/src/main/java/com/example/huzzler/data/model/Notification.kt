package com.example.huzzler.data.model

import java.util.Date

/**
 * Notification data model for dashboard notification system
 * Following 2025 best practices with sealed types and immutability
 */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false,
    val relatedItemId: String? = null,
    val actionLabel: String? = null
)

/**
 * Sealed interface for type-safe notification categories
 * Each type has associated semantic colors and icons
 */
enum class NotificationType {
    ASSIGNMENT_DUE_SOON,      // Orange warning
    ASSIGNMENT_OVERDUE,       // Red alert
    POINTS_EARNED,            // Green success
    STREAK_MILESTONE,         // Purple celebration
    NEW_REWARD_AVAILABLE,     // Blue info
    ASSIGNMENT_GRADED,        // Blue info
    SYSTEM_UPDATE             // Gray neutral
}
