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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.example.huzzler.data.model.Reward
import com.example.huzzler.data.model.RewardCategory
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private val viewModel: RewardsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RewardsFragment", "Creating view")
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HuzzlerTheme {
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
    
    // Track current event for custom snackbar display
    var currentEvent by remember { mutableStateOf<RewardEvent?>(null) }
    
    // Collect one-time events and display modern snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            currentEvent = event
            scope.launch {
                when (event) {
                    is RewardEvent.RedemptionSuccess -> {
                        snackbarHostState.showSnackbar(
                            message = event.rewardTitle,
                            actionLabel = "SUCCESS",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
                    
                    is RewardEvent.RedemptionError -> {
                        snackbarHostState.showSnackbar(
                            message = event.message,
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
                event = currentEvent,
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
 */
@Composable
private fun ModernSnackbar(
    event: RewardEvent?,
    isSuccess: Boolean
) {
    // 2025 trend: Vibrant, accessible semantic colors
    val containerColor = if (isSuccess) {
        Color(0xFF10B981) // Emerald green - success
    } else {
        Color(0xFFEF4444) // Rose red - error
    }
    
    val (title, message) = when (event) {
        is RewardEvent.RedemptionSuccess -> {
            "Redeemed!" to "${event.rewardTitle} • ${event.remainingPoints} pts left"
        }
        is RewardEvent.RedemptionError -> {
            "Failed" to event.message
        }
        null -> {
            "Notification" to "Processing..."
        }
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
            // Leading icon with semantic meaning
            Icon(
                imageVector = if (isSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
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
