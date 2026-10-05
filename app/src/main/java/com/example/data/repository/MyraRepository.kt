package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.MyraDao
import com.example.data.model.AssistantConfig
import com.example.data.model.ChatMessage
import com.example.data.model.DailyTodo
import com.example.data.model.MoodEntry
import com.example.data.model.SavedTranslation
import com.example.data.model.VoiceNote
import com.example.data.model.WorkflowTask
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow

class MyraRepository(
    private val dao: MyraDao,
    private val geminiClient: GeminiClient = GeminiClient()
) {
    val messages: Flow<List<ChatMessage>> = dao.getAllMessages()
    val tasks: Flow<List<WorkflowTask>> = dao.getAllTasks()
    val dailyTodos: Flow<List<DailyTodo>> = dao.getAllDailyTodos()
    val voiceNotes: Flow<List<VoiceNote>> = dao.getAllVoiceNotes()
    val moodEntries: Flow<List<MoodEntry>> = dao.getAllMoodEntries()
    val latestMood: Flow<MoodEntry?> = dao.getLatestMood()
    val translations: Flow<List<SavedTranslation>> = dao.getAllTranslations()
    val config: Flow<AssistantConfig?> = dao.getConfig()

    suspend fun addMessage(message: ChatMessage): Long = dao.insertMessage(message)

    suspend fun clearChat() = dao.clearChatHistory()

    suspend fun addTask(task: WorkflowTask) = dao.insertTask(task)

    suspend fun updateTask(task: WorkflowTask) = dao.updateTask(task)

    suspend fun deleteTask(task: WorkflowTask) = dao.deleteTask(task)

    // Daily Todos
    suspend fun addDailyTodo(todo: DailyTodo) = dao.insertDailyTodo(todo)
    suspend fun updateDailyTodo(todo: DailyTodo) = dao.updateDailyTodo(todo)
    suspend fun deleteDailyTodo(todo: DailyTodo) = dao.deleteDailyTodo(todo)

    // Voice Notes
    suspend fun addVoiceNote(note: VoiceNote) = dao.insertVoiceNote(note)
    suspend fun deleteVoiceNote(note: VoiceNote) = dao.deleteVoiceNote(note)

    // Mood Entries
    suspend fun addMoodEntry(entry: MoodEntry) = dao.insertMoodEntry(entry)
    suspend fun deleteMoodEntry(entry: MoodEntry) = dao.deleteMoodEntry(entry)

    suspend fun addTranslation(translation: SavedTranslation) = dao.insertTranslation(translation)

    suspend fun deleteTranslation(translation: SavedTranslation) = dao.deleteTranslation(translation)

    suspend fun saveConfig(config: AssistantConfig) = dao.updateConfig(config)

    suspend fun askAssistant(
        prompt: String,
        config: AssistantConfig,
        imageBitmap: Bitmap? = null,
        history: List<ChatMessage> = emptyList()
    ): String {
        val systemInstruction = buildString {
            append("You are ${config.name}")
            if (config.nativeName.isNotBlank() && config.name.equals("Myra", ignoreCase = true)) {
                append(" (${config.nativeName})")
            }
            append(", an exceptionally intelligent, helpful, multi-capable, and friendly AI Assistant. ")
            append("You excel at coding, translations across languages (especially Hindi, English, Spanish, Japanese, French, etc.), visual screen analysis, and task workflow management. ")
            when (config.personaMode) {
                "Coding Expert" -> append("Tone: Highly technical, precise, providing complete working code snippets with best practices. ")
                "Travel Polyglot" -> append("Tone: Engaging travel guide, offering phonetic guides, cultural context, and conversational translations. ")
                "Creative" -> append("Tone: Inspiring, expressive, witty, and imaginative. ")
                else -> append("Tone: Warm, sharp, adaptive, concise, and structured. ")
            }
            append("Format code blocks with markdown syntax (```language ... ```). ")
            append("Structure your responses specifically for mobile screens: use structured bullet points, short paragraphs (2-3 sentences maximum), bold key terms for effortless scanning, and neat code blocks. Avoid long uninterrupted walls of text. ")
            append("Always be responsive to your current name '${config.name}'. ")
            if (prompt.contains("sleep", ignoreCase = true) || prompt.contains("सो जाओ")) {
                append("If the user tells you to sleep or go to background, confirm concisely that you are entering sleep/standby mode and listening in the background.")
            }
        }

        val turnHistory = history.map { 
            val role = if (it.sender == "user") "user" else "model"
            role to it.content 
        }

        return geminiClient.generateContent(
            prompt = prompt,
            systemInstruction = systemInstruction,
            imageBitmap = imageBitmap,
            conversationHistory = turnHistory
        )
    }
}
