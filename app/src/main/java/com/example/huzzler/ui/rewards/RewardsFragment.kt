package com.example.huzzler.ui.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.R
import com.example.huzzler.data.model.RewardCategory
import com.example.huzzler.databinding.FragmentRewardsBinding
import com.example.huzzler.ui.rewards.adapter.RewardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RewardsViewModel by viewModels()
    private lateinit var rewardAdapter: RewardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnBackPressed()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadRewards()
    }

    private fun setupRecyclerView() {
        rewardAdapter = RewardAdapter { reward ->
            viewModel.onRewardClicked(reward)
        }

        binding.rvRewards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rewardAdapter
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                tvAvailablePoints.text = user.points.toString()
                tvRank.text = getString(R.string.rank_template, user.rank)
            }
        }

        viewModel.rewards.observe(viewLifecycleOwner) { rewards ->
            rewardAdapter.submitList(rewards)
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            updateCategorySelection(category)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnGameRewards.setOnClickListener {
                viewModel.selectCategory(RewardCategory.GAME_REWARDS)
            }

            btnAcademicPerks.setOnClickListener {
                viewModel.selectCategory(RewardCategory.ACADEMIC_PERKS)
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back press logic here if needed, otherwise navigate up or pop back stack
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun updateCategorySelection(category: RewardCategory) {
        val activeColor = ContextCompat.getColorStateList(requireContext(), R.color.huzzler_red)
        val inactiveColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
        val activeTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val inactiveTextColor = ContextCompat.getColor(requireContext(), R.color.gray_dark)

        binding.apply {
            when (category) {
                RewardCategory.GAME_REWARDS -> {
                    btnGameRewards.backgroundTintList = activeColor
                    btnGameRewards.setTextColor(activeTextColor)
                    btnAcademicPerks.backgroundTintList = inactiveColor
                    btnAcademicPerks.setTextColor(inactiveTextColor)
                }
                RewardCategory.ACADEMIC_PERKS -> {
                    btnGameRewards.backgroundTintList = inactiveColor
                    btnGameRewards.setTextColor(inactiveTextColor)
                    btnAcademicPerks.backgroundTintList = activeColor
                    btnAcademicPerks.setTextColor(activeTextColor)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
