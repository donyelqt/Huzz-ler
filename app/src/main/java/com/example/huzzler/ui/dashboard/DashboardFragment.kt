package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.databinding.FragmentDashboardBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter { assignment ->
            // Handle assignment click
            viewModel.onAssignmentClicked(assignment)
        }
        
        binding.rvAssignments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = assignmentAdapter
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                tvUserName.text = user.name.ifEmpty { "Doniele Arys" }
                tvPoints.text = user.points.toString()
                tvStreak.text = user.streak.toString()
                tvPrimeRate.text = "${user.primeRate}%"
            }
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
