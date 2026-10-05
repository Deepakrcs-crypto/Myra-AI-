package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTodo
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel

@Composable
fun DailyPlannerScreen(
    viewModel: MyraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dailyTodos by viewModel.dailyTodos.collectAsState()

    var newTodoText by remember { mutableStateOf("") }
    var selectedReminder by remember { mutableStateOf("09:00 AM") }
    var selectedCategory by remember { mutableStateOf("Work") }
    var activeFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Completed

    val totalCount = dailyTodos.size
    val completedCount = dailyTodos.count { it.isCompleted }
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "progressAnim")

    val reminderOptions = listOf("09:00 AM", "01:00 PM", "05:30 PM", "Evening", "No reminder")
    val categoryOptions = listOf("Work", "Personal", "Health", "Focus")

    val filteredTodos = when (activeFilter) {
        1 -> dailyTodos.filter { !it.isCompleted }
        2 -> dailyTodos.filter { it.isCompleted }
        else -> dailyTodos
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(16.dp)
            .testTag("daily_planner_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Smart Daily Planner",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Minimalist task manager with simple reminders",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Today, contentDescription = null, tint = RadiantEmerald, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Today's Focus", color = RadiantEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Tracking Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth().testTag("planner_progress_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DAILY COMPLETION",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$completedCount of $totalCount completed",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = if (completedCount == totalCount && totalCount > 0) RadiantEmerald else RadiantCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animated Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (completedCount == totalCount && totalCount > 0) RadiantEmerald else RadiantCyan,
                    trackColor = Color(0xFF0F172A),
                    strokeCap = StrokeCap.Round
                )

                if (completedCount == totalCount && totalCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GlowingAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All daily goals accomplished! Great momentum.", color = GlowingAmber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Add To-Do Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTodoText,
                        onValueChange = { newTodoText = it },
                        placeholder = { Text("Add daily to-do...", color = Color(0xFF64748B), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f).testTag("input_new_todo_title"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedBorderColor = RadiantCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (newTodoText.isNotBlank()) {
                                viewModel.addDailyTodo(newTodoText, selectedReminder, selectedCategory)
                                newTodoText = ""
                                Toast.makeText(context, "Added to Daily Planner", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RadiantCyan),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp).testTag("btn_submit_todo")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Todo", tint = Color(0xFF090D16), modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reminder selector chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                    reminderOptions.forEach { rem ->
                        val isSel = selectedReminder == rem
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) RadiantCyan.copy(alpha = 0.2f) else Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RadiantCyan else Color(0xFF1E293B)),
                            modifier = Modifier.clickable { selectedReminder = rem }
                        ) {
                            Text(
                                text = rem,
                                color = if (isSel) RadiantCyan else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Category selector chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val isSel = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) RadiantViolet.copy(alpha = 0.2f) else Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RadiantViolet else Color(0xFF1E293B)),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) RadiantViolet else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Pills: All | Pending | Completed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All (${dailyTodos.size})", "Pending (${dailyTodos.count { !it.isCompleted }})", "Done ($completedCount)").forEachIndexed { index, label ->
                val isSel = activeFilter == index
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RadiantCyan else Color(0xFF1E293B)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeFilter = index }
                ) {
                    Text(
                        text = label,
                        color = if (isSel) RadiantCyan else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily To-Do List
        if (filteredTodos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No to-dos in this view.\nAdd a daily goal above to stay focused!", color = Color(0xFF64748B), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTodos, key = { it.id }) { todo ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth().testTag("todo_item_${todo.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = todo.isCompleted,
                                    onCheckedChange = { viewModel.toggleDailyTodo(todo) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = RadiantEmerald,
                                        uncheckedColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.size(28.dp).testTag("checkbox_todo_${todo.id}")
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = todo.title,
                                        color = if (todo.isCompleted) Color(0xFF64748B) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        if (todo.reminderTime != "No reminder") {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Alarm, contentDescription = null, tint = GlowingAmber, modifier = Modifier.size(11.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(todo.reminderTime, color = GlowingAmber, fontSize = 10.sp)
                                            }
                                        }

                                        Text(todo.category, color = RadiantViolet, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteDailyTodo(todo) },
                                modifier = Modifier.size(28.dp).testTag("btn_delete_todo_${todo.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
