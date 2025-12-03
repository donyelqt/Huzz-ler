package com.example.huzzler.ui.planner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huzzler.data.model.StudyPlan
import com.example.huzzler.data.model.StudyPriority
import com.example.huzzler.data.model.StudySession
import com.example.huzzler.data.model.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

private val HuzzlerRed = Color(0xFFFF1F1F)
private val HuzzlerRedDark = Color(0xFFCC0000)
private val SuccessGreen = Color(0xFF10B981)
private val WarningOrange = Color(0xFFF59E0B)
private val UrgentRed = Color(0xFFEF4444)
private val InfoBlue = Color(0xFF3B82F6)
private val CardBackground = Color(0xFFFFFFFF)
private val GraySubtle = Color(0xFFF1F2F4)

@Composable
fun StudyPlannerScreen(
    uiState: PlannerUiState,
    onGeneratePlan: () -> Unit,
    onStartFocusSession: (StudySession) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GraySubtle)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            PlannerHeader(onDismiss = onDismiss)

            when {
                uiState.isLoading -> LoadingState()
                uiState.studyPlan != null -> PlanContent(
                    studyPlan = uiState.studyPlan,
                    onStartFocusSession = onStartFocusSession
                )
                else -> EmptyState(onGeneratePlan = onGeneratePlan)
            }
        }

        // Error snackbar
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = UrgentRed
            ) {
                Text(error, color = Color.White)
            }
        }
    }
}

@Composable
private fun PlannerHeader(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HuzzlerRed, HuzzlerRedDark)
                )
            )
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧠 AI Study Planner",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart scheduling powered by AI",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated brain icon
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Text(
                text = "🧠",
                fontSize = 64.sp,
                modifier = Modifier.scale(scale)
            )

            Text(
                text = "Analyzing your assignments...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = HuzzlerRed
            )
        }
    }
}


@Composable
private fun EmptyState(onGeneratePlan: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📅",
                fontSize = 72.sp
            )

            Text(
                text = "Ready to optimize your study time?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Our AI will analyze your assignments and create a personalized study schedule based on deadlines, difficulty, and optimal focus times.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGeneratePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuzzlerRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Generate My Study Plan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlanContent(
    studyPlan: StudyPlan,
    onStartFocusSession: (StudySession) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Insights Card
        item {
            InsightsCard(insights = studyPlan.aiInsights)
        }

        // Stats Row
        item {
            StatsRow(studyPlan = studyPlan)
        }

        // Sessions Header
        item {
            Text(
                text = "📋 Your Study Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Group sessions by date
        val sessionsByDate = studyPlan.sessions.groupBy { session ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(session.suggestedDate)
        }

        sessionsByDate.forEach { (dateKey, sessions) ->
            item {
                DateHeader(date = sessions.first().suggestedDate)
            }

            items(sessions) { session ->
                SessionCard(
                    session = session,
                    onStartFocus = { onStartFocusSession(session) }
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun InsightsCard(insights: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = InfoBlue.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InfoBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = insights,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatsRow(studyPlan: StudyPlan) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = "📚",
            value = "${studyPlan.sessions.size}",
            label = "Sessions"
        )
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = "⏱️",
            value = String.format("%.1fh", studyPlan.totalEstimatedHours),
            label = "Total Time"
        )
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = "🔥",
            value = "${studyPlan.sessions.count { it.priority == StudyPriority.URGENT }}",
            label = "Urgent"
        )
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DateHeader(date: Date) {
    val calendar = Calendar.getInstance().apply { time = date }
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }

    val dateText = when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, tomorrow) -> "Tomorrow"
        else -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date)
    }

    Text(
        text = dateText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = HuzzlerRed,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}


@Composable
private fun SessionCard(
    session: StudySession,
    onStartFocus: () -> Unit
) {
    val priorityColor = when (session.priority) {
        StudyPriority.URGENT -> UrgentRed
        StudyPriority.HIGH -> WarningOrange
        StudyPriority.MEDIUM -> InfoBlue
        StudyPriority.LOW -> SuccessGreen
    }

    val timeSlotIcon = when (session.suggestedTimeSlot) {
        TimeSlot.EARLY_MORNING -> "🌅"
        TimeSlot.MORNING -> "☀️"
        TimeSlot.AFTERNOON -> "🌤️"
        TimeSlot.EVENING -> "🌆"
        TimeSlot.NIGHT -> "🌙"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Time slot and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = timeSlotIcon, fontSize = 20.sp)
                    Text(
                        text = session.suggestedTimeSlot.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = session.priority.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = priorityColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Assignment title
            Text(
                text = session.assignmentTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Duration
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Rounded.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "${session.durationMinutes} min focus session",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Reason
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GraySubtle
            ) {
                Text(
                    text = session.reason,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start Focus Button
            Button(
                onClick = onStartFocus,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Focus Session",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
