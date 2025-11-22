package com.example.huzzler.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import androidx.compose.material.icons.rounded.Delete
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.huzzler.data.preferences.LanguagePreferences
import com.example.huzzler.data.preferences.ThemePreferences
import com.example.huzzler.data.repository.auth.AuthRepository
import com.example.huzzler.data.repository.user.UserRepository
import com.example.huzzler.ui.auth.SignInActivity
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

    companion object {
        // Toggle this flag to show/hide the language section in Settings
        private const val LANGUAGE_FEATURE_ENABLED = false
    }

    @Inject
    lateinit var themePreferences: ThemePreferences

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var languagePreferences: LanguagePreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // For now, force the app to use English when the language feature is disabled
        if (!LANGUAGE_FEATURE_ENABLED) {
            val locales = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(locales)
            lifecycleScope.launch {
                languagePreferences.setLanguage("en")
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // Observe theme and language preferences
                val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)
                val languageCode by languagePreferences.languageCode.collectAsState(initial = "en")
                
                HuzzlerTheme(darkTheme = isDarkMode) {
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onThemeToggle = {
                            lifecycleScope.launch {
                                themePreferences.setDarkMode(it)
                            }
                        },
                        currentLanguageCode = if (LANGUAGE_FEATURE_ENABLED) languageCode else "en",
                        showLanguageSection = LANGUAGE_FEATURE_ENABLED,
                        onLanguageClick = { if (LANGUAGE_FEATURE_ENABLED) showLanguagePicker() },
                        onBack = { findNavController().popBackStack() },
                        onDeleteAccountClick = { showDeleteAccountDialog() }
                    )
                }
            }
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete account")
            .setMessage("This will permanently delete your Huzzler account and all your data. This action cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                performAccountDeletion()
            }
            .show()
    }

    private fun performAccountDeletion() {
        lifecycleScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                Toast.makeText(requireContext(), "No signed-in user", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val userId = currentUser.uid

            val firestoreResult = userRepository.deleteUserProfile(userId)
            if (firestoreResult.isFailure) {
                Toast.makeText(requireContext(), "Failed to delete account data", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val authResult = authRepository.deleteCurrentUser()
            if (authResult.isFailure) {
                val errorMessage = authResult.exceptionOrNull()?.localizedMessage
                    ?: "Failed to delete account"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                return@launch
            }

            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showLanguagePicker() {
        val options = arrayOf("English", "Tagalog / Filipino")
        val codes = arrayOf("en", "fil")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose language")
            .setItems(options) { _, which ->
                val selectedCode = codes.getOrNull(which) ?: "en"
                lifecycleScope.launch {
                    languagePreferences.setLanguage(selectedCode)
                    val locales = LocaleListCompat.forLanguageTags(selectedCode)
                    AppCompatDelegate.setApplicationLocales(locales)
                    requireActivity().recreate()
                }
            }
            .show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    currentLanguageCode: String,
    showLanguageSection: Boolean,
    onLanguageClick: () -> Unit,
    onBack: () -> Unit,
    onDeleteAccountClick: () -> Unit
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
            if (showLanguageSection) {
                // Language Section
                SectionHeader("Language")

                SettingsCard {
                    SettingItem(
                        icon = Icons.Rounded.Language,
                        title = "Language",
                        subtitle = when (currentLanguageCode) {
                            "fil" -> "Tagalog / Filipino"
                            else -> "English"
                        },
                        isEnabled = true,
                        trailing = {
                        },
                        onClick = onLanguageClick
                    )
                }

                // Info text
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "💡 You can now choose English or Tagalog for Huzzler. Full translation of all screens will come next.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            SectionHeader("Account")

            SettingsCard {
                SettingItem(
                    icon = Icons.Rounded.Delete,
                    title = "Delete my account",
                    subtitle = "Permanently remove your account and data",
                    trailing = {
                        Text(
                            text = "Danger",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFB91C1C)
                        )
                    },
                    onClick = onDeleteAccountClick
                )
            }
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
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val alpha = if (isEnabled) 1f else 0.5f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onClick() }
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
