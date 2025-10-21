package com.example.huzzler.ui.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huzzler.data.model.Assignment

/**
 * Modern Assignment Submission Screen
 * 
 * Features:
 * - File upload with drag-and-drop UI
 * - Rich text entry with character counter
 * - Multiple file attachment support
 * - Real-time validation
 * - Elegant progress states
 * - Confirmation flow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentSubmissionScreen(
    assignment: Assignment,
    onBack: () -> Unit,
    onSubmit: (Assignment, List<String>, String) -> Unit
) {
    val huzzlerRed = Color(0xFFFF1F1F)
    val successGreen = Color(0xFF10B981)
    val graySubtle = Color(0xFFF1F2F4)
    
    val context = LocalContext.current
    val attachedFiles = remember { mutableStateListOf<String>() }
    var submissionText by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    
    // Determine if submission is required based on assignment type
    val isSubmissionRequired = assignment.submissionType == com.example.huzzler.data.model.SubmissionType.REQUIRES_SUBMISSION
    
    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            attachedFiles.add(uri.lastPathSegment ?: "Unknown file")
        }
    }
    
    // For REQUIRES_SUBMISSION: need files OR text
    // For COMPLETE_ONLY: optional (can submit empty for quick completion)
    val isSubmissionValid = if (isSubmissionRequired) {
        attachedFiles.isNotEmpty() || submissionText.trim().isNotEmpty()
    } else {
        true // Always valid for optional submissions
    }
    
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
            // Hero Header
            SubmissionHeader(
                assignment = assignment,
                onBack = onBack,
                huzzlerRed = huzzlerRed
            )
            
            // Content Section
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Instructions Card
                InstructionsCard(assignment = assignment)
                
                // File Upload Section
                FileUploadCard(
                    attachedFiles = attachedFiles,
                    onAttachClick = { filePickerLauncher.launch("*/*") },
                    onRemoveFile = { attachedFiles.remove(it) }
                )
                
                // Text Entry Section
                TextEntryCard(
                    text = submissionText,
                    onTextChange = { submissionText = it }
                )
                
                // Submit Button
                Button(
                    onClick = {
                        // Don't call onSubmit yet - wait for user to click Done
                        // onSubmit(assignment, attachedFiles.toList(), submissionText)
                        showSuccess = true
                    },
                    enabled = isSubmissionValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = successGreen,
                        disabledContainerColor = Color(0xFFE6E6E6)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Send,
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
                
                // Validation hint - only show for required submissions
                if (isSubmissionRequired && !isSubmissionValid) {
                    Text(
                        text = "⚠️ Please attach at least one file or add submission text",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Optional submission hint
                if (!isSubmissionRequired && attachedFiles.isEmpty() && submissionText.trim().isEmpty()) {
                    Text(
                        text = "💡 Tip: You can submit without files or text, or add optional evidence",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Success animation overlay
        AnimatedVisibility(
            visible = showSuccess,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            SuccessOverlay(onDone = {
                // Call onSubmit when user clicks Done (not on initial submit)
                onSubmit(assignment, attachedFiles.toList(), submissionText)
                showSuccess = false
                onBack()
            })
        }
    }
}

@Composable
private fun SubmissionHeader(
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
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "Submit Assignment",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard(assignment: Assignment) {
    val isRequired = assignment.submissionType == com.example.huzzler.data.model.SubmissionType.REQUIRES_SUBMISSION
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRequired) Color(0xFFE3F2FD) else Color(0xFFE6F6EC) // Blue for required, Green for optional
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isRequired) Color(0xFF2196F3) else Color(0xFF10B981),
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isRequired) "Submission Required" else "Optional Submission",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isRequired) Color(0xFF1565C0) else Color(0xFF047857)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRequired) {
                        "This assignment requires evidence of completion. Upload your work files and/or provide a written response."
                    } else {
                        "You can complete this assignment instantly, or optionally attach files/notes as proof of work."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRequired) Color(0xFF1565C0).copy(alpha = 0.8f) else Color(0xFF047857).copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FileUploadCard(
    attachedFiles: List<String>,
    onAttachClick: () -> Unit,
    onRemoveFile: (String) -> Unit
) {
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
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AttachFile,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = "Attach Files",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF000000)
                )
                
                if (attachedFiles.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = attachedFiles.size.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Upload zone
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAttachClick),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F2F4),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = Color(0xFF10B981).copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.UploadFile,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "Click to upload files",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF000000)
                    )
                    
                    Text(
                        text = "PDF, DOC, ZIP, or any file type",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // Attached files list
            if (attachedFiles.isNotEmpty()) {
                Divider(color = Color(0xFFE6E6E6))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attachedFiles.forEach { file ->
                        FileItem(fileName = file, onRemove = { onRemoveFile(file) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItem(fileName: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF1F2F4)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF10B981).copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.InsertDriveFile,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF000000),
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextEntryCard(text: String, onTextChange: (String) -> Unit) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
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
                        text = "Written Response",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF000000)
                    )
                }
                
                Text(
                    text = "${text.length}/2000",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (text.length > 2000) Color(0xFFDC2626) else Color(0xFF666666)
                )
            }
            
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 2000) onTextChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = {
                    Text(
                        text = "Type your submission here...\n\nInclude any relevant notes, answers, or explanations for your work.",
                        color = Color(0xFF999999)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFFE6E6E6),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
private fun SuccessOverlay(onDone: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Text(
                    text = "Submitted Successfully!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF000000)
                )
                
                Text(
                    text = "Your assignment has been submitted and will be reviewed shortly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}
