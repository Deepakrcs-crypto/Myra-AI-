package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workflow_tasks")
data class WorkflowTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "General", // "Coding", "Travel", "Personal", "Work"
    val isCompleted: Boolean = false,
    val priority: String = "Normal", // "High", "Normal", "Low"
    val createdAt: Long = System.currentTimeMillis()
)
