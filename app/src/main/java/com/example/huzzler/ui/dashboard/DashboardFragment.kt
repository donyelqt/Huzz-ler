package com.example.huzzler.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.databinding.FragmentDashboardBinding
import com.example.huzzler.databinding.LayoutDashboardStatCardBinding
import com.example.huzzler.ui.dashboard.adapter.AssignmentAdapter
import com.example.huzzler.ui.theme.HuzzlerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        assignmentAdapter = AssignmentAdapter(
            onAssignmentClick = { assignment ->
                // Card/Details click - show detail dialog
                viewModel.onAssignmentClicked(assignment)
            },
            onSubmitClick = { assignment ->
                // Submit button click - go directly to submission dialog
                showSubmissionDialog(assignment)
            }
        )
        
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
        // Event collector for navigation and snackbars
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is DashboardEvent.NavigateToDetail -> {
                        showAssignmentDetail(event.assignment)
                    }
                    is DashboardEvent.NavigateToNotifications -> {
                        showNotifications(event.notifications)
                    }
                    is DashboardEvent.AssignmentCompleted -> {
                        showSuccessSnackbar(
                            title = event.assignmentTitle,
                            points = event.pointsEarned,
                            total = event.totalPoints
                        )
                    }
                    is DashboardEvent.ShowError -> {
                        showErrorSnackbar(event.message)
                    }
                }
            }
        }
        
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
                // Navigate to all assignments (future implementation)
            }
            
            btnNotification.setOnClickListener {
                viewModel.onNotificationClicked()
            }
        }
    }
    
    private fun showAssignmentDetail(assignment: Assignment) {
        try {
            val dialog = AssignmentDetailDialog.newInstance(
                assignment = assignment,
                onComplete = { assignmentToComplete ->
                    // Unified UX: All assignments use submission dialog
                    // For COMPLETE_ONLY, submission is optional (can submit empty)
                    showSubmissionDialog(assignmentToComplete)
                },
                onSubmit = { assignmentToSubmit ->
                    showSubmissionDialog(assignmentToSubmit)
                }
            )
            dialog.show(childFragmentManager, "AssignmentDetailDialog")
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing assignment detail", e)
            showErrorSnackbar("Failed to open assignment details")
        }
    }
    
    private fun showSubmissionDialog(assignment: Assignment) {
        try {
            val dialog = AssignmentSubmissionDialog.newInstance(
                assignment = assignment,
                onSubmitComplete = { submittedAssignment ->
                    // Complete assignment and show custom success snackbar
                    viewModel.completeAssignment(submittedAssignment)
                    // Get current total points from user
                    val currentTotal = viewModel.user.value?.points ?: 0
                    showSuccessSnackbar(
                        title = "Assignment Completed!",
                        points = submittedAssignment.points,
                        total = currentTotal + submittedAssignment.points
                    )
                }
            )
            dialog.show(childFragmentManager, "AssignmentSubmissionDialog")
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing submission dialog", e)
            showErrorSnackbar("Failed to open submission interface")
        }
    }
    
    private fun showNotifications(notifications: List<com.example.huzzler.data.model.Notification>) {
        try {
            val dialog = NotificationsDialog.newInstance(notifications)
            dialog.show(childFragmentManager, "NotificationsDialog")
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing notifications", e)
            showErrorSnackbar("Failed to open notifications")
        }
    }
    
    private fun showCompletionDialog(assignment: Assignment) {
        try {
            AlertDialog.Builder(requireContext())
                .setTitle("Complete Assignment")
                .setMessage("Mark \"${assignment.title}\" as complete?\n\nYou'll earn +${assignment.points} points!")
                .setPositiveButton("Complete") { dialog, _ ->
                    viewModel.completeAssignment(assignment)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing completion dialog", e)
            showErrorSnackbar("Failed to show completion dialog")
        }
    }
    
    private fun showSuccessSnackbar(title: String, points: Int, total: Int) {
        try {
            // Get activity's root view which is a proper ViewGroup
            val activityRoot = activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
            if (activityRoot == null) {
                android.util.Log.e("DashboardFragment", "Activity root not found")
                return
            }
            
            val snackbarContainer = ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()
                    
                    LaunchedEffect(Unit) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "SUCCESS",
                                duration = androidx.compose.material3.SnackbarDuration.Short
                            )
                        }
                    }
                    
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ModernSnackbar(
                                title = "Completed!",
                                message = "$title • $total pts total",
                                isSuccess = true
                            )
                        }
                    }
                }
            }
            
            activityRoot.addView(snackbarContainer)
            
            // Auto-remove after delay
            snackbarContainer.postDelayed({
                activityRoot.removeView(snackbarContainer)
            }, 3000)
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing success snackbar", e)
        }
    }
    
    private fun showErrorSnackbar(message: String) {
        try {
            // Get activity's root view which is a proper ViewGroup
            val activityRoot = activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
            if (activityRoot == null) {
                android.util.Log.e("DashboardFragment", "Activity root not found")
                return
            }
            
            val snackbarContainer = ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()
                    
                    LaunchedEffect(Unit) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "ERROR",
                                duration = androidx.compose.material3.SnackbarDuration.Long
                            )
                        }
                    }
                    
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ModernSnackbar(
                                title = "Failed",
                                message = message,
                                isSuccess = false
                            )
                        }
                    }
                }
            }
            
            activityRoot.addView(snackbarContainer)
            
            // Auto-remove after delay
            snackbarContainer.postDelayed({
                activityRoot.removeView(snackbarContainer)
            }, 5000)
        } catch (e: Exception) {
            android.util.Log.e("DashboardFragment", "Error showing error snackbar", e)
        }
    }
    
    @androidx.compose.runtime.Composable
    private fun ModernSnackbar(
        title: String,
        message: String,
        isSuccess: Boolean
    ) {
        val containerColor = if (isSuccess) {
            Color(0xFF10B981) // Emerald green
        } else {
            Color(0xFFEF4444) // Rose red
        }
        
        Surface(
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.15.sp
                        ),
                        color = Color.White
                    )
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            letterSpacing = 0.25.sp
                        ),
                        color = Color.White.copy(alpha = 0.92f),
                        maxLines = 2
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
