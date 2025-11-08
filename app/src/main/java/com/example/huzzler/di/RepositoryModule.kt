package com.example.huzzler.di

import com.example.huzzler.data.repository.chat.ChatRepository
import com.example.huzzler.data.repository.chat.GeminiChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: GeminiChatRepository
    ): ChatRepository
}
