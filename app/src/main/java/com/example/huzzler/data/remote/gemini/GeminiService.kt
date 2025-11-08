package com.example.huzzler.data.remote.gemini

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiService {

    @POST("v1/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): GeminiResponse
}
