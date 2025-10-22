package com.example.huzzler.ui.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.example.huzzler.data.model.Reward
import com.example.huzzler.data.model.RewardCategory
import com.example.huzzler.data.preferences.ThemePreferences
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private val viewModel: RewardsViewModel by viewModels()
    
    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RewardsFragment", "Creating view")
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // Observe theme preference
                val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)
                
                HuzzlerTheme(darkTheme = isDarkMode) {
                    RewardsScreenWithErrorHandling(
                        viewModel = viewModel,
                        onBack = {
                            findNavController().navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardsScreenWithErrorHandling(
    viewModel: RewardsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Collect one-time events and display modern snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            scope.launch {
                when (event) {
                    is RewardEvent.RedemptionSuccess -> {
                        // Encode event data with type prefix to prevent race conditions
                        snackbarHostState.showSnackbar(
                            message = "SUCCESS|${event.rewardTitle}|${event.pointsDeducted}|${event.remainingPoints}",
                            actionLabel = "SUCCESS",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
                    
                    is RewardEvent.RedemptionError -> {
                        // Encode error message with type prefix
                        snackbarHostState.showSnackbar(
                            message = "ERROR|${event.message}",
                            actionLabel = "ERROR",
                            duration = androidx.compose.material3.SnackbarDuration.Long
                        )
                    }
                }
            }
        }
    }
    
    // Check if there's an error state
    val hasError = false
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasError) {
            ErrorScreen(
                onRetry = { viewModel.refresh() },
                onBack = onBack
            )
        } else {
            RewardsScreen(
                uiState = uiState,
                onCategorySelected = { category: RewardCategory ->
                    viewModel.selectCategory(category)
                },
                onRewardClick = { reward: Reward ->
                    viewModel.onRewardClicked(reward)
                },
                onBack = onBack
            )
        }
        
        // Modern, sleek snackbar host at bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            ModernSnackbar(
                snackbarData = data.visuals.message,
                isSuccess = data.visuals.actionLabel == "SUCCESS"
            )
        }
    }
}

/**
 * Modern, sleek, minimalist snackbar following 2025 UI/UX trends
 * Features: 
 * - Glassmorphism-inspired design with vibrant semantic colors
 * - Smooth rounded corners (16dp) for contemporary feel
 * - Icon-first visual hierarchy with Material Icons Rounded
 * - High contrast typography with subtle opacity variations
 * - Compact, information-dense layout
 * - Race-condition safe with encoded event data
 */
@Composable
private fun ModernSnackbar(
    snackbarData: String,
    isSuccess: Boolean
) {
    // Parse encoded data to prevent race conditions
    val parts = snackbarData.split("|")
    val eventType = parts.getOrNull(0) ?: "ERROR"
    
    // Determine state from encoded data (reliable source of truth)
    val actualIsSuccess = eventType == "SUCCESS"
    
    // 2025 trend: Vibrant, accessible semantic colors
    val containerColor = if (actualIsSuccess) {
        Color(0xFF10B981) // Emerald green - success
    } else {
        Color(0xFFEF4444) // Rose red - error
    }
    
    val (title, message) = if (actualIsSuccess && parts.size >= 4) {
        // SUCCESS|rewardTitle|pointsDeducted|remainingPoints
        val rewardTitle = parts[1]
        val remainingPoints = parts[3]
        "Redeemed!" to "$rewardTitle • $remainingPoints pts left"
    } else if (!actualIsSuccess && parts.size >= 2) {
        // ERROR|errorMessage
        "Failed" to parts[1]
    } else {
        "Notification" to "Processing..."
    }
    
    // Custom Surface-based snackbar to avoid Snackbar API constraints
    Surface(
        modifier = Modifier.padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Leading icon with semantic meaning (uses parsed data for accuracy)
            Icon(
                imageVector = if (actualIsSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content area with minimalist typography
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.15.sp
                    ),
                    color = Color.White
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        letterSpacing = 0.25.sp
                    ),
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Unable to load rewards",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Please check your connection and try again",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Retry")
            }
            
            Button(
                onClick = onBack
            ) {
                Text("Go Back")
            }
        }
    }
}
