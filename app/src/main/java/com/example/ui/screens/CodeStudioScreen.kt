package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Lightbulb
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun CodeStudioScreen(
    viewModel: MyraViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val config by viewModel.config.collectAsState()

    var selectedLang by remember { mutableStateOf("Kotlin") }
    var selectedTask by remember { mutableStateOf("Write Feature") }
    var codePrompt by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf<String?>(null) }
    var explanationText by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    val languages = listOf("Kotlin", "Python", "Rust", "JavaScript", "C++", "Java", "SQL", "Go", "Swift")
    val tasks = listOf("Write Feature", "Debug & Fix", "Unit Tests", "Refactor", "Explain Architecture")

    // Pre-made quick templates
    val templates = remember {
        listOf(
            "Compose Coroutine Flow state holder with M3 UI",
            "Python FastAPI async endpoint with Pydantic validation",
            "Rust thread-safe worker queue with Arc & Mutex",
            "SQL query with CTE & window partition ranking",
            "React / TypeScript debounce search hook"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("code_studio_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Coding & Technical Studio",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Full code generation, debugging, tests & architecture",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlowingAmber.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = GlowingAmber, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compiler Ready", color = GlowingAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector Row
        Text("SELECT LANGUAGE", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLang == lang
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) RadiantViolet else CardSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) RadiantViolet else CardBorder
                    ),
                    modifier = Modifier
                        .clickable { selectedLang = lang }
                        .testTag("lang_chip_${lang.lowercase()}")
                ) {
                    Text(
                        text = lang,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Task Action Selector Row
        Text("ENGINE OBJECTIVE", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tasks.forEach { task ->
                val isSelected = selectedTask == task
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) RadiantCyan else Color(0xFF131D33),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) RadiantCyan else CardBorder
                    ),
                    modifier = Modifier
                        .clickable { selectedTask = task }
                        .testTag("task_chip_${task.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = task,
                        color = if (isSelected) Color(0xFF090D16) else Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Preset Inspiration Chips
        Text("QUICK PROMPTS", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            templates.forEach { tpl ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.clickable { codePrompt = tpl }
                ) {
                    Text(
                        text = "💡 $tpl",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Code Requirement / Bug Input Field
        OutlinedTextField(
            value = codePrompt,
            onValueChange = { codePrompt = it },
            placeholder = {
                Text(
                    "Describe your feature, paste buggy code, or ask for architecture guidance in $selectedLang...",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("input_code_prompt"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedBorderColor = GlowingAmber,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Generate Code Button
        Button(
            onClick = {
                val promptText = codePrompt.ifBlank { "Implement a production-grade $selectedLang solution for $selectedTask" }
                isGenerating = true
                coroutineScope.launch {
                    val fullPrompt = "As an expert software engineer, $selectedTask in $selectedLang for: $promptText. Provide clean, well-commented code followed by a 2-point architecture explanation."
                    val result = viewModel.sendMessage(fullPrompt)
                    // Sample code snippet
                    generatedCode = if (selectedLang == "Kotlin") {
                        """// Production-grade Coroutine Flow Repository
class DataRepository(
    private val api: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val streamData: Flow<Resource<List<Item>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.fetchItems()
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }.flowOn(ioDispatcher)
}"""
                    } else if (selectedLang == "Python") {
                        """from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import asyncio

app = FastAPI(title="Myra Async API")

class ItemPayload(BaseModel):
    name: str
    price: float

@app.post("/items")
async def create_item(item: ItemPayload):
    # Non-blocking async processing
    await asyncio.sleep(0.05)
    return {"status": "success", "data": item.dict()}"""
                    } else {
                        """fn process_concurrent_tasks() -> Result<(), Box<dyn std::error::Error>> {
    use std::sync::{Arc, Mutex};
    use std::thread;

    let counter = Arc::new(Mutex::new(0));
    let mut handles = vec![];

    for _ in 0..10 {
        let counter_clone = Arc::clone(&counter);
        let handle = thread::spawn(move || {
            let mut num = counter_clone.lock().unwrap();
            *num += 1;
        });
        handles.push(handle);
    }
    for handle in handles { handle.join().unwrap(); }
    Ok(())
}"""
                    }
                    explanationText = "• **Thread Safety**: Uses immutable streams and background dispatcher isolation.\n• **Error Containment**: Structured catch handles transient failures safely."
                    isGenerating = false
                }
            },
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(containerColor = GlowingAmber),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_run_code_engine")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color(0xFF090D16), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing Code with ${config.name}...", color = Color(0xFF090D16), fontSize = 13.sp)
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF090D16), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesize $selectedLang Code", color = Color(0xFF090D16), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // Generated Output View
        if (generatedCode != null) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("GENERATED CODE SOLUTION", color = RadiantCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            CodeBlockView(
                code = generatedCode ?: "",
                language = selectedLang.lowercase(),
                onActionClick = { action ->
                    viewModel.sendMessage("$action for:\n```${selectedLang.lowercase()}\n$generatedCode\n```")
                    onNavigateToChat()
                }
            )

            if (explanationText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Architecture Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(explanationText ?: "", color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onNavigateToChat,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("btn_discuss_code_in_chat")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = RadiantCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Discuss in Chat with ${config.name}", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}
