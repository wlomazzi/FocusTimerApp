package com.example.focustimerapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focustimerapp.core.database.entity.SessionStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkSession): Long

    @Update
    suspend fun update(session: WorkSession)

    @Query(
        """
        SELECT * FROM work_sessions
        WHERE task_id = :taskId
        AND status = 'RUNNING'
        LIMIT 1
        """
    )
    suspend fun getRunningSessionForTask(taskId: Long): WorkSession?

    /*
     * Returns the current active session globally.
     * Active means RUNNING or PAUSED, but only for tasks that are NOT completed.
     */
    @Query(
        """
        SELECT ws.*
        FROM work_sessions ws
        INNER JOIN tasks t ON t.id = ws.task_id
        WHERE ws.status IN ('RUNNING', 'PAUSED')
          AND t.is_completed = 0
        ORDER BY ws.started_at DESC
        LIMIT 1
        """
    )
    suspend fun getRunningSession(): WorkSession?

    /*
     * Observes the current active session globally.
     * Active means RUNNING or PAUSED, but only for tasks that are NOT completed.
     */
    @Query(
        """
        SELECT ws.*
        FROM work_sessions ws
        INNER JOIN tasks t ON t.id = ws.task_id
        WHERE ws.status IN ('RUNNING', 'PAUSED')
          AND t.is_completed = 0
        ORDER BY ws.started_at DESC
        LIMIT 1
        """
    )
    fun observeRunningSession(): Flow<WorkSession?>

    @Query(
        """
        UPDATE work_sessions
        SET duration_seconds = :durationSeconds,
            earned_cents = :earnedCents,
            status = :status,
            ended_at = :endedAt,
            updated_at = :updatedAt
        WHERE id = :sessionId
        """
    )
    suspend fun updateSessionState(
        sessionId: Long,
        durationSeconds: Int,
        earnedCents: Long,
        status: SessionStatus,
        endedAt: LocalDateTime?,
        updatedAt: LocalDateTime
    )

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
        DELETE FROM work_sessions
        WHERE task_id = :taskId
        """
    )
    suspend fun deleteSessionsForTask(taskId: Long)
}