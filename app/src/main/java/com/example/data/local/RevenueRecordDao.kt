package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RevenueRecordDao {
    @Query("SELECT * FROM revenue_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<RevenueRecordEntity>>

    @Query("SELECT * FROM revenue_records WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getRecordsSince(sinceTimestamp: Long): Flow<List<RevenueRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RevenueRecordEntity): Long

    @Query("DELETE FROM revenue_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("SELECT SUM(amount) FROM revenue_records WHERE timestamp >= :sinceTimestamp")
    fun getTotalAmountSince(sinceTimestamp: Long): Flow<Double?>
}
