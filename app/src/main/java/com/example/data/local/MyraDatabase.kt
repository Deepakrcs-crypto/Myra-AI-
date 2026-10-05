package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AssistantConfig
import com.example.data.model.ChatMessage
import com.example.data.model.DailyTodo
import com.example.data.model.MoodEntry
import com.example.data.model.SavedTranslation
import com.example.data.model.VoiceNote
import com.example.data.model.WorkflowTask

@Database(
    entities = [
        ChatMessage::class,
        WorkflowTask::class,
        SavedTranslation::class,
        AssistantConfig::class,
        DailyTodo::class,
        VoiceNote::class,
        MoodEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MyraDatabase : RoomDatabase() {
    abstract fun myraDao(): MyraDao

    companion object {
        @Volatile
        private var INSTANCE: MyraDatabase? = null

        fun getInstance(context: Context): MyraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyraDatabase::class.java,
                    "myra_assistant_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
