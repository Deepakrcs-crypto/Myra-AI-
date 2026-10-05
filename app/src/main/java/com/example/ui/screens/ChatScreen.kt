package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.ui.components.FormattedAiResponse
import com.example.ui.components.GlowingOrb
import com.example.ui.components.OrbState
import com.example.ui.components.VoiceInputBar
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MyraViewModel,
    onNavigateToVision: () -> Unit,
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToVoiceNotes: () -> Unit = {},
    onNavigateToWellness: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val config by viewModel.config.collectAsState()
    val orbState by viewModel.orbState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newNameInput by remember { mutableStateOf("") }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll smoothly to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSelectedImageFromUri(context, uri)
            Toast.makeText(context, "Image attached for analysis", Toast.LENGTH_SHORT).show()
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Customize Assistant Name", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Rename Myra to whatever you prefer (e.g. Alex, Nova, रिया).",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        placeholder = { Text("Enter new name", color = Color(0xFF64748B)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_rename_assistant")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNameInput.isNotBlank()) {
                            viewModel.setAssistantName(newNameInput)
                        }
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantViolet),
                    modifier = Modifier.testTag("btn_confirm_rename")
                ) {
                    Text("Save Name")
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
            .testTag("chat_screen")
    ) {
        // Sleek Minimalist Top Header
        Surface(
            color = Color(0xFF0C1222),
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Assistant Status & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showRenameDialog = true; newNameInput = config.name }
                        .testTag("assistant_header_profile")
                ) {
                    GlowingOrb(
                        state = orbState,
                        size = 36.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = config.name,
                                color = Color.White,
                                fontSize = 16.sp,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (orbState == OrbState.THINKING) GlowingAmber
                                        else if (orbState == OrbState.SPEAKING) RadiantViolet
                                        else RadiantEmerald
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = when (orbState) {
                                    OrbState.THINKING -> "Thinking..."
                                    OrbState.SPEAKING -> "Speaking..."
                                    OrbState.SLEEPING -> "Standby"
                                    else -> "Online"
                                },
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Compact Controls: Sleep & Overflow Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Sleep Button
                    IconButton(
                        onClick = { viewModel.enterSleepMode() },
                        modifier = Modifier.size(36.dp).testTag("btn_header_sleep")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightlightRound,
                            contentDescription = "Enter Sleep Mode",
                            tint = RadiantCyan,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Voice Output Toggle
                    IconButton(
                        onClick = { viewModel.toggleVoiceEnabled() },
                        modifier = Modifier.size(36.dp).testTag("btn_toggle_voice")
                    ) {
                        Icon(
                            imageVector = if (config.voiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Speech Voice",
                            tint = if (config.voiceEnabled) RadiantViolet else Color(0xFF64748B),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showOptionsMenu = true },
                            modifier = Modifier.size(36.dp).testTag("btn_header_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            modifier = Modifier.background(Color(0xFF131D33))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Rename Assistant", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    showOptionsMenu = false
                                    showRenameDialog = true
                                    newNameInput = config.name
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Clear Chat History", color = GlowingRose, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = GlowingRose, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    showOptionsMenu = false
                                    viewModel.clearChat()
                                    Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Uncluttered, Spacious Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                SpaciousChatMessageItem(
                    message = msg,
                    assistantName = config.name,
                    onSpeak = { text -> viewModel.speakText(text) },
                    onCodeAction = { prompt -> viewModel.sendMessage(prompt) }
                )
            }
        }

        // Retractable Compact Voice Input Bar
        VoiceInputBar(
            inputText = inputText,
            onInputChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank() || selectedImageUri != null) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            },
            onMicClick = {
                inputText = "Hello ${config.name}, what can you help me with?"
                Toast.makeText(context, "Voice phrase captured!", Toast.LENGTH_SHORT).show()
            },
            onAttachClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            selectedImageUri = selectedImageUri,
            onRemoveImage = { viewModel.clearSelectedImage() },
            onQuickAction = { action ->
                when (action) {
                    "PLANNER" -> onNavigateToPlanner()
                    "VOICE_NOTES" -> onNavigateToVoiceNotes()
                    "WELLNESS" -> onNavigateToWellness()
                    "VISION" -> onNavigateToVision()
                    "Sleep" -> viewModel.enterSleepMode()
                    else -> viewModel.sendMessage(action)
                }
            }
        )
    }
}

@Composable
fun SpaciousChatMessageItem(
    message: ChatMessage,
    assistantName: String,
    onSpeak: (String) -> Unit,
    onCodeAction: (String) -> Unit
) {
    val isUser = message.sender == "user"
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, end = 8.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E1065)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = null,
                    tint = RadiantViolet,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.94f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Attached Image if present
            if (message.imageUri != null) {
                AsyncImage(
                    model = message.imageUri,
                    contentDescription = "Attached Image",
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .size(160.dp)
                )
            }

            // Message Bubble Card
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = if (isUser) Color(0xFF581C87) else Color(0xFF111827),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) RadiantViolet.copy(alpha = 0.4f) else Color(0xFF1F2937)
                ),
                modifier = Modifier.testTag(if (isUser) "user_message_bubble" else "assistant_message_bubble")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (isUser) {
                        Text(
                            text = message.content,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    } else {
                        // Formatted AI response with clear bullets, bold terms, short paragraphs, and code blocks
                        FormattedAiResponse(
                            content = message.content,
                            onCodeAction = onCodeAction
                        )
                    }

                    // Timestamp & Read Aloud action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = { onSpeak(message.content) },
                                modifier = Modifier.size(22.dp).testTag("btn_speak_msg_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Read aloud",
                                    tint = RadiantCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
