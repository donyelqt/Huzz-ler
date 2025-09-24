package com.example.huzzler.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val points: Int = 0,
    val streak: Int = 0,
    val primeRate: Int = 0,
    val rank: String = "Scholar",
    val profileImageUrl: String = ""
)
