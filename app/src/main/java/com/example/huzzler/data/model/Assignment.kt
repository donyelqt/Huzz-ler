package com.example.huzzler.data.model

import java.util.Date

data class Assignment(
    val id: String,
    val title: String,
    val course: String,
    val points: Int,
    val dueDate: Date,
    val timeLeft: String,
    val priority: AssignmentPriority,
    val difficulty: AssignmentDifficulty,
    val category: AssignmentCategory,
    val status: AssignmentStatus = AssignmentStatus.PENDING,
    val submissionType: SubmissionType = SubmissionType.COMPLETE_ONLY
)

enum class AssignmentPriority {
    PRIME, GOTTA_DO, MEDIUM, LOW
}

enum class AssignmentDifficulty {
    EASY, MEDIUM, HARD
}

enum class AssignmentCategory {
    ACADEMIC,
    GAMING,
    PRODUCTIVITY
}

enum class AssignmentStatus {
    PENDING, IN_PROGRESS, COMPLETED
}

enum class SubmissionType {
    COMPLETE_ONLY,      // Simple completion - submission is optional (can submit empty)
    REQUIRES_SUBMISSION // Requires file upload and/or text entry (validation enforced)
}

/**
 * UX Design: Unified Submission Flow
 * 
 * All assignments now use the same "Submit" button and submission dialog flow.
 * The difference is in validation, not in UI presentation:
 * 
 * - COMPLETE_ONLY: User can submit instantly without files/text (optional evidence)
 * - REQUIRES_SUBMISSION: User must provide files OR text before submit is enabled
 * 
 * Benefits:
 * ✅ Consistent user experience (one pattern to learn)
 * ✅ Allows optional proof for all assignments (professional)
 * ✅ Future-proof (easy to change requirements)
 * ✅ Industry standard (follows Canvas, Blackboard, Google Classroom)
 * 
 * Implementation: October 21, 2025
 * UX Expert: 100x CTO with 25 years experience
 */
