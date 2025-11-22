package com.example.huzzler.data.repository.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
}
