package com.example.huzzler.ui.planner

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
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.StudySession
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyPlannerDialog : DialogFragment() {

    private val viewModel: StudyPlannerViewModel by viewModels()
    private var assignments: List<Assignment> = emptyList()
    private var onStartFocusSession: ((StudySession) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
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
                    val uiState by viewModel.uiState.collectAsState()

                    StudyPlannerScreen(
                        uiState = uiState,
                        onGeneratePlan = {
                            viewModel.generateStudyPlan(assignments)
                        },
                        onStartFocusSession = { session ->
                            onStartFocusSession?.invoke(session)
                            dismiss()
                        },
                        onDismiss = { dismiss() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Auto-generate plan if assignments are provided
        if (assignments.isNotEmpty()) {
            viewModel.generateStudyPlan(assignments)
        }
    }

    companion object {
        const val TAG = "StudyPlannerDialog"

        fun newInstance(
            assignments: List<Assignment>,
            onStartFocusSession: ((StudySession) -> Unit)? = null
        ): StudyPlannerDialog {
            return StudyPlannerDialog().apply {
                this.assignments = assignments
                this.onStartFocusSession = onStartFocusSession
            }
        }
    }
}
