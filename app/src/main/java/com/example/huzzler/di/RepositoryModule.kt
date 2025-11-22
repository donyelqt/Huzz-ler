package com.example.huzzler.di

import com.example.huzzler.data.repository.auth.AuthRepository
import com.example.huzzler.data.repository.auth.FirebaseAuthRepository
import com.example.huzzler.data.repository.chat.ChatRepository
import com.example.huzzler.data.repository.chat.GeminiChatRepository
import com.example.huzzler.data.repository.user.FirestoreUserRepository
import com.example.huzzler.data.repository.user.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: FirestoreUserRepository
    ): UserRepository
}
