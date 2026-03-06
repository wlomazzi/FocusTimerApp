package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.model.TaskWithStats
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Concrete implementation of TaskRepository.
 *
 * Responsibilities:
 * - Convert database entities to domain models
 * - Aggregate task statistics (earnings and worked time)
 * - Provide reactive task streams to the domain layer
 */
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    /**
     * Observes all tasks including aggregated statistics from work sessions.
     */
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

    /**
     * Observes only active tasks.
     */
    override fun observeActiveTasks(): Flow<List<Task>> =
        observeAllTasks()
            .map { tasks ->
                tasks.filter { !it.isCompleted }
            }

    /**
     * Observes only completed tasks.
     */
    override fun observeCompletedTasks(): Flow<List<Task>> =
        observeAllTasks()
            .map { tasks ->
                tasks.filter { it.isCompleted }
            }

    /**
     * Retrieves a single task by its id.
     */
    override suspend fun getTaskById(taskId: Long): Task? =
        taskDao.getTaskById(taskId)?.toDomain()

    /**
     * Inserts a new task into the database.
     */
    override suspend fun insert(task: Task): Long =
        taskDao.insert(task.toEntity())

    /**
     * Updates an existing task.
     */
    override suspend fun update(task: Task) {
        taskDao.update(task.toEntity())
    }

    /**
     * Deletes a task.
     */
    override suspend fun delete(task: Task) {
        taskDao.delete(task.toEntity())
    }

    /**
     * Marks a task as completed.
     */
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
}