package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodEntry
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantCyanLight
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodOption(
    val type: String,
    val emoji: String,
    val label: String,
    val color: Color
)

@Composable
fun MentalWellnessScreen(
    viewModel: MyraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()
    val moodEntries by viewModel.moodEntries.collectAsState()
    val latestMood by viewModel.latestMood.collectAsState()

    var selectedMoodOption by remember { mutableStateOf<MoodOption?>(null) }
    var moodNoteInput by remember { mutableStateOf("") }
    var activeEmpatheticMsg by remember { mutableStateOf<String?>(null) }

    // 4-4-4-4 Box Breathing State
    var isBreathingActive by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("Inhale") } // Inhale -> Hold -> Exhale -> Hold
    var phaseSecondsRemaining by remember { mutableIntStateOf(4) }
    var completedCycles by remember { mutableIntStateOf(0) } // 4 cycles = ~1 minute
    var isBreathingFinished by remember { mutableStateOf(false) }

    val moodOptions = remember {
        listOf(
            MoodOption("Joyful", "🌟", "Joyful", GlowingAmber),
            MoodOption("Good", "😊", "Good", RadiantEmerald),
            MoodOption("Calm", "🌿", "Calm", RadiantCyan),
            MoodOption("Tired", "😴", "Tired", Color(0xFF94A3B8)),
            MoodOption("Stressed", "😰", "Stressed", GlowingRose),
            MoodOption("Down", "🌧️", "Down", Color(0xFF6366F1))
        )
    }

    // 4-4-4-4 Guided Breathing Engine (64 seconds total = 4 cycles of 16 seconds)
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            completedCycles = 0
            isBreathingFinished = false
            while (isBreathingActive && completedCycles < 4) {
                // 1. INHALE (4s)
                currentPhase = "Inhale"
                for (s in 4 downTo 1) {
                    if (!isBreathingActive) break
                    phaseSecondsRemaining = s
                    delay(1000)
                }

                // 2. HOLD (4s)
                if (!isBreathingActive) break
                currentPhase = "Hold"
                for (s in 4 downTo 1) {
                    if (!isBreathingActive) break
                    phaseSecondsRemaining = s
                    delay(1000)
                }

                // 3. EXHALE (4s)
                if (!isBreathingActive) break
                currentPhase = "Exhale"
                for (s in 4 downTo 1) {
                    if (!isBreathingActive) break
                    phaseSecondsRemaining = s
                    delay(1000)
                }

                // 4. HOLD (4s)
                if (!isBreathingActive) break
                currentPhase = "Hold"
                for (s in 4 downTo 1) {
                    if (!isBreathingActive) break
                    phaseSecondsRemaining = s
                    delay(1000)
                }

                completedCycles++
            }

            if (completedCycles >= 4) {
                isBreathingActive = false
                isBreathingFinished = true
                viewModel.completeBreathingSession()
                Toast.makeText(context, "1-minute Box Breathing complete! Nervous system reset.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Dynamic circle expansion based on phase
    val targetScale = when (currentPhase) {
        "Inhale" -> 1.35f
        "Hold" -> 1.35f
        "Exhale" -> 0.85f
        else -> 0.85f
    }
    val animatedScale by animateFloatAsState(
        targetValue = if (isBreathingActive) targetScale else 1.0f,
        animationSpec = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
        label = "breathScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("mental_wellness_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mood & Mental Wellness",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1-Tap mood check-in & 4-4-4-4 box breathing",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = GlowingRose, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mindful", color = RadiantCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1-Tap Mood Check-in Widget Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth().testTag("mood_checkin_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HOW ARE YOU FEELING RIGHT NOW?",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 6 Expressive Mood Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moodOptions.forEach { opt ->
                        val isSelected = selectedMoodOption?.type == opt.type
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) opt.color.copy(alpha = 0.25f) else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) opt.color else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedMoodOption = opt
                                    val entry = viewModel.recordMood(opt.type, opt.emoji, moodNoteInput)
                                    activeEmpatheticMsg = entry.empatheticResponse
                                    if (opt.type == "Stressed") {
                                        Toast.makeText(context, "Take a breath with Myra below.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .testTag("mood_btn_${opt.type.lowercase()}")
                        ) {
                            Text(text = opt.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opt.label,
                                color = if (isSelected) opt.color else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Empathetic Response Banner from Myra
                AnimatedVisibility(visible = activeEmpatheticMsg != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D33),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("empathetic_response_banner")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedMoodOption?.emoji ?: "💬", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${config.name}'s Note for You", color = RadiantCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { viewModel.speakText(activeEmpatheticMsg ?: "") },
                                    modifier = Modifier.size(26.dp).testTag("btn_speak_empathy")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play message", tint = RadiantCyan, modifier = Modifier.size(15.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeEmpatheticMsg ?: "",
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1-Minute Guided Box Breathing (4-4-4-4 Technique) Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isBreathingActive) RadiantCyan else CardBorder),
            modifier = Modifier.fillMaxWidth().testTag("box_breathing_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "1-MIN GUIDED BOX BREATHING",
                            color = RadiantCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "4-4-4-4 Navy SEAL Calming Technique",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Text(
                            text = "Cycle ${completedCycles + 1}/4",
                            color = RadiantEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Guided Breathing Animated Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .testTag("breathing_circle_container")
                ) {
                    Canvas(modifier = Modifier.size(170.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val baseRadius = (size.minDimension / 2f) * 0.65f * animatedScale

                        // Outer glowing aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    RadiantCyan.copy(alpha = 0.35f),
                                    RadiantCyan.copy(alpha = 0.05f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = baseRadius * 1.4f
                            ),
                            radius = baseRadius * 1.4f,
                            center = center
                        )

                        // Core Breathing Orb
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF06B6D4),
                                    Color(0xFF38BDF8),
                                    Color(0xFFA855F7),
                                    Color(0xFF06B6D4)
                                ),
                                center = center
                            ),
                            radius = baseRadius,
                            center = center
                        )

                        // Boundary stroke
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f),
                            radius = baseRadius * 0.95f,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Countdown & Instruction text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isBreathingActive) currentPhase.uppercase() else if (isBreathingFinished) "DONE" else "START",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBreathingActive) "${phaseSecondsRemaining}s" else if (isBreathingFinished) "🌟" else "4s",
                            color = RadiantCyanLight,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Phase Sequence Indicator (Inhale 4s -> Hold 4s -> Exhale 4s -> Hold 4s)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Inhale (4s)", "Hold (4s)", "Exhale (4s)", "Hold (4s)").forEach { step ->
                        val isCurrent = isBreathingActive && step.startsWith(currentPhase)
                        Text(
                            text = step,
                            color = if (isCurrent) RadiantCyan else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Breathing Action Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            isBreathingActive = !isBreathingActive
                            if (isBreathingActive) {
                                isBreathingFinished = false
                                viewModel.speakText("Beginning 1-minute box breathing. Inhale gently.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBreathingActive) GlowingRose else RadiantCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(46.dp).testTag("btn_toggle_breathing")
                    ) {
                        Icon(
                            imageVector = if (isBreathingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF090D16),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBreathingActive) "Stop Session" else "Start 1-Min Breathing",
                            color = Color(0xFF090D16),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (completedCycles > 0 || isBreathingFinished) {
                        IconButton(
                            onClick = {
                                isBreathingActive = false
                                completedCycles = 0
                                isBreathingFinished = false
                                phaseSecondsRemaining = 4
                                currentPhase = "Inhale"
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Mood History
        Text(
            text = "RECENT CHECK-IN JOURNAL",
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
        if (moodEntries.isEmpty()) {
            Text("No check-ins yet. Tap your mood above to begin your wellness log.", color = Color(0xFF64748B), fontSize = 12.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                moodEntries.take(4).forEach { entry ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth().testTag("mood_log_item_${entry.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(entry.moodEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(entry.moodType, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = if (entry.breathingCompleted) "Guided Breathing Session Completed" else entry.note.ifBlank { "Mood logged" },
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Text(
                                text = dateFormat.format(Date(entry.timestamp)),
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
