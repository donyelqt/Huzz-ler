package com.example.huzzler.ui.rewards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Reward
import com.example.huzzler.data.model.RewardCategory
import com.example.huzzler.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _rewards = MutableLiveData<List<Reward>>()
    val rewards: LiveData<List<Reward>> = _rewards

    private val _selectedCategory = MutableLiveData<com.example.huzzler.ui.rewards.RewardCategory>()
    val selectedCategory: LiveData<com.example.huzzler.ui.rewards.RewardCategory> = _selectedCategory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val allRewards = mutableListOf<Reward>()

    init {
        _selectedCategory.value = com.example.huzzler.ui.rewards.RewardCategory.GAME_REWARDS
    }

    fun loadRewards() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Simulate API call
            delay(1000)
            
            // Mock user data
            _user.value = User(
                id = "1",
                email = "daa6681@students.uc-bcf.edu.ph",
                name = "Doniele Arys",
                points = 1280,
                streak = 3,
                primeRate = 87,
                rank = "Scholar"
            )
            
            // Mock rewards data
            allRewards.clear()
            allRewards.addAll(
                listOf(
                    Reward(
                        id = "1",
                        title = "Valorant Points",
                        description = "1,000 VP for weapon skins",
                        pointsCost = 400,
                        category = RewardCategory.GAME_REWARDS,
                        imageUrl = "",
                        isPopular = true,
                        gameTag = "Valorant"
                    ),
                    Reward(
                        id = "2",
                        title = "MLBB Battle Pass",
                        description = "699 diamonds for battle pass",
                        pointsCost = 500,
                        category = RewardCategory.GAME_REWARDS,
                        imageUrl = "",
                        isPopular = false,
                        gameTag = "MLBB"
                    ),
                    Reward(
                        id = "3",
                        title = "Extra Credit Points",
                        description = "5 bonus points for next assignment",
                        pointsCost = 200,
                        category = RewardCategory.ACADEMIC_PERKS,
                        imageUrl = "",
                        isPopular = false
                    ),
                    Reward(
                        id = "4",
                        title = "Assignment Extension",
                        description = "24-hour extension for any assignment",
                        pointsCost = 300,
                        category = RewardCategory.ACADEMIC_PERKS,
                        imageUrl = "",
                        isPopular = true
                    )
                )
            )
            
            filterRewardsByCategory(_selectedCategory.value ?: com.example.huzzler.ui.rewards.RewardCategory.GAME_REWARDS)
            _isLoading.value = false
        }
    }

    fun selectCategory(category: com.example.huzzler.ui.rewards.RewardCategory) {
        _selectedCategory.value = category
        filterRewardsByCategory(category)
    }

    private fun filterRewardsByCategory(category: com.example.huzzler.ui.rewards.RewardCategory) {
        val filteredRewards = when (category) {
            com.example.huzzler.ui.rewards.RewardCategory.GAME_REWARDS -> 
                allRewards.filter { it.category == RewardCategory.GAME_REWARDS }
            com.example.huzzler.ui.rewards.RewardCategory.ACADEMIC_PERKS -> 
                allRewards.filter { it.category == RewardCategory.ACADEMIC_PERKS }
        }
        _rewards.value = filteredRewards
    }

    fun onRewardClicked(reward: Reward) {
        // Handle reward redemption
        val currentUser = _user.value ?: return
        if (currentUser.points >= reward.pointsCost) {
            // Simulate redemption
            val updatedUser = currentUser.copy(points = currentUser.points - reward.pointsCost)
            _user.value = updatedUser
        }
    }
}
