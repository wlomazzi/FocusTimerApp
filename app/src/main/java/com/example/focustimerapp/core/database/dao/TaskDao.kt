package com.example.focustimerapp.core.database.dao

import androidx.room.*
import com.example.focustimerapp.core.database.entity.TaskEntity
import com.example.focustimerapp.core.database.entity.TaskStatus
import com.example.focustimerapp.core.database.model.TaskWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /*
     * Returns all tasks with aggregated stats.
     * Filters by scheduled_start_date when provided.
     */
    @Query("""
    SELECT 
        t.*,
        COALESCE(SUM(ws.earned_cents), 0) AS totalEarnedCents,
        COALESCE(SUM(ws.duration_seconds), 0) AS totalSeconds
    FROM tasks t
    LEFT JOIN work_sessions ws
        ON ws.task_id = t.id
    WHERE 
        (:startDate IS NULL OR date(t.scheduled_start_date) >= date(:startDate))
        AND (:endDate IS NULL OR date(t.scheduled_start_date) <= date(:endDate))
    GROUP BY t.id
    ORDER BY t.scheduled_start_date 
    """)
    fun observeTasksWithStats(
        startDate: String?,
        endDate: String?
    ): Flow<List<TaskWithStats>>


    /*
     * Updates task totals and status after session changes.
     */
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


    /*
     * Returns all tasks ordered by creation date.
     */
    @Query("""
        SELECT * FROM tasks 
        ORDER BY created_at 
    """)
    fun observeAllTasks(): Flow<List<TaskEntity>>


    /*
     * Returns active (not completed, not archived) tasks.
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE is_completed = 0 AND is_archived = 0
        ORDER BY created_at 
    """)
    fun observeActiveTasks(): Flow<List<TaskEntity>>


    /*
     * Returns completed (not archived) tasks.
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE is_completed = 1 AND is_archived = 0
        ORDER BY completed_at 
    """)
    fun observeCompletedTasks(): Flow<List<TaskEntity>>


    /*
     * Returns a single task by id.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?


    @Insert
    suspend fun insert(task: TaskEntity): Long


    @Update
    suspend fun update(task: TaskEntity)


    @Delete
    suspend fun delete(task: TaskEntity)


    /*
     * Updates only task status.
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