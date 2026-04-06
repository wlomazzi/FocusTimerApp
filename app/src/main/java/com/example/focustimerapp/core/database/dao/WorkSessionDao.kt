package com.example.focustimerapp.core.database.dao

import androidx.room.*
import com.example.focustimerapp.core.database.model.TaskTotals
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
     * Returns the currently running session globally.
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
     * Observes the current running session.
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
     * Returns the running session for a specific task.
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
     * Returns a session by id.
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE id = :sessionId
        LIMIT 1
        """
    )
    suspend fun getSessionById(sessionId: Long): WorkSession?

    /**
     * Closes a session when paused or finished.
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
     * Returns all sessions for a given task.
     */
    @Query(
        """
        SELECT * FROM work_sessions
        WHERE task_id = :taskId
        ORDER BY started_at DESC
        """
    )
    suspend fun getSessionsForTask(taskId: Long): List<WorkSession>

    @Query(
        """
        SELECT 
            COALESCE(SUM(duration_seconds), 0) as totalSeconds,
            COALESCE(SUM(earned_cents), 0) as totalEarnedCents
        FROM work_sessions
        WHERE task_id = :taskId
        """
    )
    suspend fun getTaskTotals(taskId: Long): TaskTotals

    /**
     * Deletes all sessions for a given task.
     */
    @Query(
        """
        DELETE FROM work_sessions
        WHERE task_id = :taskId
        """
    )
    suspend fun deleteSessionsForTask(taskId: Long)

    /**
     * Deletes a specific session by id.
     */
    @Query(
        """
        DELETE FROM work_sessions
        WHERE id = :sessionId
        """
    )
    suspend fun deleteSessionById(sessionId: Long)
}