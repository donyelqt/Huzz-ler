package com.example.huzzler.data.repository.chat

import com.example.huzzler.BuildConfig
import com.example.huzzler.data.model.ChatMessage
import com.example.huzzler.data.remote.gemini.GeminiContent
import com.example.huzzler.data.remote.gemini.GeminiGenerationConfig
import com.example.huzzler.data.remote.gemini.GeminiPart
import com.example.huzzler.data.remote.gemini.GeminiRequest
import com.example.huzzler.data.remote.gemini.GeminiResponse
import com.example.huzzler.data.remote.gemini.GeminiSafetySetting
import com.example.huzzler.data.remote.gemini.GeminiService
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiChatRepository @Inject constructor(
    private val geminiService: GeminiService
) : ChatRepository {

    override suspend fun generateAssistantReply(history: List<ChatMessage>): Result<ChatMessage> = runCatching {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            error("Gemini API key is missing. Define GEMINI_API_KEY in your gradle.properties or environment configuration.")
        }

        val truncatedHistory = history.takeLast(HISTORY_LIMIT)

        val contents = buildList {
            add(
                GeminiContent(
                    role = ROLE_USER,
                    parts = listOf(GeminiPart(text = SYSTEM_PROMPT.trim()))
                )
            )

            addAll(truncatedHistory.map { message ->
                GeminiContent(
                    role = if (message.isFromUser) ROLE_USER else ROLE_MODEL,
                    parts = listOf(GeminiPart(text = message.content))
                )
            })
        }

        val request = GeminiRequest(
            contents = contents,
            generationConfig = DEFAULT_GENERATION_CONFIG,
            safetySettings = DEFAULT_SAFETY_SETTINGS
        )

        val response = geminiService.generateContent(
            model = BuildConfig.GEMINI_MODEL,
            request = request,
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        response.promptFeedback?.blockReason?.let { reason ->
            error("Prompt blocked by safety filters: $reason")
        }

        val reply = extractReply(response)

        ChatMessage(
            id = UUID.randomUUID().toString(),
            content = reply,
            isFromUser = false,
            timestamp = Date()
        )
    }

    private fun extractReply(response: GeminiResponse): String {
        val candidate = response.candidates?.firstOrNull()
            ?: error("Gemini response contained no candidates")

        val contentParts = candidate.content?.parts
            ?.mapNotNull { it.text?.trim().takeUnless { text -> text.isNullOrEmpty() } }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        if (contentParts.isEmpty()) {
            error("Gemini returned an empty response")
        }

        return contentParts.joinToString(separator = "\n\n")
    }

    private companion object {
        private const val ROLE_USER = "user"
        private const val ROLE_MODEL = "model"
        private const val ROLE_SYSTEM = "system"
        private const val HISTORY_LIMIT = 10

        private const val SYSTEM_PROMPT = """
            You are Huzz, a friendly academic productivity assistant helping college students manage assignments, meet deadlines, and stay motivated. Respond concisely, with clear actionable suggestions when appropriate, and keep tone supportive and professional.
        """

        private val DEFAULT_GENERATION_CONFIG = GeminiGenerationConfig(
            temperature = 0.7,
            topK = 40,
            topP = 0.95,
            maxOutputTokens = 512
        )

        private val DEFAULT_SAFETY_SETTINGS = listOf(
            GeminiSafetySetting(category = "HARM_CATEGORY_HARASSMENT", threshold = "BLOCK_NONE"),
            GeminiSafetySetting(category = "HARM_CATEGORY_HATE_SPEECH", threshold = "BLOCK_NONE"),
            GeminiSafetySetting(category = "HARM_CATEGORY_SEXUALLY_EXPLICIT", threshold = "BLOCK_NONE"),
            GeminiSafetySetting(category = "HARM_CATEGORY_DANGEROUS_CONTENT", threshold = "BLOCK_NONE"),
            GeminiSafetySetting(category = "HARM_CATEGORY_CIVIC_INTEGRITY", threshold = "BLOCK_NONE")
        )
    }
}
