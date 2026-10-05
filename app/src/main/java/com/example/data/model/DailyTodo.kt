package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_todos")
data class DailyTodo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val reminderTime: String = "No reminder", // e.g. "09:00 AM", "02:00 PM", "06:30 PM"
    val isCompleted: Boolean = false,
    val category: String = "Daily", // "Work", "Personal", "Health", "Focus"
    val createdAt: Long = System.currentTimeMillis()
)
