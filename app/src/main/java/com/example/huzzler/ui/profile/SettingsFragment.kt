package com.example.huzzler.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.huzzler.data.preferences.ThemePreferences
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings Fragment - 2025 Modern Design
 * 
 * Features:
 * ✨ Material Design 3 with Compose
 * 🎨 Theme Toggle (Light/Dark)
 * 🌍 Language Selector (English only for now)
 * 📱 Clean, minimalist UI
 * 
 * Following: Linear, Arc Browser, Notion design patterns
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

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
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onThemeToggle = {
                            lifecycleScope.launch {
                                themePreferences.setDarkMode(it)
                            }
                        },
                        onBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            // Modern header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back button
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Title
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Section
            SectionHeader("Language")
            
            SettingsCard {
                SettingItem(
                    icon = Icons.Rounded.Language,
                    title = "Language",
                    subtitle = "English (Default)",
                    isEnabled = false,
                    trailing = {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "Only",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFA16207)
                            )
                        }
                    }
                )
            }
            
            // Info text
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "💡 More languages coming soon! We're working on adding support for multiple languages.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        color = Color(0xFF64748B),
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        content()
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean = true,
    trailing: @Composable () -> Unit
) {
    val alpha = if (isEnabled) 1f else 0.5f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFF1F5F9).copy(alpha = alpha),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFEF4444).copy(alpha = alpha),
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Text
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                ),
                color = Color(0xFF0F172A).copy(alpha = alpha)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B).copy(alpha = alpha)
            )
        }
        
        // Trailing widget
        trailing()
    }
}
