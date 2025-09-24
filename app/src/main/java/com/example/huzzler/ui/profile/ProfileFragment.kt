package com.example.huzzler.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
            binding.apply {
                tvUserName.text = user.name.ifEmpty { "Doniele Arys" }
                tvUserEmail.text = user.email
                tvUserPoints.text = "${user.points} Points"
                tvUserStreak.text = "${user.streak} Day Streak"
                tvUserRank.text = user.rank
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener {
                logout()
            }
            
            btnEditProfile.setOnClickListener {
                // Handle edit profile
            }
            
            btnSettings.setOnClickListener {
                // Handle settings
            }
        }
    }

    private fun logout() {
        // Clear user session and navigate to sign in
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
