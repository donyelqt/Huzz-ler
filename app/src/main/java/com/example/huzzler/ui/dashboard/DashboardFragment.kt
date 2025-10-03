package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.R
import com.example.huzzler.databinding.FragmentDashboardBinding
import com.example.huzzler.databinding.LayoutDashboardStatCardBinding
import com.example.huzzler.ui.dashboard.adapter.AssignmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var assignmentAdapter: AssignmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bindStatCard(
        statCardBinding: LayoutDashboardStatCardBinding,
        value: String,
        @androidx.annotation.DrawableRes iconRes: Int,
        @androidx.annotation.StringRes captionRes: Int,
        @androidx.annotation.ColorRes accentColorRes: Int
    ) {
        statCardBinding.apply {
            tvStatValue.text = value
            tvStatCaption.text = getString(captionRes)
            ivStatIcon.setImageResource(iconRes)

            val accentColor = ContextCompat.getColor(requireContext(), accentColorRes)
            tvStatValue.setTextColor(accentColor)
            ivStatIcon.imageTintList = android.content.res.ColorStateList.valueOf(accentColor)
            tvStatCaption.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter { assignment ->
            // Handle assignment click
            viewModel.onAssignmentClicked(assignment)
        }
        
        binding.rvAssignments.apply {
            layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean = false
            }
            adapter = assignmentAdapter
            setHasFixedSize(false)
            itemAnimator = null
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                tvUserName.text = user.name.ifEmpty { "Doniele Arys" }
            }

            bindStatCard(
                LayoutDashboardStatCardBinding.bind(binding.includePoints.root),
                user.points.toString(),
                R.drawable.ic_points,
                R.string.metric_points,
                R.color.dashboard_stat_accent_points
            )

            bindStatCard(
                LayoutDashboardStatCardBinding.bind(binding.includeStreak.root),
                user.streak.toString(),
                R.drawable.ic_schedule,
                R.string.metric_day_streak,
                R.color.dashboard_stat_accent_streak
            )

            bindStatCard(
                LayoutDashboardStatCardBinding.bind(binding.includePrimeRate.root),
                "${user.primeRate}%",
                R.drawable.ic_rewards,
                R.string.metric_prime_rate,
                R.color.dashboard_stat_accent_prime
            )
        }

        viewModel.assignments.observe(viewLifecycleOwner) { assignments ->
            assignmentAdapter.submitList(assignments)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            tvViewAll.setOnClickListener {
                // Navigate to all assignments
            }
            
            btnNotification.setOnClickListener {
                // Handle notification click
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
