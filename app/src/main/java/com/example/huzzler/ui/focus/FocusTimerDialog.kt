package com.example.huzzler.ui.focus

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.ui.theme.HuzzlerTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FocusTimerDialog : DialogFragment() {

    private val viewModel: FocusTimerViewModel by viewModels()
    private var assignment: Assignment? = null
    private var onSessionCompleted: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Huzzler_FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HuzzlerTheme {
                    val timerState by viewModel.timerState.collectAsState()
                    val currentSession by viewModel.currentSession.collectAsState()

                    FocusTimerScreen(
                        timerState = timerState,
                        assignment = assignment,
                        onStart = { viewModel.startFocusSession(assignment) },
                        onPause = { viewModel.pauseTimer() },
                        onResume = { viewModel.resumeTimer() },
                        onStop = { viewModel.stopTimer() },
                        onSkipBreak = { viewModel.skipBreak() },
                        onDismiss = { dismiss() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is FocusEvent.SessionCompleted -> {
                        onSessionCompleted?.invoke(event.pointsEarned)
                        showSnackbar("🎉 +${event.pointsEarned} points! Session ${event.totalSessions} complete!")
                    }
                    is FocusEvent.BreakStarted -> {
                        showSnackbar("☕ ${event.breakMinutes} minute break started!")
                    }
                    is FocusEvent.BreakEnded -> {
                        showSnackbar(event.message)
                    }
                    is FocusEvent.ShowMessage -> {
                        showSnackbar(event.message)
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "FocusTimerDialog"

        fun newInstance(
            assignment: Assignment? = null,
            onSessionCompleted: ((Int) -> Unit)? = null
        ): FocusTimerDialog {
            return FocusTimerDialog().apply {
                this.assignment = assignment
                this.onSessionCompleted = onSessionCompleted
            }
        }
    }
}
