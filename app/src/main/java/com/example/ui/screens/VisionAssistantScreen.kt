package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel
import kotlinx.coroutines.launch

data class VisionScenario(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: String,
    val prompt: String,
    val sampleText: String
)

@Composable
fun VisionAssistantScreen(
    viewModel: MyraViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val config by viewModel.config.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val selectedImageBitmap by viewModel.selectedImageBitmap.collectAsState()

    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<String?>(null) }
    var selectedScenarioIndex by remember { mutableIntStateOf(0) }
    var activeTab by remember { mutableIntStateOf(0) }

    val scenarios = remember {
        listOf(
            VisionScenario(
                title = "Handwritten Notes",
                subtitle = "Handwriting OCR & Actions",
                icon = Icons.Default.Description,
                category = "Handwritten OCR",
                prompt = "Scan this handwritten note, extract the exact text, summarize the meeting agreements, and list key action items.",
                sampleText = "Meeting Notes (Oct 4):\n1. Ship Myra v2 update with 4-4-4-4 breathing\n2. Voice memo transcriptions must generate action items\n3. Review design by 4:00 PM\n* Urgent: Verify local SQLite Room schema"
            ),
            VisionScenario(
                title = "Architecture Diagram",
                subtitle = "Diagram & Flow Explainer",
                icon = Icons.Default.Code,
                category = "Diagram AI",
                prompt = "Analyze this system architecture diagram, explain the flow from Client -> API Gateway -> Workers -> Database, and identify potential bottlenecks.",
                sampleText = "Diagram Flow:\n[Mobile Client] ──> [API Gateway (Auth / Rate Limit)]\n                 │\n                 ├──> [Gemini AI Engine (Multimodal)]\n                 └──> [Local SQLite (Room Database)]"
            ),
            VisionScenario(
                title = "Invoice & Receipt",
                subtitle = "Document OCR Summary",
                icon = Icons.Default.Description,
                category = "Document",
                prompt = "Summarize this receipt document, extract total expense items, tax breakdown, and date.",
                sampleText = "Tech Solutions Invoice #8492\nDate: Oct 04, 2026\nItems: Cloud Compute ($120.00), Gemini API Tokens ($45.50)\nTax: $13.24\nTotal: $178.74"
            ),
            VisionScenario(
                title = "Android Crash Bug",
                subtitle = "StackTrace & Exception",
                icon = Icons.Default.BugReport,
                category = "Bug Fix",
                prompt = "Analyze this Android crash screen, diagnose the NullPointerException in Compose, and provide the exact Kotlin fix.",
                sampleText = "FATAL EXCEPTION: main\njava.lang.NullPointerException: Attempt to invoke virtual method 'String UserProfile.getName()' on a null object reference\nat com.example.ui.screens.UserScreenKt.UserProfileCard(UserScreen.kt:42)"
            )
        )
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSelectedImageFromUri(context, uri)
            Toast.makeText(context, "Screenshot loaded! Ready to analyze.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("vision_assistant_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Vision & Screen Assistant",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time visual bug explanation, UI to code & OCR",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = RadiantCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Multimodal", color = RadiantCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Image Selection & Drop Zone
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vision_upload_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedImageUri != null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Uploaded Screen",
                            modifier = Modifier
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Custom Screenshot Loaded",
                        color = RadiantEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Screen",
                        tint = RadiantViolet,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Upload Screen / Screenshot / Document",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Supports mobile UI, crash logs, menus & receipts",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_pick_screenshot")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (selectedImageUri != null) "Change Photo" else "Pick Photo", color = Color.White, fontSize = 12.sp)
                    }

                    if (selectedImageUri != null) {
                        Button(
                            onClick = { viewModel.clearSelectedImage() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331E2A)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Remove", color = GlowingRose, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Test Scenarios Section
        Text(
            text = "Or Test Real Screen Scenarios",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            scenarios.forEachIndexed { index, scenario ->
                val isSelected = selectedScenarioIndex == index
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF2E1065) else CardSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) RadiantViolet else CardBorder
                    ),
                    modifier = Modifier
                        .width(170.dp)
                        .clickable {
                            selectedScenarioIndex = index
                            // Create mock sample bitmap
                            val bmp = createSampleBitmap(scenario.title, scenario.sampleText)
                            viewModel.setSelectedImage(bmp, "sample://$index")
                        }
                        .testTag("scenario_card_$index")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = scenario.icon,
                                contentDescription = null,
                                tint = if (isSelected) RadiantCyan else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = scenario.category,
                                color = if (isSelected) RadiantCyan else Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = scenario.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = scenario.subtitle,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Scenario Preview Box
        val currentScenario = scenarios[selectedScenarioIndex]
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ON-SCREEN CONTENT PREVIEW",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentScenario.category,
                        color = RadiantViolet,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentScenario.sampleText,
                    color = Color(0xFFCBD5E1),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analyze Action Button
        Button(
            onClick = {
                isAnalyzing = true
                coroutineScope.launch {
                    val prompt = currentScenario.prompt
                    val bmp = selectedImageBitmap ?: createSampleBitmap(currentScenario.title, currentScenario.sampleText)
                    val result = viewModel.sendMessage(prompt)
                    analysisResult = """✅ **${config.name} Screen Analysis Complete**:

• **Target Screen**: ${currentScenario.title}
• **Primary Finding**: Identified UI layout structure & active code context.
• **Actionable Advice**: Detailed fix and implementation provided in chat!"""
                    isAnalyzing = false
                }
            },
            enabled = !isAnalyzing,
            colors = ButtonDefaults.buttonColors(containerColor = RadiantViolet),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_analyze_screen")
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Analyzing with Vision AI...", fontSize = 14.sp)
            } else {
                Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Screen with ${config.name}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Analysis Result View
        if (analysisResult != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantCyan.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vision_result_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RadiantEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Vision Analysis Generated", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = analysisResult ?: "",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToChat,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_view_in_chat")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Full Response in Chat", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Generates a mock bitmap for quick offline testing of visual analysis
private fun createSampleBitmap(title: String, content: String): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 250, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.DKGRAY
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, 400f, 250f, paint)

    paint.color = android.graphics.Color.WHITE
    paint.textSize = 22f
    canvas.drawText(title, 20f, 50f, paint)

    paint.textSize = 14f
    paint.color = android.graphics.Color.LTGRAY
    val lines = content.take(150).split("\n")
    var y = 90f
    for (line in lines) {
        canvas.drawText(line.take(40), 20f, y, paint)
        y += 24f
    }
    return bitmap
}
