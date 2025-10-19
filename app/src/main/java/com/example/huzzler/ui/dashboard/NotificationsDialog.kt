package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.example.huzzler.data.model.Notification
import com.example.huzzler.ui.theme.HuzzlerTheme

/**
 * Full-screen DialogFragment for notifications
 * Using DialogFragment ensures proper lifecycle management
 */
class NotificationsDialog : DialogFragment() {
    
    private var notifications: List<Notification> = emptyList()
    
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
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { dismiss() }
                    )
                }
            }
        }
    }
    
    companion object {
        fun newInstance(notifications: List<Notification>): NotificationsDialog {
            return NotificationsDialog().apply {
                this.notifications = notifications
            }
        }
    }
}
