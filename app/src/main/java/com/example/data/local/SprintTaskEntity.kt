package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sprint_tasks")
data class SprintTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "MARKETING", "PRODUCT", "SIGNALS", "OUTREACH"
    val potentialYield: Double,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)
