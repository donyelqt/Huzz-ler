package com.example.huzzler.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huzzler.data.model.Notification
import com.example.huzzler.data.model.NotificationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Modern Notifications Screen - 2025 UI/UX Design
 * 
 * Design Principles:
 * ✨ Glassmorphism - Subtle transparency and blur effects
 * 🎨 Minimalist - Clean, spacious, breathing room
 * 🌊 Smooth Animations - Polished micro-interactions
 * 🎯 Visual Hierarchy - Clear information architecture
 * 🌈 Semantic Colors - Intuitive color coding
 * 
 * Inspired by: iOS 17, Telegram, Linear, Arc Browser
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<Notification>,
    onBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit = {}
) {
    val unreadCount = notifications.count { !it.isRead }
    
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing), // Fix status bar overlap
        containerColor = Color(0xFFF8FAFC), // Subtle gray background (2025 trend)
        topBar = {
            // Elevated glassmorphic header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 0.5.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Modern back button
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF1E293B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        // Title with modern typography
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color(0xFF0F172A)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Modern badge
                        if (unreadCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            EmptyNotificationsState(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Tighter spacing (2025 trend)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        NotificationCard(
                            notification = notification,
                            onClick = { onNotificationClick(notification) }
                        )
                    }
                }
                
                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    val (icon, containerColor, contentColor) = getNotificationStyle(notification.type)
    
    // 2025 Design: Consistent white cards with subtle elevation
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp), // Softer corners
        color = Color.White, // Consistent white for all cards
        shadowElevation = if (!notification.isRead) 1.dp else 0.5.dp,
        tonalElevation = if (!notification.isRead) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Modern icon with gradient background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = containerColor.copy(alpha = 0.15f), // Softer background
                        shape = RoundedCornerShape(12.dp) // Rounded square (2025 trend)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            // Content with improved spacing
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp) // Better breathing room
            ) {
                // Title with modern typography
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = notification.title,
                        modifier = Modifier.weight(1f, fill = false),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Unread indicator (modern placement)
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = Color(0xFF3B82F6),
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                // Message with better readability
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Timestamp (minimalist)
                Text(
                    text = formatRelativeTime(notification.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 64.dp)
        ) {
            // Modern illustration-style icon (2025 trend)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title with modern typography
            Text(
                text = "All Caught Up!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF0F172A)
            )
            
            // Description with softer color
            Text(
                text = "You're up to date with all notifications.\nWe'll let you know when something new arrives.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = Color(0xFF64748B),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun getNotificationStyle(type: NotificationType): Triple<ImageVector, Color, Color> {
    // 2025 Color Palette: Softer, more sophisticated colors
    return when (type) {
        NotificationType.ASSIGNMENT_DUE_SOON -> Triple(
            Icons.Rounded.AccessTime,
            Color(0xFFFBBF24), // Modern amber
            Color(0xFFD97706)  // Rich amber
        )
        NotificationType.ASSIGNMENT_OVERDUE -> Triple(
            Icons.Rounded.Warning,
            Color(0xFFF87171), // Soft red
            Color(0xFFDC2626)  // Bold red
        )
        NotificationType.POINTS_EARNED -> Triple(
            Icons.Rounded.EmojiEvents,
            Color(0xFF34D399), // Fresh green
            Color(0xFF059669)  // Deep green
        )
        NotificationType.STREAK_MILESTONE -> Triple(
            Icons.Rounded.LocalFireDepartment,
            Color(0xFFA78BFA), // Soft purple
            Color(0xFF7C3AED)  // Vibrant purple
        )
        NotificationType.NEW_REWARD_AVAILABLE -> Triple(
            Icons.Rounded.AutoAwesome,
            Color(0xFF60A5FA), // Sky blue
            Color(0xFF2563EB)  // Rich blue
        )
        NotificationType.ASSIGNMENT_GRADED -> Triple(
            Icons.Rounded.Assignment,
            Color(0xFF3B82F6), // Primary blue
            Color(0xFF1D4ED8)  // Deep blue
        )
        NotificationType.SYSTEM_UPDATE -> Triple(
            Icons.Rounded.Info,
            Color(0xFF94A3B8), // Modern gray
            Color(0xFF475569)  // Slate gray
        )
    }
}

private fun formatRelativeTime(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    
    return when {
        diffInMillis < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            "$minutes${if (minutes == 1L) " min" else " mins"} ago"
        }
        diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            "$hours${if (hours == 1L) " hour" else " hours"} ago"
        }
        diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            "$days${if (days == 1L) " day" else " days"} ago"
        }
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
    }
}
