package com.example.huzzler.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.AssignmentCategory
import com.example.huzzler.data.model.AssignmentDifficulty
import com.example.huzzler.data.model.AssignmentPriority
import com.example.huzzler.data.model.AssignmentStatus
import com.example.huzzler.data.model.SubmissionType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Modern Assignment Detail Screen - 2025 UI/UX Design
 * 
 * Enhanced Features:
 * - Hero header matching dashboard design (Huzzler red gradient)
 * - Improved visual hierarchy with better spacing
 * - Animated progress indicators
 * - Modern card designs with subtle shadows
 * - Icon-first information display
 * - Clean, minimalist aesthetic
 * - Consistent with dashboard color palette
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignment: Assignment,
    onBack: () -> Unit,
    onComplete: (Assignment) -> Unit,
    onSubmit: (Assignment) -> Unit = {}
) {
    // Huzzler brand color (matching dashboard)
    val huzzlerRed = Color(0xFFFF1F1F)
    val graySubtle = Color(0xFFF1F2F4)
    
    // Animated progress for completed assignments
    val progressAnimation by animateFloatAsState(
        targetValue = if (assignment.status == AssignmentStatus.COMPLETED) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(graySubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Header (matching dashboard red card)
            HeroHeader(
                assignment = assignment,
                onBack = onBack,
                huzzlerRed = huzzlerRed
            )
            
            // Content Section
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress Card (if completed)
                if (assignment.status == AssignmentStatus.COMPLETED) {
                    CompletedBanner()
                }
                
                // Key Stats Row
                KeyStatsRow(assignment = assignment, huzzlerRed = huzzlerRed)
                
                // Submit Button Section (positioned below stats for better UX)
                if (assignment.status != AssignmentStatus.COMPLETED) {
                    SubmitButtonSection(
                        assignment = assignment,
                        onSubmit = { onSubmit(assignment) }
                    )
                }
                
                // Badges Section
                BadgesSection(assignment = assignment)
                
                // Details Card
                DetailsCard(assignment = assignment, huzzlerRed = huzzlerRed)
                
                // Description Card
                DescriptionCard(assignment = assignment)
                
                // Requirements Card
                RequirementsCard(huzzlerRed = huzzlerRed)
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Submit button section - positioned inline below stats for better UX
 * Prevents content overlap and provides clear call-to-action
 */
@Composable
private fun SubmitButtonSection(
    assignment: Assignment,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981) // Success green
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Submit Assignment",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
        
        // Motivational hint card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Stars,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFF59E0B) // Gold color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tap to submit & earn +${assignment.points} points",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF000000)
                )
            }
        }
    }
}

/**
 * Hero header with gradient background matching dashboard design
 */
@Composable
private fun HeroHeader(
    assignment: Assignment,
    onBack: () -> Unit,
    huzzlerRed: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        huzzlerRed,
                        huzzlerRed.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(top = 16.dp, bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Assignment title
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Course info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = assignment.course,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }
    }
}

/**
 * Completed assignment banner with success message
 */
@Composable
private fun CompletedBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE6F6EC) // Light green
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF10B981), // Success green
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TaskAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Assignment Completed!",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1E8737)
                )
                Text(
                    text = "Great work! Points have been added to your account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1E8737).copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Key stats displayed as prominent cards (dashboard stat card style)
 */
@Composable
private fun KeyStatsRow(assignment: Assignment, huzzlerRed: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Points stat
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Stars,
            value = "+${assignment.points}",
            label = "Points",
            accentColor = huzzlerRed
        )
        
        // Time remaining stat
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.AccessTime,
            value = assignment.timeLeft,
            label = "Time Left",
            accentColor = if (assignment.timeLeft.contains("h") && 
                assignment.timeLeft.replace("[^0-9]".toRegex(), "").toIntOrNull()?.let { it < 24 } == true) 
                Color(0xFFF59E0B) else Color(0xFF10B981)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = accentColor,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Badges section with modern chip design
 */
@Composable
private fun BadgesSection(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Classification",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF000000)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(priority = assignment.priority)
                DifficultyBadge(difficulty = assignment.difficulty)
                CategoryBadge(category = assignment.category)
            }
        }
    }
}

@Composable
private fun DetailsCard(assignment: Assignment, huzzlerRed: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = huzzlerRed.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            tint = huzzlerRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = "Timeline",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF000000)
                )
            }
            
            // Due Date
            ModernDetailRow(
                label = "Due Date",
                value = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                    .format(assignment.dueDate),
                valueColor = Color(0xFF000000)
            )
            
            Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
            
            ModernDetailRow(
                label = "Time Remaining",
                value = assignment.timeLeft,
                valueColor = if (assignment.timeLeft.contains("h") && 
                    assignment.timeLeft.replace("[^0-9]".toRegex(), "").toIntOrNull()?.let { it < 24 } == true)
                    Color(0xFFF59E0B) else Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun ModernDetailRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DescriptionCard(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Description,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF000000)
                )
            }
            
            Text(
                text = generateDescription(assignment),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun RequirementsCard(huzzlerRed: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = huzzlerRed.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = huzzlerRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = "Requirements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF000000)
                )
            }
            
            RequirementItem("Complete all coding exercises")
            RequirementItem("Submit via GitHub classroom")
            RequirementItem("Include unit tests")
            RequirementItem("Add documentation comments")
        }
    }
}

@Composable
private fun RequirementItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF10B981).copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color(0xFF10B981),
                            shape = CircleShape
                        )
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun PriorityBadge(priority: AssignmentPriority) {
    val (label, color) = when (priority) {
        AssignmentPriority.PRIME -> "Prime" to Color(0xFFEF4444)
        AssignmentPriority.GOTTA_DO -> "Gotta Do" to Color(0xFFF59E0B)
        AssignmentPriority.MEDIUM -> "Medium" to Color(0xFF3B82F6)
        AssignmentPriority.LOW -> "Low" to Color(0xFF10B981)
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.15.sp
            ),
            color = color
        )
    }
}

@Composable
private fun DifficultyBadge(difficulty: AssignmentDifficulty) {
    val (label, color) = when (difficulty) {
        AssignmentDifficulty.EASY -> "Easy" to Color(0xFF10B981)
        AssignmentDifficulty.MEDIUM -> "Medium" to Color(0xFFF59E0B)
        AssignmentDifficulty.HARD -> "Hard" to Color(0xFFEF4444)
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SignalCellularAlt,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp
                ),
                color = color
            )
        }
    }
}

@Composable
private fun CategoryBadge(category: AssignmentCategory) {
    val (label, color) = when (category) {
        AssignmentCategory.GAMING -> "Gaming" to Color(0xFFF97316)
        AssignmentCategory.ACADEMIC -> "Academic" to Color(0xFF2563EB)
        AssignmentCategory.PRODUCTIVITY -> "Productivity" to Color(0xFF10B981)
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Category,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                color = color
            )
        }
    }
}

@Composable
private fun StatusBadge() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF10B981).copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color(0xFF10B981)
            )
            Text(
                text = "Completed",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp
                ),
                color = Color(0xFF10B981)
            )
        }
    }
}

private fun generateDescription(assignment: Assignment): String {
    return when {
        assignment.title.contains("Binary Search", ignoreCase = true) -> 
            "Implement a self-balancing binary search tree data structure with insert, delete, and search operations. Your implementation should maintain O(log n) time complexity for all operations and include proper error handling. Submit your code with comprehensive unit tests covering edge cases including empty trees, single-node trees, and balanced/unbalanced scenarios."
        
        assignment.title.contains("Database", ignoreCase = true) -> 
            "Design and implement a relational database schema for a university management system. Include tables for students, courses, enrollments, and grades. Create ER diagrams, normalize to 3NF, write SQL queries for common operations, and implement stored procedures for grade calculations. Include documentation explaining your design decisions."
        
        else -> 
            "Complete this assignment according to the course specifications. Ensure all requirements are met, code is well-documented, and follows best practices. Submit your work before the deadline with proper testing and documentation. Review the rubric carefully to maximize your points."
    }
}
