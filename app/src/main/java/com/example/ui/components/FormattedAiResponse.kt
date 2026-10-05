package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RadiantCyan

@Composable
fun FormattedAiResponse(
    content: String,
    onCodeAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Split content by code blocks first
        val segments = content.split("```")
        segments.forEachIndexed { index, segment ->
            if (index % 2 == 1) {
                // Code block segment
                val language = segment.substringBefore("\n").trim().ifBlank { "kotlin" }
                val codeBody = segment.substringAfter("\n").trim()
                Spacer(modifier = Modifier.height(6.dp))
                CodeBlockView(
                    code = codeBody,
                    language = language,
                    onActionClick = onCodeAction,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                // Text content segment: split into lines/paragraphs
                val lines = segment.trim().split("\n")
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isNotBlank()) {
                        when {
                            // Section Headings (### or ## or #)
                            trimmed.startsWith("###") || trimmed.startsWith("##") || trimmed.startsWith("#") -> {
                                val headingText = trimmed.replace(Regex("""^#+\s*"""), "")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = headingText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            // Bullet Points (• or - or *)
                            trimmed.startsWith("•") || trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                                val bulletContent = trimmed.replace(Regex("""^([•\-\*]\s*)"""), "")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 7.dp, end = 8.dp)
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(RadiantCyan)
                                    )
                                    Text(
                                        text = parseBoldMarkdown(bulletContent),
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            // Standard Paragraph
                            else -> {
                                Text(
                                    text = parseBoldMarkdown(trimmed),
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        }
                    } else {
                        // Empty line for paragraph breathing room
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// Parses **bold** strings into AnnotatedString with bold white text
fun parseBoldMarkdown(rawText: String) = buildAnnotatedString {
    val parts = rawText.split("**")
    parts.forEachIndexed { i, part ->
        if (i % 2 == 1) {
            // Bold part
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            ) {
                append(part)
            }
        } else {
            // Normal part
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFCBD5E1)
                )
            ) {
                append(part)
            }
        }
    }
}
