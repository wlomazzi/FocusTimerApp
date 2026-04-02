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
     * Start or Resume a task
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

        // Update task after change
        updateTaskAfterSessionChange(taskId)

        return sessionId
    }

    /**
     * Pause current session
     */
    override suspend fun pauseSession() {

        val session = workSessionDao.getRunningSession()
            ?: throw IllegalStateException("No running session")

        val now = LocalDateTime.now()

        val duration = Duration.between(session.startedAt, now).seconds.toInt()

        val earned = ((duration / 3600.0) * session.rateCents).toLong()

        workSessionDao.closeSession(
            sessionId = session.id,
            durationSeconds = duration,
            earnedCents = earned,
            endedAt = now,
            updatedAt = now
        )

        // Update task after change
        updateTaskAfterSessionChange(session.taskId)
    }

    /**
     * Finish task
     */
    override suspend fun finishSession() {

        val session = workSessionDao.getRunningSession()

        val now = LocalDateTime.now()

        if (session != null) {

            val duration = Duration.between(session.startedAt, now).seconds.toInt()

            val earned = ((duration / 3600.0) * session.rateCents).toLong()

            workSessionDao.closeSession(
                sessionId = session.id,
                durationSeconds = duration,
                earnedCents = earned,
                endedAt = now,
                updatedAt = now
            )

            // Update task after change
            updateTaskAfterSessionChange(session.taskId)
        }
    }

    /**
     * Central rule: update BOTH status and totals
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

        // Get totals from DB
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

    override suspend fun getRunningSession(
        taskId: Long
    ): WorkSession? =
        workSessionDao.getRunningSessionForTask(taskId)

    override suspend fun getSessionsForTask(
        taskId: Long
    ): List<WorkSession> =
        workSessionDao.getSessionsForTask(taskId)
}