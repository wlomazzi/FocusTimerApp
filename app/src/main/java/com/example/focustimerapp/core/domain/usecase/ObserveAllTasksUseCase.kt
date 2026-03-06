package com.example.focustimerapp.core.domain.usecase

import com.example.focustimerapp.core.domain.repository.TaskRepository

class ObserveAllTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke() = repository.observeAllTasks()
}