package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moodType: String, // "Joyful", "Good", "Calm", "Tired", "Stressed", "Down"
    val moodEmoji: String, // "🌟", "😊", "🌿", "😴", "😰", "🌧️"
    val note: String = "",
    val empatheticResponse: String,
    val breathingCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
