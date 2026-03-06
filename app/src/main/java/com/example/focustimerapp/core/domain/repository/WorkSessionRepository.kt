package com.example.focustimerapp.core.domain.repository

import com.example.focustimerapp.core.database.entity.SessionStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/*
 * Defines work session business operations.
 */
interface WorkSessionRepository {

    suspend fun startSession(
        taskId: Long,
        rateCents: Long
    ): Long

    suspend fun updateSession(
        sessionId: Long,
        durationSeconds: Int,
        earnedCents: Long,
        status: SessionStatus,
        endedAt: LocalDateTime? = null
    )

    suspend fun getRunningSession(taskId: Long): WorkSession?

    suspend fun getSessionsForTask(taskId: Long): List<WorkSession>

    fun observeRunningSession(): Flow<WorkSession?>
}