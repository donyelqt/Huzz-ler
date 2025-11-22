package com.example.huzzler.data.repository.user

import com.example.huzzler.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection get() = firestore.collection("users")

    override suspend fun getUserProfile(userId: String): Result<User?> = runCatching {
        val snapshot = usersCollection.document(userId).get().await()
        if (!snapshot.exists()) {
            null
        } else {
            snapshot.toObject(User::class.java)?.copy(id = userId)
        }
    }

    override suspend fun upsertUserProfile(user: User): Result<Unit> = runCatching {
        val data = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "points" to user.points,
            "streak" to user.streak,
            "primeRate" to user.primeRate,
            "rank" to user.rank,
            "profileImageUrl" to user.profileImageUrl,
            "lastStreakDate" to user.lastStreakDate
        )
        usersCollection.document(user.id).set(data, SetOptions.merge()).await()
        Unit
    }

    override suspend fun updateUserName(userId: String, newName: String): Result<Unit> = runCatching {
        usersCollection.document(userId).update("name", newName).await()
        Unit
    }

    override suspend fun deleteUserProfile(userId: String): Result<Unit> = runCatching {
        usersCollection.document(userId).delete().await()
        Unit
    }
}
