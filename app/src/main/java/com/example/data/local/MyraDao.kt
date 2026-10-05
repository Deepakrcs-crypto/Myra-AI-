package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssistantConfig
import com.example.data.model.ChatMessage
import com.example.data.model.DailyTodo
import com.example.data.model.MoodEntry
import com.example.data.model.SavedTranslation
import com.example.data.model.VoiceNote
import com.example.data.model.WorkflowTask
import kotlinx.coroutines.flow.Flow

@Dao
interface MyraDao {
    // Chat messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // Tasks
    @Query("SELECT * FROM workflow_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<WorkflowTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: WorkflowTask): Long

    @Update
    suspend fun updateTask(task: WorkflowTask)

    @Delete
    suspend fun deleteTask(task: WorkflowTask)

    // Daily Todos (Smart Daily Planner)
    @Query("SELECT * FROM daily_todos ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllDailyTodos(): Flow<List<DailyTodo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyTodo(todo: DailyTodo): Long

    @Update
    suspend fun updateDailyTodo(todo: DailyTodo)

    @Delete
    suspend fun deleteDailyTodo(todo: DailyTodo)

    // Voice Notes & AI Summarizer
    @Query("SELECT * FROM voice_notes ORDER BY timestamp DESC")
    fun getAllVoiceNotes(): Flow<List<VoiceNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceNote(voiceNote: VoiceNote): Long

    @Delete
    suspend fun deleteVoiceNote(voiceNote: VoiceNote)

    // Mood & Mental Wellness Entries
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMood(): Flow<MoodEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry): Long

    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntry)

    // Saved translations
    @Query("SELECT * FROM saved_translations ORDER BY timestamp DESC")
    fun getAllTranslations(): Flow<List<SavedTranslation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: SavedTranslation): Long

    @Delete
    suspend fun deleteTranslation(translation: SavedTranslation)

    // Assistant Config
    @Query("SELECT * FROM assistant_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<AssistantConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConfig(config: AssistantConfig)
}
