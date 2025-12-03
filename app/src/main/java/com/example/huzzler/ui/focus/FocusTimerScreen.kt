package com.example.huzzler.ui.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huzzler.data.model.Assignment
import com.example.huzzler.data.model.TimerState

// Colors
private val HuzzlerRed = Color(0xFFFF1F1F)
private val HuzzlerRedDark = Color(0xFFCC0000)
private val BreakGreen = Color(0xFF10B981)
private val BreakGreenDark = Color(0xFF059669)
private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)

@Composable
fun FocusTimerScreen(
    timerState: TimerState,
    assignment: Assignment?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSkipBreak: () -> Unit,
    onDismiss: () -> Unit
) {
    val primaryColor by animateColorAsState(
        targetValue = if (timerState.isBreak) BreakGreen else HuzzlerRed,
        animationSpec = tween(500),
        label = "primaryColor"
    )

    val gradientColors = if (timerState.isBreak) {
        listOf(BreakGreenDark, BreakGreen)
    } else {
        listOf(HuzzlerRedDark, HuzzlerRed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (timerState.isBreak) "Break Time" else "Focus Mode",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Assignment info card (if any)
            assignment?.let {
                AssignmentInfoCard(assignment = it, primaryColor = primaryColor)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Session counter
            SessionCounter(
                sessionCount = timerState.sessionCount,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main timer circle
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularTimer(
                    progress = timerState.progress,
                    primaryColor = primaryColor,
                    isRunning = timerState.isRunning && !timerState.isPaused
                )
                
                TimerDisplay(
                    remainingSeconds = timerState.remainingSeconds,
                    isBreak = timerState.isBreak,
                    primaryColor = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Control buttons
            TimerControls(
                timerState = timerState,
                primaryColor = primaryColor,
                gradientColors = gradientColors,
                onStart = onStart,
                onPause = onPause,
                onResume = onResume,
                onStop = onStop,
                onSkipBreak = onSkipBreak
            )

            Spacer(modifier = Modifier.weight(1f))

            // Motivational text
            MotivationalText(
                isRunning = timerState.isRunning,
                isPaused = timerState.isPaused,
                isBreak = timerState.isBreak
            )
        }
    }
}


@Composable
private fun AssignmentInfoCard(assignment: Assignment, primaryColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📚",
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Focusing on",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${assignment.points}",
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "bonus pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun SessionCounter(sessionCount: Int, primaryColor: Color) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val isCompleted = index < (sessionCount % 4)
            val isCurrent = index == (sessionCount % 4)
            
            Box(
                modifier = Modifier
                    .size(if (isCurrent) 14.dp else 12.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> primaryColor
                            isCurrent -> primaryColor.copy(alpha = 0.5f)
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }
                    )
            )
            if (index < 3) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "$sessionCount sessions",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun CircularTimer(
    progress: Float,
    primaryColor: Color,
    isRunning: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    // Pulsing animation when running
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .scale(pulseScale)
    ) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background circle
        drawCircle(
            color = Color.Gray.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        val sweepAngle = 360f * animatedProgress
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Glow effect
        if (isRunning) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.1f),
                radius = radius + strokeWidth,
                center = center
            )
        }
    }
}


@Composable
private fun TimerDisplay(
    remainingSeconds: Int,
    isBreak: Boolean,
    primaryColor: Color
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeText,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 4.sp
        )
        Text(
            text = if (isBreak) "Take a breather" else "Stay focused",
            style = MaterialTheme.typography.bodyMedium,
            color = primaryColor
        )
    }
}

@Composable
private fun TimerControls(
    timerState: TimerState,
    primaryColor: Color,
    gradientColors: List<Color>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSkipBreak: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            // Not started - show start button
            !timerState.isRunning && !timerState.isPaused -> {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .height(64.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Focus",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Running - show pause and stop
            timerState.isRunning && !timerState.isPaused -> {
                // Stop button
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Stop",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Pause button
                Button(
                    onClick = onPause,
                    modifier = Modifier.size(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Pause,
                        contentDescription = "Pause",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Skip break button (only during break)
                if (timerState.isBreak) {
                    OutlinedButton(
                        onClick = onSkipBreak,
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = primaryColor
                        )
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Skip Break",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }

            // Paused - show resume and stop
            timerState.isPaused -> {
                // Stop button
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Stop",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Resume button
                Button(
                    onClick = onResume,
                    modifier = Modifier.size(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Resume",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
private fun MotivationalText(
    isRunning: Boolean,
    isPaused: Boolean,
    isBreak: Boolean
) {
    val text = when {
        !isRunning && !isPaused -> "Ready to crush it? 💪"
        isPaused -> "Paused. Take your time."
        isBreak -> "You earned this break! 🎉"
        else -> "You're doing amazing! 🔥"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}
