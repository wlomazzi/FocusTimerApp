package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.dao.WorkSessionDao
import com.example.focustimerapp.core.database.entity.TaskStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val workSessionDao: WorkSessionDao
) : TaskRepository {

    override fun observeAllTasks(
        startDateTime: String?,
        endDateTime: String?
    ): Flow<List<Task>> =
        taskDao.observeTasksWithStats(startDateTime, endDateTime)
            .map { statsList ->
                statsList.map { stats ->
                    stats.task.toDomain(
                        totalEarnedCents = stats.totalEarnedCents,
                        totalSeconds = stats.totalSeconds
                    )
                }
            }

    override fun observeActiveTasks(): Flow<List<Task>> =
        observeAllTasks(null, null)
            .map { tasks -> tasks.filter { !it.isCompleted } }

    override fun observeCompletedTasks(): Flow<List<Task>> =
        observeAllTasks(null, null)
            .map { tasks -> tasks.filter { it.isCompleted } }

    override suspend fun getTaskById(taskId: Long): Task? =
        taskDao.getTaskById(taskId)?.toDomain()

    override suspend fun insert(task: Task): Long =
        taskDao.insert(task.toEntity())

    override suspend fun update(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun markTaskAsCompleted(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return

        val now = LocalDateTime.now().toString()

        val updatedTask = task.copy(
            isCompleted = true,
            completedAt = now,
            updatedAt = now
        )

        taskDao.update(updatedTask)
    }

    override suspend fun getSessionsByTaskId(taskId: Long): List<WorkSession> {
        return workSessionDao.getSessionsForTask(taskId)
    }

    override suspend fun deleteSession(sessionId: Long) {
        val session = workSessionDao.getSessionById(sessionId) ?: return
        val taskId = session.taskId

        workSessionDao.deleteSessionById(sessionId)

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
            updatedAt = LocalDateTime.now().toString()
        )
    }
}