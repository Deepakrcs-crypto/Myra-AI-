package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlockView(
    code: String,
    language: String = "kotlin",
    onActionClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    val cleanCode = code.trim()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1322))
            .border(1.dp, Color(0xFF263353), RoundedCornerShape(12.dp))
            .testTag("code_block_view")
    ) {
        // IDE Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131D33))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // macOS / Terminal style dots
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = language.uppercase(),
                    color = RadiantCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Copy Button
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("code", cleanCode)
                    clipboard.setPrimaryClip(clip)
                    isCopied = true
                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    coroutineScope.launch {
                        delay(2000)
                        isCopied = false
                    }
                },
                modifier = Modifier.size(28.dp).testTag("copy_code_button")
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy Code",
                    tint = if (isCopied) RadiantEmerald else Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Code Content (Horizontal scrollable)
        val hScrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hScrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = cleanCode,
                color = Color(0xFFE2E8F0),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }

        // Quick developer actions
        if (onActionClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { onActionClick("Explain this code step-by-step") },
                    label = { Text("Explain", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.QuestionMark, contentDescription = null, modifier = Modifier.size(12.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF1E293B),
                        labelColor = RadiantCyan
                    ),
                    border = null,
                    modifier = Modifier.height(28.dp).testTag("chip_explain_code")
                )

                AssistChip(
                    onClick = { onActionClick("Debug this code and fix potential errors") },
                    label = { Text("Debug & Fix", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.BugReport, contentDescription = null, modifier = Modifier.size(12.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF1E293B),
                        labelColor = GlowingAmber
                    ),
                    border = null,
                    modifier = Modifier.height(28.dp).testTag("chip_debug_code")
                )

                AssistChip(
                    onClick = { onActionClick("Generate unit tests for this code") },
                    label = { Text("Unit Tests", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF1E293B),
                        labelColor = RadiantViolet
                    ),
                    border = null,
                    modifier = Modifier.height(28.dp).testTag("chip_tests_code")
                )
            }
        }
    }
}
