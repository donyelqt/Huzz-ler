package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.databinding.FragmentAllAssignmentsBinding
import com.example.huzzler.ui.dashboard.adapter.AssignmentAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        setupEventHandling() // Handle ViewModel events
        
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
                // Card/Details click - triggers ViewModel event to show detail dialog
                viewModel.onAssignmentClicked(assignment)
            },
            onSubmitClick = { assignment ->
                // Submit button click - show submission dialog directly
                showSubmissionDialog(assignment)
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
    
    private fun setupEventHandling() {
        // Listen to ViewModel events (same as Dashboard)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is DashboardEvent.NavigateToDetail -> {
                        showAssignmentDetail(event.assignment)
                    }
                    is DashboardEvent.AssignmentCompleted -> {
                        // Show success feedback
                        android.widget.Toast.makeText(
                            requireContext(),
                            "✅ ${event.assignmentTitle} completed! +${event.pointsEarned} points",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                    is DashboardEvent.ShowError -> {
                        showErrorSnackbar(event.message)
                    }
                    else -> {
                        // Ignore other events (like NavigateToNotifications)
                    }
                }
            }
        }
    }
    
    private fun showAssignmentDetail(assignment: Assignment) {
        try {
            val dialog = AssignmentDetailDialog.newInstance(
                assignment = assignment,
                onComplete = { assignmentToComplete ->
                    // Unified UX: All assignments use submission dialog
                    showSubmissionDialog(assignmentToComplete)
                },
                onSubmit = { assignmentToSubmit ->
                    showSubmissionDialog(assignmentToSubmit)
                }
            )
            dialog.show(childFragmentManager, "AssignmentDetailDialog")
        } catch (e: Exception) {
            android.util.Log.e("AllAssignments", "Error showing assignment detail", e)
            showErrorSnackbar("Failed to open assignment details")
        }
    }
    
    private fun showSubmissionDialog(assignment: Assignment) {
        try {
            val dialog = AssignmentSubmissionDialog.newInstance(
                assignment = assignment,
                onSubmitComplete = { submittedAssignment ->
                    // Complete assignment - ViewModel will emit event
                    viewModel.completeAssignment(submittedAssignment)
                }
            )
            dialog.show(childFragmentManager, "AssignmentSubmissionDialog")
        } catch (e: Exception) {
            android.util.Log.e("AllAssignments", "Error showing submission dialog", e)
            showErrorSnackbar("Failed to open submission interface")
        }
    }
    
    private fun showErrorSnackbar(message: String) {
        try {
            android.widget.Toast.makeText(
                requireContext(),
                message,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            android.util.Log.e("AllAssignments", "Error showing error snackbar", e)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
