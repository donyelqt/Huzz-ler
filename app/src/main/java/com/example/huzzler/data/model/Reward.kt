package com.example.huzzler.data.model

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val category: RewardCategory,
    val imageUrl: String,
    val isPopular: Boolean = false,
    val gameTag: String? = null
)

enum class RewardCategory {
    GAME_REWARDS, ACADEMIC_PERKS
}
