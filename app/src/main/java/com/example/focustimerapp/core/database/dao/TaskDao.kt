package com.example.focustimerapp.core.database.dao

import androidx.room.*
import com.example.focustimerapp.core.database.entity.TaskEntity
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



    /**
     * Observe all tasks ordered by most recently created first.
     */

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    /**
     * Observe only active (not completed) tasks.
     */
    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY created_at DESC")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    /**
     * Observe completed tasks ordered by completion date descending.
     */
    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC")
    fun observeCompletedTasks(): Flow<List<TaskEntity>>

    /**
     * Get a single task by its id.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    /**
     * Insert a new task.
     */
    @Insert
    suspend fun insert(task: TaskEntity): Long

    /**
     * Update an existing task.
     */
    @Update
    suspend fun update(task: TaskEntity)

    /**
     * Delete a task.
     */
    @Delete
    suspend fun delete(task: TaskEntity)
}