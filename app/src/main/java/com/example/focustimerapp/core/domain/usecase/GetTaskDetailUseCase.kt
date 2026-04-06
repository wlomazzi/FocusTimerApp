package com.example.focustimerapp.core.domain.usecase

import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.TaskRepository
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import com.example.focustimerapp.core.database.entity.WorkSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class TaskDetail(
    val task: Task,
    val sessions: List<WorkSession>,
    val totalEarnedCents: Long,
    val totalDurationSeconds: Int
)

class GetTaskDetailUseCase(
    private val taskRepository: TaskRepository,
    private val workSessionRepository: WorkSessionRepository
) {

    operator fun invoke(taskId: Long): Flow<TaskDetail> = flow {

        val task = taskRepository.getTaskById(taskId)
            ?: throw IllegalStateException("Task not found")

        val sessions = workSessionRepository.getSessionsForTask(taskId)

        val totalEarned = sessions.sumOf { it.earnedCents }
        val totalDuration = sessions.sumOf { it.durationSeconds }

        emit(
            TaskDetail(
                task = task,
                sessions = sessions,
                totalEarnedCents = totalEarned,
                totalDurationSeconds = totalDuration
            )
        )
    }
}