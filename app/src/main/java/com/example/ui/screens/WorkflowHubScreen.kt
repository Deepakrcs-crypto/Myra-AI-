package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkflowTask
import com.example.ui.components.GlowingOrb
import com.example.ui.components.OrbState
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel

@Composable
fun WorkflowHubScreen(
    viewModel: MyraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val orbState by viewModel.orbState.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskCategory by remember { mutableStateOf("General") }
    var newTaskPriority by remember { mutableStateOf("Normal") }

    var showRenameDialog by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf(config.name) }

    val completedTasksCount = tasks.count { it.isCompleted }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Workflow Task", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("e.g., Debug Compose Memory Leak", color = Color(0xFF64748B)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_new_task_title")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Category", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("General", "Coding", "Travel", "Work").forEach { cat ->
                            val isSel = newTaskCategory == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) RadiantViolet else Color(0xFF1E293B),
                                modifier = Modifier.clickable { newTaskCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Priority", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("High", "Normal", "Low").forEach { pri ->
                            val isSel = newTaskPriority == pri
                            val priColor = if (pri == "High") GlowingRose else if (pri == "Normal") GlowingAmber else RadiantEmerald
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) priColor.copy(alpha = 0.3f) else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) priColor else Color.Transparent),
                                modifier = Modifier.clickable { newTaskPriority = pri }
                            ) {
                                Text(
                                    text = pri,
                                    color = if (isSel) priColor else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle, newTaskCategory, newTaskPriority)
                            newTaskTitle = ""
                        }
                        showAddTaskDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantViolet),
                    modifier = Modifier.testTag("btn_confirm_add_task")
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D33)
        )
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Customize Assistant Identity", color = Color.White) },
            text = {
                Column {
                    Text(
                        "Change your AI companion's name anytime. ${config.name} will immediately respond to this new identity.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editNameText,
                        onValueChange = { editNameText = it },
                        placeholder = { Text("Enter name", color = Color(0xFF64748B)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_rename_modal")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editNameText.isNotBlank()) {
                            viewModel.setAssistantName(editNameText)
                        }
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantCyan),
                    modifier = Modifier.testTag("btn_save_identity")
                ) {
                    Text("Save Identity", color = Color(0xFF090D16))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D33)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("workflow_hub_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ecosystem & Workflow Hub",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Task orchestration, context memory & environment",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(RadiantEmerald))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Operational", color = RadiantEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Assistant Identity Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlowingOrb(state = orbState, size = 44.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = config.name,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (config.name.equals("Myra", ignoreCase = true)) {
                                    Text(
                                        text = " (${config.nativeName})",
                                        color = RadiantCyan,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = "AI Companion & Workflow Core",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            editNameText = config.name
                            showRenameDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_rename_in_hub")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rename", color = RadiantCyan, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Persona Mode Chips
                Text("ASSISTANT PERSONA MODE", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Balanced", "Coding Expert", "Travel Polyglot", "Creative").forEach { mode ->
                        val isSel = config.personaMode == mode
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) RadiantViolet.copy(alpha = 0.25f) else Color(0xFF10172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RadiantViolet else Color(0xFF1E293B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setPersonaMode(mode) }
                        ) {
                            Text(
                                text = mode,
                                color = if (isSel) Color.White else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggles: Voice & Standby
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Voice Output (TTS)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Speaks responses aloud", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = config.voiceEnabled,
                        onCheckedChange = { viewModel.toggleVoiceEnabled() },
                        colors = SwitchDefaults.colors(checkedThumbColor = RadiantCyan, checkedTrackColor = Color(0xFF0E4A66))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NightlightRound, contentDescription = null, tint = RadiantViolet, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Sleep & Background Standby", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Minimal battery, continuous listening", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = { viewModel.enterSleepMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1065)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_trigger_sleep_hub")
                    ) {
                        Text("Sleep", color = RadiantViolet, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ecosystem Tasks Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Ecosystem Tasks", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("$completedTasksCount of ${tasks.size} tasks completed", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }

            Button(
                onClick = { showAddTaskDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = RadiantCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_add_task_trigger")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF090D16), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Task", color = Color(0xFF090D16), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Task Items List
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
                val priorityColor = if (task.priority == "High") GlowingRose else if (task.priority == "Normal") GlowingAmber else RadiantEmerald
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth().testTag("task_row_${task.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTask(task) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = RadiantEmerald,
                                    uncheckedColor = Color(0xFF64748B)
                                )
                            )

                            Column {
                                Text(
                                    text = task.title,
                                    color = if (task.isCompleted) Color(0xFF64748B) else Color.White,
                                    fontSize = 13.sp,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(task.category, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    Text("•", color = Color(0xFF475569), fontSize = 10.sp)
                                    Text(task.priority, color = priorityColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteTask(task) },
                            modifier = Modifier.size(28.dp).testTag("btn_delete_task_${task.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System & Environment Telemetry Card
        Text("SYSTEM & RUNTIME ENVIRONMENT", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Conversation Turns Retained", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("${messages.size} entries", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Local Database Persistence", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("Room SQLite (Active)", color = RadiantEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AI Inference Engine", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("gemini-3.5-flash", color = RadiantViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Standby Background Listening", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("Enabled (Always-On)", color = RadiantCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
