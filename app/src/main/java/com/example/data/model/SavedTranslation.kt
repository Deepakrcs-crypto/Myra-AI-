package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_translations")
data class SavedTranslation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val phonetic: String? = null,
    val culturalNote: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
