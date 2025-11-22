package com.example.huzzler.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.huzzler.R
import com.example.huzzler.databinding.FragmentProfileBinding
import com.example.huzzler.ui.auth.SignInActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
        
        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            val displayName = if (user.name.isNotBlank()) {
                user.name
            } else {
                user.email.substringBefore("@")
            }

            val initials = displayName
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it[0].uppercaseChar() }
                .joinToString("")
                .take(2)

            binding.apply {
                tvUserName.text = displayName
                tvUserEmail.text = user.email
                tvUserPoints.text = "${user.points} Points"
                tvUserStreak.text = "${user.streak} Day Streak"
                tvUserRank.text = user.rank
                tvUserAvatar.text = initials
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener {
                logout()
            }
            
            btnEditProfile.setOnClickListener {
                showEditProfileDialog()
            }
            
            btnSettings.setOnClickListener {
                navigateToSettings()
            }
        }
    }
    
    private fun showEditProfileDialog() {
        val currentUser = viewModel.user.value
        val currentName = when {
            currentUser?.name?.isNotBlank() == true -> currentUser.name
            !currentUser?.email.isNullOrBlank() -> currentUser?.email?.substringBefore("@") ?: ""
            else -> ""
        }
        
        val dialog = EditProfileDialog.newInstance(
            currentName = currentName,
            onNameSaved = { newName ->
                // Update name in ViewModel
                viewModel.updateUserName(newName)
                
                // Show success feedback
                Toast.makeText(
                    requireContext(),
                    "✅ Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        
        dialog.show(childFragmentManager, "EditProfileDialog")
    }
    
    private fun navigateToSettings() {
        try {
            findNavController().navigate(R.id.action_profile_to_settings)
        } catch (e: Exception) {
            android.util.Log.e("ProfileFragment", "Error navigating to settings", e)
            Toast.makeText(
                requireContext(),
                "Failed to open settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logout() {
        // Clear user session and navigate to sign in
        viewModel.logout()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
