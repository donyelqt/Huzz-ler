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
 * Full-screen DialogFragment for assignment details
 * Using DialogFragment ensures proper lifecycle management
 */
class AssignmentDetailDialog : DialogFragment() {
    
    private var assignment: Assignment? = null
    private var onComplete: ((Assignment) -> Unit)? = null
    private var onSubmit: ((Assignment) -> Unit)? = null
    private var onFocus: ((Assignment) -> Unit)? = null
    
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
                        AssignmentDetailScreen(
                            assignment = assignmentData,
                            onBack = { dismiss() },
                            onComplete = { assignmentToComplete ->
                                onComplete?.invoke(assignmentToComplete)
                                dismiss()
                            },
                            onSubmit = { assignmentToSubmit ->
                                onSubmit?.invoke(assignmentToSubmit)
                                dismiss()
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
            onComplete: (Assignment) -> Unit,
            onSubmit: (Assignment) -> Unit = {},
            onFocus: (Assignment) -> Unit = {}
        ): AssignmentDetailDialog {
            return AssignmentDetailDialog().apply {
                this.assignment = assignment
                this.onComplete = onComplete
                this.onSubmit = onSubmit
                this.onFocus = onFocus
            }
        }
    }
}
