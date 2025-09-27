package com.example.huzzler.data.model

/**
 * Represents the overview metrics displayed at the top of the AI chat screen.
 */
data class ChatOverview(
    val primeRate: Int = 0,
    val dayStreak: Int = 0,
    val points: Int = 0,
    val isOnline: Boolean = true
)
