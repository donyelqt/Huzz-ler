package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.ui.theme.HuzzlerTheme

/**
 * Full-screen DialogFragment for assignment submission
 * Provides file upload and text entry interface
 */
class AssignmentSubmissionDialog : DialogFragment() {
    
    private var assignment: Assignment? = null
    private var onSubmitComplete: ((Assignment) -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HuzzlerTheme {
                    assignment?.let { assignmentData ->
                        AssignmentSubmissionScreen(
                            assignment = assignmentData,
                            onBack = { dismiss() },
                            onSubmit = { assignment, files, text ->
                                android.util.Log.d("Submission", "Files: $files, Text: $text")
                                onSubmitComplete?.invoke(assignment)
                            }
                        )
                    }
                }
            }
        }
    }
    
    companion object {
        fun newInstance(
            assignment: Assignment,
            onSubmitComplete: (Assignment) -> Unit
        ): AssignmentSubmissionDialog {
            return AssignmentSubmissionDialog().apply {
                this.assignment = assignment
                this.onSubmitComplete = onSubmitComplete
            }
        }
    }
}
