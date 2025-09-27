package com.example.huzzler.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.ChatMessage
import com.example.huzzler.data.model.ChatOverview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> = _isTyping

    private val _overview = MutableLiveData(ChatOverview())
    val overview: LiveData<ChatOverview> = _overview

    private val messagesList = mutableListOf<ChatMessage>()

    fun loadInitialMessage() {
        val initialMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = "Hey buddy, need a quick way to submit during prime time? or are you rushing?",
            isFromUser = false,
            timestamp = Date()
        )
        
        messagesList.add(initialMessage)
        _messages.value = messagesList.toList()

        // Simulate initial overview data
        _overview.value = ChatOverview(
            primeRate = 85,
            dayStreak = 3,
            points = 1240
        )
    }

    fun sendMessage(content: String) {
        // Add user message
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true,
            timestamp = Date()
        )
        
        messagesList.add(userMessage)
        _messages.value = messagesList.toList()
        
        // Simulate AI response
        generateAIResponse(content)
    }

    private fun generateAIResponse(userMessage: String) {
        viewModelScope.launch {
            _isTyping.value = true
            
            // Simulate typing delay
            delay(2000)
            
            val aiResponse = generateResponseBasedOnInput(userMessage)
            
            val aiMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = aiResponse,
                isFromUser = false,
                timestamp = Date()
            )
            
            messagesList.add(aiMessage)
            _messages.value = messagesList.toList()
            _isTyping.value = false
        }
    }

    private fun generateResponseBasedOnInput(input: String): String {
        return when {
            input.lowercase().contains("assignment") || input.lowercase().contains("homework") -> {
                "I can help you with your assignments! Would you like me to check your upcoming deadlines or help you organize your tasks?"
            }
            input.lowercase().contains("submit") || input.lowercase().contains("upload") -> {
                "Sure! You can submit your files through the upload button below. I'll help you organize and submit them properly."
            }
            input.lowercase().contains("points") || input.lowercase().contains("reward") -> {
                "Great question about points! You currently have points that you can redeem for rewards. Check out the Rewards section to see what's available!"
            }
            input.lowercase().contains("help") -> {
                "I'm here to help! I can assist you with:\n• Managing assignments\n• Submitting work\n• Tracking your progress\n• Redeeming rewards\n\nWhat would you like to do?"
            }
            input.lowercase().contains("hello") || input.lowercase().contains("hi") -> {
                "Hello! I'm your Huzzler AI assistant. I'm here to help you stay productive and manage your academic tasks. What can I help you with today?"
            }
            else -> {
                "That's interesting! I'm here to help you with your academic tasks and productivity. Is there anything specific you'd like assistance with?"
            }
        }
    }

    fun handleFileAttachment() {
        // Handle file attachment logic
        val systemMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = "File attachment feature coming soon! You'll be able to attach documents, images, and other files.",
            isFromUser = false,
            timestamp = Date()
        )
        
        messagesList.add(systemMessage)
        _messages.value = messagesList.toList()
    }

    fun handleCameraCapture() {
        val systemMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = "Camera integration coming soon! You'll be able to capture documents directly from the chat.",
            isFromUser = false,
            timestamp = Date()
        )

        messagesList.add(systemMessage)
        _messages.value = messagesList.toList()
    }
}
