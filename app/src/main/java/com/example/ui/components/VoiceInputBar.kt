package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet

@Composable
fun VoiceInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    isListening: Boolean = false,
    selectedImageUri: String? = null,
    onRemoveImage: () -> Unit = {},
    onQuickAction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF090D16))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("voice_input_bar")
    ) {
        // Image Attachment Preview Pill
        AnimatedVisibility(visible = selectedImageUri != null) {
            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, RadiantViolet, RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    IconButton(
                        onClick = onRemoveImage,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Image",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Retractable Compact Action Tray (Hidden by default, slides down when "+" tapped)
        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUICK ACTIONS",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { isMenuExpanded = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Menu", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiniActionItem(
                            label = "Daily Planner",
                            icon = Icons.Default.Today,
                            tint = RadiantEmerald,
                            testTag = "mini_action_planner",
                            onClick = {
                                isMenuExpanded = false
                                onQuickAction("PLANNER")
                            }
                        )

                        MiniActionItem(
                            label = "Voice Memo",
                            icon = Icons.Default.Mic,
                            tint = RadiantViolet,
                            testTag = "mini_action_voice",
                            onClick = {
                                isMenuExpanded = false
                                onQuickAction("VOICE_NOTES")
                            }
                        )

                        MiniActionItem(
                            label = "Breathing",
                            icon = Icons.Default.SelfImprovement,
                            tint = RadiantCyan,
                            testTag = "mini_action_wellness",
                            onClick = {
                                isMenuExpanded = false
                                onQuickAction("WELLNESS")
                            }
                        )

                        MiniActionItem(
                            label = "Visual Scan",
                            icon = Icons.Outlined.Visibility,
                            tint = GlowingAmber,
                            testTag = "mini_action_vision",
                            onClick = {
                                isMenuExpanded = false
                                onQuickAction("VISION")
                            }
                        )

                        MiniActionItem(
                            label = "Sleep",
                            icon = Icons.Default.NightlightRound,
                            tint = GlowingRose,
                            testTag = "mini_action_sleep",
                            onClick = {
                                isMenuExpanded = false
                                onQuickAction("Sleep")
                            }
                        )
                    }
                }
            }
        }

        // Minimalist Modern Input Capsule
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF12192E),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232D48)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand / Retract Actions Button (+)
                IconButton(
                    onClick = { isMenuExpanded = !isMenuExpanded },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isMenuExpanded) RadiantViolet else Color(0xFF1E2842))
                        .testTag("btn_toggle_action_tray")
                ) {
                    Icon(
                        imageVector = if (isMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Quick Tools",
                        tint = if (isMenuExpanded) Color.White else RadiantCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Camera / Attachment Icon
                IconButton(
                    onClick = onAttachClick,
                    modifier = Modifier.size(34.dp).testTag("btn_attach_image")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Attach Photo / Document",
                        tint = if (selectedImageUri != null) RadiantCyan else Color(0xFF94A3B8),
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Text Input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            if (isListening) "Listening..." else "Message Myra...",
                            color = if (isListening) RadiantCyan else Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Voice Mic Button
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isListening) GlowingRose else Color.Transparent)
                        .testTag("btn_mic_input")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = "Voice Input",
                        tint = if (isListening) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Send Button
                if (inputText.isNotBlank() || selectedImageUri != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onSend,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RadiantViolet)
                            .testTag("btn_send_message")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniActionItem(
    label: String,
    icon: ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
