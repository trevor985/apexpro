package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SprintTaskDao {
    @Query("SELECT * FROM sprint_tasks ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<SprintTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<SprintTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: SprintTaskEntity): Long

    @Update
    suspend fun updateTask(task: SprintTaskEntity)

    @Query("UPDATE sprint_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM sprint_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
