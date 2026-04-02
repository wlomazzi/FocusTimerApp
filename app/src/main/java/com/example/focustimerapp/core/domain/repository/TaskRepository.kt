package com.example.focustimerapp.core.domain.repository

import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun observeAllTasks(): Flow<List<Task>>
    fun observeActiveTasks(): Flow<List<Task>>
    fun observeCompletedTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insert(task: Task): Long
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun markTaskAsCompleted(taskId: Long)
    suspend fun getSessionsByTaskId(taskId: Long): List<WorkSession>
    suspend fun deleteSession(sessionId: Long)
}