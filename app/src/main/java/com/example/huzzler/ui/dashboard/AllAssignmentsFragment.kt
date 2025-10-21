package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.databinding.FragmentAllAssignmentsBinding
import com.example.huzzler.ui.dashboard.adapter.AssignmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllAssignmentsFragment : Fragment() {
    
    private var _binding: FragmentAllAssignmentsBinding? = null
    private val binding get() = _binding!!
    
    // Use shared ViewModel from Activity scope to access same data as Dashboard
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var assignmentAdapter: AssignmentAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // Debug: Log ViewModel instance and data
        android.util.Log.d("AllAssignments", "ViewModel instance: ${viewModel.hashCode()}")
        android.util.Log.d("AllAssignments", "Assignments count: ${viewModel.assignments.value?.size ?: 0}")
        
        // Force reload if no data (safety net)
        if (viewModel.assignments.value.isNullOrEmpty()) {
            android.util.Log.w("AllAssignments", "No assignments found, forcing reload...")
            viewModel.loadDashboardData()
        }
    }
    
    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(
            onAssignmentClick = { assignment ->
                viewModel.onAssignmentClicked(assignment)
            },
            onSubmitClick = { assignment ->
                // Navigate back to dashboard, then show submission dialog
                findNavController().popBackStack()
                // Note: The submission dialog will be shown by dashboard after navigation
            }
        )
        
        binding.rvAllAssignments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = assignmentAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    
    private fun setupObservers() {
        // Load current data immediately if available
        viewModel.assignments.value?.let { assignments ->
            android.util.Log.d("AllAssignments", "Initial load: ${assignments.size} assignments")
            assignmentAdapter.submitList(assignments.toList()) // Create new list to trigger update
            binding.tvAssignmentCount.text = "${assignments.size} Assignments"
        }
        
        // Observe for future changes
        viewModel.assignments.observe(viewLifecycleOwner) { assignments ->
            android.util.Log.d("AllAssignments", "Observer triggered: ${assignments?.size ?: 0} assignments")
            assignments?.let {
                assignmentAdapter.submitList(it.toList()) // Create new list to trigger update
                binding.tvAssignmentCount.text = "${it.size} Assignments"
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
