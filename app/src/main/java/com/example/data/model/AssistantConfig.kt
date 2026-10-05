package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistant_config")
data class AssistantConfig(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Myra",
    val nativeName: String = "मायरा",
    val personaMode: String = "Balanced", // "Balanced", "Coding Expert", "Travel Polyglot", "Creative"
    val voiceEnabled: Boolean = true,
    val sleepMode: Boolean = false,
    val preferredLanguage: String = "Auto", // "Auto", "Hindi", "English", "Spanish", "Japanese"
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
