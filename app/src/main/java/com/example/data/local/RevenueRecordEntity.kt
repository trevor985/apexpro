package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revenue_records")
data class RevenueRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val timestamp: Long,
    val category: String, // "SAAS", "SIGNALS", "ARBITRAGE", "AGENCY"
    val notes: String = ""
)
