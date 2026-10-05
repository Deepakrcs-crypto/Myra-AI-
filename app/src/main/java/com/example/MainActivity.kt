package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuickActionBottomSheet
import com.example.ui.components.SleepOverlay
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DailyPlannerScreen
import com.example.ui.screens.MentalWellnessScreen
import com.example.ui.screens.VisionAssistantScreen
import com.example.ui.screens.VoiceNotesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MyraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MyraApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyraApp(viewModel: MyraViewModel) {
    val config by viewModel.config.collectAsState()
    var selectedScreen by remember { mutableIntStateOf(0) } // 0: Chat, 1: Planner, 2: Voice Notes, 3: Wellness, 4: Vision
    var showQuickActionSheet by remember { mutableStateOf(false) }

    // Subscreen title mapping
    val subScreenTitle = when (selectedScreen) {
        1 -> "Smart Daily Planner"
        2 -> "Voice Notes & Summarizer"
        3 -> "Mood & Mental Wellness"
        4 -> "Visual AI & Document Scanner"
        else -> "Myra"
    }

    // Handle back button if on sub-screens
    if (selectedScreen != 0) {
        BackHandler {
            selectedScreen = 0
        }
    }

    // Handle back button when in Sleep Mode
    if (config.sleepMode) {
        BackHandler {
            viewModel.wakeUp("Back button wake")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        Scaffold(
            topBar = {
                // Show clean TopAppBar with back button ONLY on sub-screens
                if (selectedScreen != 0 && !config.sleepMode) {
                    TopAppBar(
                        title = {
                            Text(
                                text = subScreenTitle,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { selectedScreen = 0 },
                                modifier = Modifier.testTag("btn_back_to_chat")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Chat",
                                    tint = RadiantCyan
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0C1222)
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF090D16)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF090D16))
            ) {
                when (selectedScreen) {
                    0 -> ChatScreen(
                        viewModel = viewModel,
                        onNavigateToVision = { selectedScreen = 4 },
                        onNavigateToPlanner = { selectedScreen = 1 },
                        onNavigateToVoiceNotes = { selectedScreen = 2 },
                        onNavigateToWellness = { selectedScreen = 3 }
                    )
                    1 -> DailyPlannerScreen(
                        viewModel = viewModel
                    )
                    2 -> VoiceNotesScreen(
                        viewModel = viewModel,
                        onNavigateToPlanner = { selectedScreen = 1 }
                    )
                    3 -> MentalWellnessScreen(
                        viewModel = viewModel
                    )
                    4 -> VisionAssistantScreen(
                        viewModel = viewModel,
                        onNavigateToChat = { selectedScreen = 0 }
                    )
                }
            }
        }

        // Fullscreen Ambient Sleep Overlay
        SleepOverlay(
            assistantName = config.name,
            isVisible = config.sleepMode,
            onWakeUp = { viewModel.wakeUp("Orb tap") },
            onSendWakeMessage = { wakeMsg ->
                viewModel.wakeUp("Message: $wakeMsg")
                viewModel.sendMessage(wakeMsg)
            }
        )

        // Mobile Quick Action Bottom Sheet
        QuickActionBottomSheet(
            viewModel = viewModel,
            isVisible = showQuickActionSheet,
            onDismiss = { showQuickActionSheet = false },
            onNavigateToBreathing = { selectedScreen = 3 },
            onNavigateToVoiceNotes = { selectedScreen = 2 },
            onSendToChat = { prompt ->
                selectedScreen = 0
                viewModel.sendMessage(prompt)
            }
        )
    }
}
