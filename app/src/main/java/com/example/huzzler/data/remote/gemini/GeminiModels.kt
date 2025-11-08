package com.example.huzzler.data.remote.gemini

import com.google.gson.annotations.SerializedName

data class GeminiPart(
    @SerializedName("text") val text: String? = null
)

data class GeminiContent(
    @SerializedName("role") val role: String,
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiGenerationConfig(
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("topK") val topK: Int? = null,
    @SerializedName("topP") val topP: Double? = null,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int? = null
)

data class GeminiSafetySetting(
    @SerializedName("category") val category: String,
    @SerializedName("threshold") val threshold: String
)

data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @SerializedName("safetySettings") val safetySettings: List<GeminiSafetySetting>? = null
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?,
    @SerializedName("promptFeedback") val promptFeedback: GeminiPromptFeedback?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?,
    @SerializedName("finishReason") val finishReason: String?,
    @SerializedName("safetyRatings") val safetyRatings: List<GeminiSafetyRating>?
)

data class GeminiSafetyRating(
    @SerializedName("category") val category: String?,
    @SerializedName("probability") val probability: String?
)

data class GeminiPromptFeedback(
    @SerializedName("blockReason") val blockReason: String?,
    @SerializedName("safetyRatings") val safetyRatings: List<GeminiSafetyRating>?
)
