package com.example.huzzler.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.FocusSession
import com.example.huzzler.data.model.FocusSessionStatus
import com.example.huzzler.data.model.TimerState
import com.example.huzzler.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FocusEvent {
    data class SessionCompleted(val pointsEarned: Int, val totalSessions: Int) : FocusEvent
    data class BreakStarted(val breakMinutes: Int) : FocusEvent
    data class BreakEnded(val message: String) : FocusEvent
    data class ShowMessage(val message: String) : FocusEvent
}

@HiltViewModel
class FocusTimerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _currentSession = MutableStateFlow<FocusSession?>(null)
    val currentSession: StateFlow<FocusSession?> = _currentSession.asStateFlow()

    private val _events = MutableSharedFlow<FocusEvent>()
    val events: SharedFlow<FocusEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null
    private var totalSessionsCompleted = 0

    companion object {
        const val DEFAULT_FOCUS_MINUTES = 25
        const val DEFAULT_BREAK_MINUTES = 5
        const val LONG_BREAK_MINUTES = 15
        const val SESSIONS_BEFORE_LONG_BREAK = 4
        const val POINTS_PER_SESSION = 25
        const val BONUS_POINTS_WITH_ASSIGNMENT = 10
    }

    fun startFocusSession(assignment: Assignment? = null) {
        if (_timerState.value.isRunning) return

        val focusMinutes = DEFAULT_FOCUS_MINUTES
        val totalSeconds = focusMinutes * 60

        _currentSession.value = FocusSession(
            assignmentId = assignment?.id,
            assignmentTitle = assignment?.title,
            durationMinutes = focusMinutes,
            status = FocusSessionStatus.FOCUSING
        )

        _timerState.value = TimerState(
            totalSeconds = totalSeconds,
            remainingSeconds = totalSeconds,
            isRunning = true,
            isPaused = false,
            isBreak = false,
            sessionCount = totalSessionsCompleted,
            progress = 1f
        )

        startCountdown()
    }


    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                if (!_timerState.value.isPaused) {
                    delay(1000L)
                    val current = _timerState.value
                    if (!current.isPaused && current.isRunning) {
                        val newRemaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                        val progress = newRemaining.toFloat() / current.totalSeconds.toFloat()
                        _timerState.value = current.copy(
                            remainingSeconds = newRemaining,
                            progress = progress
                        )

                        if (newRemaining == 0) {
                            onTimerComplete()
                        }
                    }
                } else {
                    delay(100L)
                }
            }
        }
    }

    private fun onTimerComplete() {
        viewModelScope.launch {
            val current = _timerState.value
            
            if (current.isBreak) {
                // Break ended, ready for next focus session
                _events.emit(FocusEvent.BreakEnded("Break's over! Ready to focus again?"))
                resetTimer()
            } else {
                // Focus session completed
                totalSessionsCompleted++
                val basePoints = POINTS_PER_SESSION
                val bonusPoints = if (_currentSession.value?.assignmentId != null) BONUS_POINTS_WITH_ASSIGNMENT else 0
                val totalPoints = basePoints + bonusPoints

                // Award points to user
                awardPoints(totalPoints)

                _events.emit(FocusEvent.SessionCompleted(totalPoints, totalSessionsCompleted))

                // Start break
                startBreak()
            }
        }
    }

    private fun startBreak() {
        val isLongBreak = totalSessionsCompleted % SESSIONS_BEFORE_LONG_BREAK == 0
        val breakMinutes = if (isLongBreak) LONG_BREAK_MINUTES else DEFAULT_BREAK_MINUTES
        val breakSeconds = breakMinutes * 60

        _currentSession.value = _currentSession.value?.copy(
            status = FocusSessionStatus.ON_BREAK,
            sessionsCompleted = totalSessionsCompleted
        )

        _timerState.value = TimerState(
            totalSeconds = breakSeconds,
            remainingSeconds = breakSeconds,
            isRunning = true,
            isPaused = false,
            isBreak = true,
            sessionCount = totalSessionsCompleted,
            progress = 1f
        )

        viewModelScope.launch {
            _events.emit(FocusEvent.BreakStarted(breakMinutes))
        }

        startCountdown()
    }

    fun pauseTimer() {
        if (!_timerState.value.isRunning) return
        _timerState.value = _timerState.value.copy(isPaused = true)
        _currentSession.value = _currentSession.value?.copy(status = FocusSessionStatus.PAUSED)
    }

    fun resumeTimer() {
        if (!_timerState.value.isPaused) return
        val newStatus = if (_timerState.value.isBreak) FocusSessionStatus.ON_BREAK else FocusSessionStatus.FOCUSING
        _timerState.value = _timerState.value.copy(isPaused = false)
        _currentSession.value = _currentSession.value?.copy(status = newStatus)
    }

    fun stopTimer() {
        timerJob?.cancel()
        _currentSession.value = _currentSession.value?.copy(status = FocusSessionStatus.CANCELLED)
        resetTimer()
    }

    fun skipBreak() {
        if (!_timerState.value.isBreak) return
        timerJob?.cancel()
        resetTimer()
        viewModelScope.launch {
            _events.emit(FocusEvent.ShowMessage("Break skipped. Start when you're ready!"))
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState(
            totalSeconds = DEFAULT_FOCUS_MINUTES * 60,
            remainingSeconds = DEFAULT_FOCUS_MINUTES * 60,
            isRunning = false,
            isPaused = false,
            isBreak = false,
            sessionCount = totalSessionsCompleted,
            progress = 1f
        )
        _currentSession.value = null
    }

    private suspend fun awardPoints(points: Int) {
        try {
            val currentUser = userRepository.getCurrentUserProfile()
            currentUser?.let { user ->
                val updatedUser = user.copy(points = user.points + points)
                userRepository.upsertUserProfile(updatedUser)
            }
        } catch (e: Exception) {
            _events.emit(FocusEvent.ShowMessage("Points saved locally"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
