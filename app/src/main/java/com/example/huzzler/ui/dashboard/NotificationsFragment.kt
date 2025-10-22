package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.huzzler.data.preferences.ThemePreferences
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Full-screen Fragment for notifications (2025 Best Practice)
 * Replaces DialogFragment pattern for better UX and navigation
 * 
 * Benefits:
 * - Proper back stack integration
 * - Shared ViewModel (no memory leaks)
 * - Native navigation animations
 * - Follows Google/Facebook/LinkedIn pattern
 */
@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    
    private val viewModel: DashboardViewModel by activityViewModels()
    
    @Inject
    lateinit var themePreferences: ThemePreferences
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // Observe theme preference
                val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)
                
                HuzzlerTheme(darkTheme = isDarkMode) {
                    // Use remember to observe LiveData in Compose
                    val notifications = viewModel.notifications.observeAsState(initial = emptyList())
                    
                    NotificationsScreen(
                        notifications = notifications.value,
                        onBack = { findNavController().popBackStack() },
                        onNotificationClick = { notification ->
                            // Handle notification click
                            viewModel.markNotificationAsRead(notification.id)
                            
                            // Navigate based on notification type
                            when (notification.type) {
                                com.example.huzzler.data.model.NotificationType.ASSIGNMENT_DUE_SOON,
                                com.example.huzzler.data.model.NotificationType.ASSIGNMENT_OVERDUE,
                                com.example.huzzler.data.model.NotificationType.ASSIGNMENT_GRADED -> {
                                    // Go back to dashboard, assignment detail will open
                                    findNavController().popBackStack()
                                }
                                else -> {
                                    // Other notification types - stay on page
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
