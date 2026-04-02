package com.example.focustimerapp.core.database.dao

import androidx.room.*
import com.example.focustimerapp.core.database.entity.TaskEntity
import com.example.focustimerapp.core.database.entity.TaskStatus
import com.example.focustimerapp.core.database.model.TaskWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("""
    SELECT 
        t.*,
        COALESCE(SUM(ws.earned_cents), 0) AS totalEarnedCents,
        COALESCE(SUM(ws.duration_seconds), 0) AS totalSeconds
    FROM tasks t
    LEFT JOIN work_sessions ws
        ON ws.task_id = t.id
    GROUP BY t.id
    ORDER BY t.created_at DESC
    """)
    fun observeTasksWithStats(): Flow<List<TaskWithStats>>

    @Query("""
    UPDATE tasks
    SET status = :status,
        total_seconds = :totalSeconds,
        total_earned_cents = :totalEarnedCents,
        updated_at = :updatedAt
    WHERE id = :taskId
    """)
    suspend fun updateTaskAfterSession(
        taskId: Long,
        status: TaskStatus,
        totalSeconds: Long,
        totalEarnedCents: Long,
        updatedAt: String
    )


    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY created_at DESC")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC")
    fun observeCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    /**
     * Update task status (used by WorkSessionRepository)
     */
    @Query("""
        UPDATE tasks
        SET status = :status,
            updated_at = :updatedAt
        WHERE id = :taskId
    """)
    suspend fun updateStatus(
        taskId: Long,
        status: TaskStatus,
        updatedAt: String
    )
}