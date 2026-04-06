package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.dao.WorkSessionDao
import com.example.focustimerapp.core.database.entity.TaskStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

/*
 * Final architecture:
 * - WorkSession = immutable blocks
 * - endedAt == null => running
 * - Task.status = source of truth
 * - Totals MUST be persisted in Task
 */
class WorkSessionRepositoryImpl @Inject constructor(
    private val workSessionDao: WorkSessionDao,
    private val taskDao: TaskDao
) : WorkSessionRepository {

    /**
     * Start or resume a task by creating a new running session.
     */
    override suspend fun startSession(
        taskId: Long,
        rateCents: Long
    ): Long {
        val runningSession = workSessionDao.getRunningSession()

        if (runningSession != null) {
            throw IllegalStateException("Another task is already running")
        }

        val now = LocalDateTime.now()

        val session = WorkSession(
            taskId = taskId,
            startedAt = now,
            endedAt = null,
            durationSeconds = 0,
            rateCents = rateCents,
            earnedCents = 0,
            createdAt = now,
            updatedAt = now
        )

        val sessionId = workSessionDao.insert(session)

        updateTaskAfterSessionChange(taskId)

        return sessionId
    }

    /**
     * Pause the current running session.
     */
    override suspend fun pauseSession() {
        val session = workSessionDao.getRunningSession()
            ?: throw IllegalStateException("No running session")

        val now = LocalDateTime.now()
        val normalized = normalizeSessionForPersistence(
            session = session,
            startedAt = session.startedAt,
            endedAt = now
        )

        workSessionDao.update(normalized)
        updateTaskAfterSessionChange(session.taskId)
    }

    /**
     * Finish the current running session.
     */
    override suspend fun finishSession() {
        val session = workSessionDao.getRunningSession() ?: return

        val now = LocalDateTime.now()
        val normalized = normalizeSessionForPersistence(
            session = session,
            startedAt = session.startedAt,
            endedAt = now
        )

        workSessionDao.update(normalized)
        updateTaskAfterSessionChange(session.taskId)
    }

    /**
     * Update an existing session.
     *
     * Important:
     * When a session timestamp changes, duration and earned values must also be recalculated
     * before updating task totals.
     */
    override suspend fun updateSession(session: WorkSession) {
        val normalized = normalizeSessionForPersistence(
            session = session,
            startedAt = session.startedAt,
            endedAt = session.endedAt
        )

        workSessionDao.update(normalized)
        updateTaskAfterSessionChange(session.taskId)
    }

    /**
     * Recalculate session-derived fields before persistence.
     *
     * Rules:
     * - Running session: duration = 0, earned = 0
     * - Closed session: duration and earned are recalculated from timestamps
     * - Negative duration is blocked
     */
    private fun normalizeSessionForPersistence(
        session: WorkSession,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime?
    ): WorkSession {
        val now = LocalDateTime.now()

        if (endedAt == null) {
            return session.copy(
                startedAt = startedAt,
                endedAt = null,
                durationSeconds = 0,
                earnedCents = 0,
                updatedAt = now
            )
        }

        require(!endedAt.isBefore(startedAt)) {
            "Session end time cannot be before start time"
        }

        val durationSeconds = Duration.between(startedAt, endedAt).seconds.toInt()
        val earnedCents = ((durationSeconds / 3600.0) * session.rateCents).toLong()

        return session.copy(
            startedAt = startedAt,
            endedAt = endedAt,
            durationSeconds = durationSeconds,
            earnedCents = earnedCents,
            updatedAt = now
        )
    }

    /**
     * Central rule:
     * After any session change, recalculate task status and persisted totals.
     */
    private suspend fun updateTaskAfterSessionChange(taskId: Long) {
        val now = LocalDateTime.now()

        val sessions = workSessionDao.getSessionsForTask(taskId)

        val hasActive = sessions.any { it.endedAt == null }
        val hasSessions = sessions.isNotEmpty()

        val newStatus = when {
            hasActive -> TaskStatus.RUNNING
            hasSessions -> TaskStatus.FINISHED
            else -> TaskStatus.PENDING
        }

        val totals = workSessionDao.getTaskTotals(taskId)

        taskDao.updateTaskAfterSession(
            taskId = taskId,
            status = newStatus,
            totalSeconds = totals.totalSeconds,
            totalEarnedCents = totals.totalEarnedCents,
            updatedAt = now.toString()
        )
    }

    override fun observeRunningSession(): Flow<WorkSession?> =
        workSessionDao.observeRunningSession()

    override suspend fun getRunningSession(taskId: Long): WorkSession? =
        workSessionDao.getRunningSessionForTask(taskId)

    override suspend fun getSessionsForTask(taskId: Long): List<WorkSession> =
        workSessionDao.getSessionsForTask(taskId)
}