package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceNote
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VoiceNotesScreen(
    viewModel: MyraViewModel,
    onNavigateToPlanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val voiceNotes by viewModel.voiceNotes.collectAsState()
    val config by viewModel.config.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var liveTranscript by remember { mutableStateOf("") }
    var generatedSummary by remember { mutableStateOf<String?>(null) }
    var generatedActions by remember { mutableStateOf<List<String>>(emptyList()) }
    var noteTitleInput by remember { mutableStateOf("") }
    var isSummarizing by remember { mutableStateOf(false) }

    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingSeconds++
                // Simulated progressive speech recognition stream
                when (recordingSeconds) {
                    2 -> liveTranscript = "We need to finalize the quarterly release roadmap for our team."
                    4 -> liveTranscript = "We need to finalize the quarterly release roadmap for our team. Priority one is testing the new voice summarizer and daily task synchronization."
                    7 -> liveTranscript = "We need to finalize the quarterly release roadmap for our team. Priority one is testing the new voice summarizer and daily task synchronization. Also schedule a follow-up with design by Thursday afternoon."
                }
            }
        }
    }

    // Audio recording wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "voicePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recPulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(16.dp)
            .testTag("voice_notes_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Voice Notes & Summarizer",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fast audio speech-to-text with AI action items",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantViolet.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RadiantViolet, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Synthesis", color = RadiantViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fast Voice Recording Studio Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isRecording) GlowingRose else CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Recording Mic Orb
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isRecording) listOf(GlowingRose, Color(0xFF881337))
                                else listOf(RadiantViolet, Color(0xFF4C1D95))
                            )
                        )
                        .clickable {
                            isRecording = !isRecording
                            if (!isRecording && liveTranscript.isNotBlank()) {
                                // Auto-trigger summarization upon stopping recording
                                isSummarizing = true
                                val (sum, actions) = viewModel.generateAiSummaryForVoiceNote(liveTranscript)
                                generatedSummary = sum
                                generatedActions = actions.split("|")
                                isSummarizing = false
                            }
                        }
                        .testTag("btn_fast_voice_record")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Record Voice Note",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isRecording) "Recording... (${recordingSeconds}s)" else "Tap to record fast voice note",
                    color = if (isRecording) GlowingRose else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Live Transcription text box
                OutlinedTextField(
                    value = liveTranscript,
                    onValueChange = { liveTranscript = it },
                    placeholder = {
                        Text(
                            "Transcription will appear in real time, or type here...",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_voice_transcript"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = RadiantViolet,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Summarize & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (liveTranscript.isNotBlank()) {
                                isSummarizing = true
                                val (sum, actions) = viewModel.generateAiSummaryForVoiceNote(liveTranscript)
                                generatedSummary = sum
                                generatedActions = actions.split("|")
                                isSummarizing = false
                            }
                        },
                        enabled = liveTranscript.isNotBlank() && !isSummarizing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("btn_summarize_transcript")
                    ) {
                        if (isSummarizing) {
                            CircularProgressIndicator(color = RadiantViolet, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RadiantViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Summarize", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            if (liveTranscript.isNotBlank()) {
                                val summaryToSave = generatedSummary ?: viewModel.quickSummarizeText(liveTranscript)
                                val actionsToSave = if (generatedActions.isNotEmpty()) generatedActions.joinToString("|")
                                else "Review recorded voice note"
                                viewModel.saveVoiceNote(
                                    title = noteTitleInput.ifBlank { "Voice Memo #${voiceNotes.size + 1}" },
                                    rawTranscript = liveTranscript,
                                    summary = summaryToSave,
                                    actionItems = actionsToSave,
                                    durationSeconds = recordingSeconds.coerceAtLeast(10)
                                )
                                Toast.makeText(context, "Voice note & summary saved!", Toast.LENGTH_SHORT).show()
                                liveTranscript = ""
                                generatedSummary = null
                                generatedActions = emptyList()
                                noteTitleInput = ""
                            }
                        },
                        enabled = liveTranscript.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RadiantViolet),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_save_voice_note")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Note", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // AI Summary & Action Items Card Preview
                if (generatedSummary != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D33),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("voice_summary_preview")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Summary", color = RadiantCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(generatedSummary ?: "", color = Color(0xFFE2E8F0), fontSize = 12.sp, lineHeight = 18.sp)

                            if (generatedActions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("KEY ACTION ITEMS", color = GlowingAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                generatedActions.forEach { act ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RadiantEmerald, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(act, color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        generatedActions.forEach { act ->
                                            viewModel.addDailyTodo(act, "Today", "Work")
                                        }
                                        Toast.makeText(context, "Added ${generatedActions.size} items to Daily Planner!", Toast.LENGTH_SHORT).show()
                                        onNavigateToPlanner()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_export_to_planner")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = RadiantEmerald, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add to Daily Planner", color = RadiantEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recorded Voice Notes List
        Text(
            text = "SAVED VOICE NOTES (${voiceNotes.size})",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (voiceNotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No voice notes recorded yet.\nTap the mic above to create a memo with automatic AI summary!", color = Color(0xFF64748B), fontSize = 13.sp)
            }
        } else {
            val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(voiceNotes, key = { it.id }) { note ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth().testTag("voice_note_card_${note.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2E1065)
                                    ) {
                                        Text(
                                            "${note.durationSeconds}s Audio",
                                            color = RadiantViolet,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = note.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.speakText("${note.title}. Summary: ${note.summary}") },
                                        modifier = Modifier.size(30.dp).testTag("btn_play_note_${note.id}")
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play summary", tint = RadiantCyan, modifier = Modifier.size(17.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteVoiceNote(note) },
                                        modifier = Modifier.size(30.dp).testTag("btn_delete_note_${note.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(17.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Transcript: ${note.rawTranscript}",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("💡 ${note.summary}", color = Color(0xFFCBD5E1), fontSize = 12.sp)

                                    if (note.actionItems.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val itemsList = note.actionItems.split("|")
                                        itemsList.forEach { item ->
                                            Text("• $item", color = RadiantCyan, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateFormat.format(Date(note.timestamp)),
                                color = Color(0xFF475569),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
