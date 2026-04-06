package com.example.focustimerapp.feature.task

import com.example.focustimerapp.core.domain.usecase.TaskDetail

sealed class TaskDetailUiState {
    data object Loading : TaskDetailUiState()
    data class Success(val data: TaskDetail) : TaskDetailUiState()
    data class Error(val message: String) : TaskDetailUiState()
}