package com.example.huzzler.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.ChatMessage
import com.example.huzzler.data.model.ChatOverview
import com.example.huzzler.data.repository.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> = _isTyping

    private val _overview = MutableLiveData(ChatOverview())
    val overview: LiveData<ChatOverview> = _overview

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val messagesList = mutableListOf<ChatMessage>()

    fun loadInitialMessage() {
        if (messagesList.isNotEmpty()) {
            return
        }

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
        if (content.isBlank()) {
            return
        }

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true,
            timestamp = Date()
        )

        messagesList.add(userMessage)
        _messages.value = messagesList.toList()

        requestAssistantReply()
    }

    private fun requestAssistantReply() {
        viewModelScope.launch {
            _isTyping.value = true
            _errorMessage.value = null

            val result = chatRepository.generateAssistantReply(messagesList.toList())

            result.onSuccess { aiMessage ->
                messagesList.add(aiMessage)
                _messages.value = messagesList.toList()
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message

                val fallbackMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = DEFAULT_FAILURE_MESSAGE,
                    isFromUser = false,
                    timestamp = Date()
                )

                messagesList.add(fallbackMessage)
                _messages.value = messagesList.toList()
            }

            _isTyping.value = false
        }
    }

    fun onErrorMessageConsumed() {
        _errorMessage.value = null
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

    companion object {
        private const val DEFAULT_FAILURE_MESSAGE = "I ran into a problem reaching the Gemini service. Please try again in a moment."
    }
}
