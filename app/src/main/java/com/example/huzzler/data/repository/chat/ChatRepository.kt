package com.example.huzzler.data.repository.chat

import com.example.huzzler.data.model.ChatMessage

interface ChatRepository {
    suspend fun generateAssistantReply(history: List<ChatMessage>): Result<ChatMessage>
}
