package com.example.huzzler

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.huzzler.data.preferences.ThemePreferences
import com.example.huzzler.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Activity - 2025 UX Best Practices
 * 
 * Bottom Navigation Behavior:
 * - Show on primary destinations (Dashboard, Rewards, Chat, Profile)
 * - Hide on secondary destinations (All Assignments, Notifications, etc.)
 * 
 * This follows the pattern used by:
 * - Instagram (hide nav on profile detail)
 * - Twitter (hide nav on tweet detail)
 * - Google Photos (hide nav on album view)
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme preference before setting content
        lifecycleScope.launch {
            themePreferences.isDarkMode.collect { isDarkMode ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setupWithNavController(navController)
        
        // Hide/Show bottom nav based on destination (2025 Best Practice)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check by label as fallback for newly added destinations
            val isSecondaryDestination = destination.label in listOf(
                "All Assignments",
                "Notifications", 
                "Settings"
            )
            
            when {
                // Primary destinations - SHOW bottom nav
                destination.id == R.id.navigation_dashboard ||
                destination.id == R.id.navigation_rewards ||
                destination.id == R.id.navigation_chat ||
                destination.id == R.id.navigation_profile -> {
                    navView.visibility = View.VISIBLE
                }
                // Secondary destinations - HIDE bottom nav (by ID or label)
                destination.id == R.id.navigation_all_assignments ||
                destination.id == R.id.navigation_notifications ||
                isSecondaryDestination -> {
                    navView.visibility = View.GONE
                }
                // Default - SHOW bottom nav
                else -> {
                    navView.visibility = View.VISIBLE
                }
            }
        }
    }
}
