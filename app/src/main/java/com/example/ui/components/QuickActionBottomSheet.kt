package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel

data class QuickActionTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
    viewModel: MyraViewModel,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToVoiceNotes: () -> Unit,
    onSendToChat: (String) -> Unit
) {
    if (!isVisible) return

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var activeSubAction by remember { mutableIntStateOf(0) } // 0: Menu, 1: Motivation, 2: Summarize, 3: Hindi Translation

    var motivationQuote by remember { mutableStateOf(viewModel.getDailyMotivation()) }
    var summarizeInputText by remember { mutableStateOf("") }
    var summarizeResultText by remember { mutableStateOf<String?>(null) }

    var hindiInputText by remember { mutableStateOf("") }
    var hindiResultText by remember { mutableStateOf<String?>(null) }
    var isEnglishToHindi by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF334155))
            )
        },
        modifier = Modifier.testTag("quick_action_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back or Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = RadiantCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (activeSubAction) {
                            1 -> "Daily Motivation"
                            2 -> "Quick Summarizer"
                            3 -> "Hindi Translation"
                            else -> "Mobile Quick Actions"
                        },
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (activeSubAction != 0) {
                    IconButton(
                        onClick = { activeSubAction = 0 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Back", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (activeSubAction) {
                0 -> {
                    // Grid of 5 Mobile Quick Action Tiles
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // 1. Daily Motivation
                        ActionTileItem(
                            title = "Daily Motivation",
                            subtitle = "Inspiring thought & uplifting message from Myra",
                            icon = Icons.Default.FormatQuote,
                            color = GlowingAmber,
                            testTag = "tile_daily_motivation",
                            onClick = {
                                motivationQuote = viewModel.getDailyMotivation()
                                activeSubAction = 1
                            }
                        )

                        // 2. Quick Summarize
                        ActionTileItem(
                            title = "Quick Summarize",
                            subtitle = "Paste any text for instant 3-bullet executive summary",
                            icon = Icons.Default.AutoAwesome,
                            color = RadiantViolet,
                            testTag = "tile_quick_summarize",
                            onClick = { activeSubAction = 2 }
                        )

                        // 3. Hindi Translation
                        ActionTileItem(
                            title = "Hindi Translation (हिन्दी अनुवाद)",
                            subtitle = "Bidirectional English <-> Hindi live translation",
                            icon = Icons.Default.Translate,
                            color = GlowingRose,
                            testTag = "tile_hindi_translation",
                            onClick = { activeSubAction = 3 }
                        )

                        // 4. 1-Min Guided Breathing
                        ActionTileItem(
                            title = "1-Min Guided Breathing",
                            subtitle = "4-4-4-4 box breathing technique to reset focus",
                            icon = Icons.Default.SelfImprovement,
                            color = RadiantCyan,
                            testTag = "tile_box_breathing",
                            onClick = {
                                onDismiss()
                                onNavigateToBreathing()
                            }
                        )

                        // 5. Fast Voice Note
                        ActionTileItem(
                            title = "Fast Voice Memo",
                            subtitle = "Record audio memo with automatic AI summary",
                            icon = Icons.Default.Mic,
                            color = RadiantEmerald,
                            testTag = "tile_fast_voice_note",
                            onClick = {
                                onDismiss()
                                onNavigateToVoiceNotes()
                            }
                        )
                    }
                }

                1 -> {
                    // Daily Motivation Sub-view
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlowingAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("motivation_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = motivationQuote,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { motivationQuote = viewModel.getDailyMotivation() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = GlowingAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("New Quote", color = Color.White, fontSize = 11.sp)
                                }

                                IconButton(
                                    onClick = { viewModel.speakText(motivationQuote) },
                                    modifier = Modifier.size(36.dp).background(Color(0xFF1E293B), CircleShape)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak quote", tint = RadiantCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Quick Summarize Sub-view
                    Column {
                        OutlinedTextField(
                            value = summarizeInputText,
                            onValueChange = { summarizeInputText = it },
                            placeholder = { Text("Paste article, email, or meeting note here...", color = Color(0xFF64748B), fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(110.dp).testTag("input_quick_summarize"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurface,
                                unfocusedContainerColor = CardSurface,
                                focusedBorderColor = RadiantViolet,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (summarizeInputText.isNotBlank()) {
                                    summarizeResultText = viewModel.quickSummarizeText(summarizeInputText)
                                }
                            },
                            enabled = summarizeInputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = RadiantViolet),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_run_quick_summary")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate 3-Bullet Summary", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (summarizeResultText != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131D33),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(summarizeResultText ?: "", color = Color(0xFFE2E8F0), fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("summary", summarizeResultText))
                                                Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy", color = Color.White, fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                onDismiss()
                                                onSendToChat(summarizeResultText ?: "")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = RadiantEmerald, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Send to Chat", color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Hindi Translation Sub-view
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglishToHindi) "English → Hindi" else "Hindi → English",
                                color = RadiantCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { isEnglishToHindi = !isEnglishToHindi },
                                modifier = Modifier.size(30.dp).background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = hindiInputText,
                            onValueChange = { hindiInputText = it },
                            placeholder = { Text(if (isEnglishToHindi) "Enter English text to translate..." else "हिन्दी में लिखें...", color = Color(0xFF64748B), fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(90.dp).testTag("input_hindi_quick_trans"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurface,
                                unfocusedContainerColor = CardSurface,
                                focusedBorderColor = GlowingRose,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (hindiInputText.isNotBlank()) {
                                    hindiResultText = viewModel.quickTranslateHindi(hindiInputText, isEnglishToHindi)
                                }
                            },
                            enabled = hindiInputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = GlowingRose),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_run_hindi_trans")
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Translate Instantly", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (hindiResultText != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131D33),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlowingRose.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("अनुवाद (Translation)", color = GlowingRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        IconButton(
                                            onClick = { viewModel.speakText(hindiResultText ?: "") },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play", tint = RadiantCyan, modifier = Modifier.size(15.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(hindiResultText ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun ActionTileItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .border(1.dp, color.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }
    }
}
