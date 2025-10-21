package com.example.huzzler

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.huzzler.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setupWithNavController(navController)
        
        // Hide/Show bottom nav based on destination (2025 Best Practice)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Primary destinations - SHOW bottom nav
                R.id.navigation_dashboard,
                R.id.navigation_rewards,
                R.id.navigation_chat,
                R.id.navigation_profile -> {
                    navView.visibility = View.VISIBLE
                }
                // Secondary destinations - HIDE bottom nav
                R.id.navigation_all_assignments,
                R.id.navigation_notifications -> {
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
