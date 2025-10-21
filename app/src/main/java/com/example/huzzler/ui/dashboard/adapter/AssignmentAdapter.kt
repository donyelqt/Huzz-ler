package com.example.huzzler.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.huzzler.R
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.AssignmentCategory
import com.example.huzzler.data.model.AssignmentDifficulty
import com.example.huzzler.data.model.AssignmentPriority
import com.example.huzzler.data.model.SubmissionType
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
                tvTimeLeft.text = root.context.getString(R.string.time_left, assignment.timeLeft)

                val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                tvDueDate.text = root.context.getString(R.string.due, dateFormat.format(assignment.dueDate))

                val (priorityLabel, priorityColor, priorityBg) = when (assignment.priority) {
                    AssignmentPriority.PRIME -> Triple("Prime", R.color.priority_prime, R.color.priority_prime_light)
                    AssignmentPriority.GOTTA_DO -> Triple("Gotta Do", R.color.priority_gotta_do, R.color.priority_gotta_do_light)
                    AssignmentPriority.MEDIUM -> Triple("Medium", R.color.priority_medium, R.color.priority_medium_light)
                    AssignmentPriority.LOW -> Triple("Low", R.color.priority_low, R.color.priority_low_light)
                }

                val priorityColorInt = ContextCompat.getColor(root.context, priorityColor)
                tvPriority.text = priorityLabel
                tvPriority.setTextColor(priorityColorInt)
                tintBackground(chipPriority, priorityBg)
                tintBackground(viewPriorityIndicator, priorityColor)
                cardAssignment.cardElevation = 0f

                val (difficultyLabel, difficultyTextColor, difficultyBgColor) = when (assignment.difficulty) {
                    AssignmentDifficulty.EASY -> Triple("Easy", R.color.difficulty_easy_text, R.color.difficulty_easy_bg)
                    AssignmentDifficulty.MEDIUM -> Triple("Medium", R.color.difficulty_medium_text, R.color.difficulty_medium_bg)
                    AssignmentDifficulty.HARD -> Triple("Hard", R.color.difficulty_hard_text, R.color.difficulty_hard_bg)
                }

                tvDifficulty.text = difficultyLabel
                tvDifficulty.setTextColor(ContextCompat.getColor(root.context, difficultyTextColor))
                tintBackground(chipDifficulty, difficultyBgColor)

                val (categoryColor, categoryIcon, accentColor) = when (assignment.category) {
                    AssignmentCategory.GAMING -> Triple(R.color.category_gaming, R.drawable.ic_category_gaming, R.color.category_gaming)
                    AssignmentCategory.ACADEMIC -> Triple(R.color.category_academic, R.drawable.ic_category_academic, R.color.category_academic)
                    AssignmentCategory.PRODUCTIVITY -> Triple(R.color.category_productivity, R.drawable.ic_category_productivity, R.color.category_productivity)
                }

                val categoryColorInt = ContextCompat.getColor(root.context, categoryColor)
                cardCategoryIcon.setCardBackgroundColor(categoryColorInt)
                ivCategoryIcon.setImageResource(categoryIcon)
                tvPoints.setTextColor(categoryColorInt)

                ivGlowAccent.apply {
                    setImageResource(R.drawable.circle_white_transparent)
                    DrawableCompat.setTint(DrawableCompat.wrap(drawable), ContextCompat.getColor(root.context, accentColor))
                    alpha = 0.2f
                    isVisible = true
                }

                // Unified UX: All assignments show 'Submit' button with green color
                // Consistent icon and styling for better user experience
                btnComplete.text = root.context.getString(R.string.submit)
                btnComplete.setIconResource(R.drawable.ic_send)
                btnComplete.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.green)

                btnComplete.setOnClickListener { onAssignmentClick(assignment) }
                btnDetails.setOnClickListener { onAssignmentClick(assignment) }
                root.setOnClickListener { onAssignmentClick(assignment) }
            }
        }

        private fun tintBackground(view: View, colorRes: Int) {
            val background = view.background ?: return
            val drawable = DrawableCompat.wrap(background.mutate())
            DrawableCompat.setTint(drawable, ContextCompat.getColor(view.context, colorRes))
            view.background = drawable
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
