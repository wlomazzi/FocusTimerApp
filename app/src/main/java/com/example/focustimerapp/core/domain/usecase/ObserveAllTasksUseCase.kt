package com.example.focustimerapp.core.domain.usecase

import com.example.focustimerapp.core.domain.repository.TaskRepository

class ObserveAllTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(
        startDateTime: String?,
        endDateTime: String?
    ) = repository.observeAllTasks(startDateTime, endDateTime)
}