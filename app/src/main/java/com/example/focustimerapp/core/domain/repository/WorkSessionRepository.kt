package com.example.focustimerapp.core.domain.repository

import com.example.focustimerapp.core.database.entity.WorkSession
import kotlinx.coroutines.flow.Flow

interface WorkSessionRepository {

    /**
     * Start or resume a task (creates a new session)
     */
    suspend fun startSession(
        taskId: Long,
        rateCents: Long
    ): Long

    /**
     * Pause current session (closes it)
     */
    suspend fun pauseSession()

    /**
     * Finish current session (closes it)
     */
    suspend fun finishSession()

    /**
     * Observe current running session
     */
    fun observeRunningSession(): Flow<WorkSession?>

    /**
     * Get running session for a specific task
     */
    suspend fun getRunningSession(taskId: Long): WorkSession?

    /**
     * Get all sessions for a task
     */
    suspend fun getSessionsForTask(taskId: Long): List<WorkSession>
}