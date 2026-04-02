package com.example.focustimerapp.core.database.dao

import androidx.room.*
import com.example.focustimerapp.core.database.entity.WorkSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkSession): Long

    @Update
    suspend fun update(session: WorkSession)

    /**
     * Get current running session globally
     * (only one allowed in the app)
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE ended_at IS NULL
        ORDER BY started_at DESC
        LIMIT 1
        """
    )
    suspend fun getRunningSession(): WorkSession?

    /**
     * Observe current running session
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE ended_at IS NULL
        ORDER BY started_at DESC
        LIMIT 1
        """
    )
    fun observeRunningSession(): Flow<WorkSession?>

    /**
     * Get running session for a specific task
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE task_id = :taskId
          AND ended_at IS NULL
        LIMIT 1
        """
    )
    suspend fun getRunningSessionForTask(taskId: Long): WorkSession?

    /**
     * Close a session (used for pause/finish)
     */
    @Query(
        """
        UPDATE work_sessions
        SET duration_seconds = :durationSeconds,
            earned_cents = :earnedCents,
            ended_at = :endedAt,
            updated_at = :updatedAt
        WHERE id = :sessionId
        """
    )
    suspend fun closeSession(
        sessionId: Long,
        durationSeconds: Int,
        earnedCents: Long,
        endedAt: LocalDateTime,
        updatedAt: LocalDateTime
    )

    /**
     * Get all sessions of a task
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE task_id = :taskId
        ORDER BY started_at DESC
        """
    )
    suspend fun getSessionsForTask(taskId: Long): List<WorkSession>

    /**
     * Delete all sessions of a task
     */
    @Query(
        """
        DELETE FROM work_sessions
        WHERE task_id = :taskId
        """
    )
    suspend fun deleteSessionsForTask(taskId: Long)
}