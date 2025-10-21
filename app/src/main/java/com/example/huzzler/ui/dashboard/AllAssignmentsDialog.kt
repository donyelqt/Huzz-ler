package com.example.huzzler.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.databinding.DialogAllAssignmentsBinding
import com.example.huzzler.ui.dashboard.adapter.AssignmentAdapter

class AllAssignmentsDialog : DialogFragment() {
    
    private var _binding: DialogAllAssignmentsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var assignmentAdapter: AssignmentAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAllAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onStart() {
        super.onStart()
        // Make dialog full screen
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        
        binding.tvAssignmentCount.text = "${assignments.size} Assignments"
    }
    
    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(
            onAssignmentClick = { assignment ->
                assignmentClickCallback?.invoke(assignment)
                dismiss()
            },
            onSubmitClick = { assignment ->
                submitClickCallback?.invoke(assignment)
                dismiss()
            }
        )
        
        binding.rvAllAssignments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = assignmentAdapter
            setHasFixedSize(true)
        }
        
        assignmentAdapter.submitList(assignments)
    }
    
    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear static references to prevent memory leaks
        assignments = emptyList()
        assignmentClickCallback = null
        submitClickCallback = null
    }
    
    companion object {
        private var assignments: List<Assignment> = emptyList()
        private var assignmentClickCallback: ((Assignment) -> Unit)? = null
        private var submitClickCallback: ((Assignment) -> Unit)? = null
        
        fun newInstance(
            assignments: ArrayList<Assignment>,
            onAssignmentClick: (Assignment) -> Unit,
            onSubmitClick: (Assignment) -> Unit
        ): AllAssignmentsDialog {
            this.assignments = assignments
            this.assignmentClickCallback = onAssignmentClick
            this.submitClickCallback = onSubmitClick
            return AllAssignmentsDialog()
        }
    }
}
