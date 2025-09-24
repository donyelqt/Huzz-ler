package com.example.huzzler.data.model

import java.util.Date

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date,
    val isTyping: Boolean = false
)
