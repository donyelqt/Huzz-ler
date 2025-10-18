package com.example.huzzler.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Reward
import com.example.huzzler.data.model.RewardCategory
import com.example.huzzler.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed interface for one-time UI events
 * Following event-driven architecture best practices
 */
sealed interface RewardEvent {
    data class RedemptionSuccess(
        val rewardTitle: String,
        val pointsDeducted: Int,
        val remainingPoints: Int
    ) : RewardEvent
    
    data class RedemptionError(
        val message: String,
        val requiredPoints: Int,
        val currentPoints: Int
    ) : RewardEvent
}

@HiltViewModel
class RewardsViewModel @Inject constructor() : ViewModel() {

    private val allRewards = mutableListOf<Reward>()

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()
    
    // SharedFlow for one-time events (like toasts)
    // replay = 0 ensures events are only delivered once
    private val _events = MutableSharedFlow<RewardEvent>(replay = 0)
    val events: SharedFlow<RewardEvent> = _events.asSharedFlow()

    init {
        loadRewards()
    }

    fun loadRewards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulate API call
            delay(1000)

            // Mock user data
            val user = User(
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

            val category = _uiState.value.selectedCategory
            _uiState.update {
                it.copy(
                    user = user,
                    rewards = filterRewards(category),
                    isLoading = false
                )
            }
        }
    }

    fun refresh() = loadRewards()

    fun selectCategory(category: RewardCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                rewards = filterRewards(category)
            )
        }
    }

    private fun filterRewards(category: RewardCategory): List<Reward> {
        return when (category) {
            RewardCategory.GAME_REWARDS ->
                allRewards.filter { it.category == RewardCategory.GAME_REWARDS }
            RewardCategory.ACADEMIC_PERKS ->
                allRewards.filter { it.category == RewardCategory.ACADEMIC_PERKS }
        }
    }

    /**
     * Handles reward redemption with comprehensive validation and user feedback
     * Emits success or error events for UI to display toast notifications
     */
    fun onRewardClicked(reward: Reward) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user
            
            // Guard: Check if user data is loaded
            if (currentUser == null) {
                _events.emit(
                    RewardEvent.RedemptionError(
                        message = "Unable to process redemption. Please try again.",
                        requiredPoints = reward.pointsCost,
                        currentPoints = 0
                    )
                )
                return@launch
            }
            
            // Validate: Check if user has sufficient points
            if (currentUser.points < reward.pointsCost) {
                val deficit = reward.pointsCost - currentUser.points
                _events.emit(
                    RewardEvent.RedemptionError(
                        message = "Insufficient points! You need $deficit more points to redeem this reward.",
                        requiredPoints = reward.pointsCost,
                        currentPoints = currentUser.points
                    )
                )
                return@launch
            }
            
            // Process redemption
            val updatedUser = currentUser.copy(
                points = currentUser.points - reward.pointsCost
            )
            
            // Update state
            _uiState.update {
                it.copy(
                    user = updatedUser,
                    rewards = filterRewards(it.selectedCategory)
                )
            }
            
            // Emit success event
            _events.emit(
                RewardEvent.RedemptionSuccess(
                    rewardTitle = reward.title,
                    pointsDeducted = reward.pointsCost,
                    remainingPoints = updatedUser.points
                )
            )
        }
    }
}

data class RewardsUiState(
    val user: User? = null,
    val selectedCategory: RewardCategory = RewardCategory.GAME_REWARDS,
    val rewards: List<Reward> = emptyList(),
    val isLoading: Boolean = false
)
