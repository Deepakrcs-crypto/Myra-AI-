package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_notes")
data class VoiceNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val rawTranscript: String,
    val summary: String,
    val actionItems: String, // Pipe or newline separated action items
    val durationSeconds: Int = 15,
    val timestamp: Long = System.currentTimeMillis()
)
