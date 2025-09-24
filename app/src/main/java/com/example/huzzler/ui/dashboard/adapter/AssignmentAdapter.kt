package com.example.huzzler.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.AssignmentPriority
import com.example.huzzler.databinding.ItemAssignmentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AssignmentAdapter(
    private val onAssignmentClick: (Assignment) -> Unit
) : ListAdapter<Assignment, AssignmentAdapter.AssignmentViewHolder>(AssignmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AssignmentViewHolder(
        private val binding: ItemAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Assignment) {
            binding.apply {
                tvAssignmentTitle.text = assignment.title
                tvCourse.text = assignment.course
                tvPoints.text = "+${assignment.points}"
                tvTimeLeft.text = assignment.timeLeft
                
                // Format due date
                val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                tvDueDate.text = "Due: ${dateFormat.format(assignment.dueDate)}"
                
                // Set priority indicator
                val (priorityText, priorityColor) = when (assignment.priority) {
                    AssignmentPriority.PRIME -> "Prime" to R.color.priority_prime
                    AssignmentPriority.GOTTA_DO -> "Gotta Do" to R.color.priority_gotta_do
                    AssignmentPriority.MEDIUM -> "Medium" to R.color.priority_medium
                    AssignmentPriority.LOW -> "Low" to R.color.priority_low
                }
                
                tvPriority.text = priorityText
                tvPriority.setTextColor(ContextCompat.getColor(root.context, priorityColor))
                cardPriority.setCardBackgroundColor(ContextCompat.getColor(root.context, priorityColor))
                
                // Set click listeners
                btnComplete.setOnClickListener {
                    onAssignmentClick(assignment)
                }
                
                btnDetails.setOnClickListener {
                    onAssignmentClick(assignment)
                }
                
                root.setOnClickListener {
                    onAssignmentClick(assignment)
                }
            }
        }
    }

    private class AssignmentDiffCallback : DiffUtil.ItemCallback<Assignment>() {
        override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem == newItem
        }
    }
}
