package com.example.huzzler.data.repository.user

import com.example.huzzler.data.model.User

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<User?>
    suspend fun getCurrentUserProfile(): User?
    suspend fun upsertUserProfile(user: User): Result<Unit>
    suspend fun updateUserName(userId: String, newName: String): Result<Unit>
    suspend fun deleteUserProfile(userId: String): Result<Unit>
}
