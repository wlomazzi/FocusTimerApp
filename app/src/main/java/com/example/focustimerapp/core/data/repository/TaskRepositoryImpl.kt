package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.dao.WorkSessionDao
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

    override fun observeAllTasks(): Flow<List<Task>> =
        taskDao.observeTasksWithStats()
            .map { statsList ->
                statsList.map { stats ->
                    stats.task.toDomain(
                        totalEarnedCents = stats.totalEarnedCents,
                        totalSeconds = stats.totalSeconds
                    )
                }
            }

    override fun observeActiveTasks(): Flow<List<Task>> =
        observeAllTasks()
            .map { tasks -> tasks.filter { !it.isCompleted } }

    override fun observeCompletedTasks(): Flow<List<Task>> =
        observeAllTasks()
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

    /*
     * NEW: get sessions
     */
    override suspend fun getSessionsByTaskId(taskId: Long): List<WorkSession> {
        return workSessionDao.getSessionsForTask(taskId)
    }

    /*
     * NEW: delete session
     */
    override suspend fun deleteSession(sessionId: Long) {
        workSessionDao.deleteSessionById(sessionId)
    }
}