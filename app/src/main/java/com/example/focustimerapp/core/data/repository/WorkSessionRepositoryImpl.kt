package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.database.dao.WorkSessionDao
import com.example.focustimerapp.core.database.entity.SessionStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/*
 * Concrete implementation of WorkSessionRepository.
 * Handles persistence and business rules for work sessions.
 */
class WorkSessionRepositoryImpl @Inject constructor(
    private val workSessionDao: WorkSessionDao
) : WorkSessionRepository {

    /*
     * Starts a new session for a task.
     *
     * BUSINESS RULE:
     * Only one RUNNING session can exist globally.
     * PAUSED sessions do NOT block a new session.
     */
    override suspend fun startSession(
        taskId: Long,
        rateCents: Long
    ): Long {

        val activeSession = workSessionDao.getRunningSession()

        /*
         * Block only if a session is actually RUNNING
         */
        if (activeSession?.status == SessionStatus.RUNNING) {
            throw IllegalStateException(
                "Another task is already running"
            )
        }

        val now = LocalDateTime.now()

        val session = WorkSession(
            taskId = taskId,
            startedAt = now,
            rateCents = rateCents,
            durationSeconds = 0,
            earnedCents = 0,
            endedAt = null,
            status = SessionStatus.RUNNING,
            createdAt = now,
            updatedAt = now
        )

        return workSessionDao.insert(session)
    }

    /*
     * Observes the currently active session globally.
     * (RUNNING or PAUSED depending on DAO implementation)
     */
    override fun observeRunningSession(): Flow<WorkSession?> =
        workSessionDao.observeRunningSession()

    /*
     * Updates session progress or status.
     */
    override suspend fun updateSession(
        sessionId: Long,
        durationSeconds: Int,
        earnedCents: Long,
        status: SessionStatus,
        endedAt: LocalDateTime?
    ) {

        val now = LocalDateTime.now()

        workSessionDao.updateSessionState(
            sessionId = sessionId,
            durationSeconds = durationSeconds,
            earnedCents = earnedCents,
            status = status,
            endedAt = endedAt,
            updatedAt = now
        )
    }

    /*
     * Returns the RUNNING session for a specific task.
     */
    override suspend fun getRunningSession(
        taskId: Long
    ): WorkSession? =
        workSessionDao.getRunningSessionForTask(taskId)

    /*
     * Returns all sessions belonging to a task.
     */
    override suspend fun getSessionsForTask(
        taskId: Long
    ): List<WorkSession> =
        workSessionDao.getSessionsForTask(taskId)
}