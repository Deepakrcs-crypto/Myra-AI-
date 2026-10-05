package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val isCode: Boolean = false,
    val codeLanguage: String? = null,
    val actionType: String? = null // e.g. "vision", "translation", "sleep", "code"
)

enum class MessageSender {
    USER, ASSISTANT, SYSTEM
}
