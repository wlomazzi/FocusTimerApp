package com.example.focustimerapp.feature.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.usecase.GetTaskDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle["taskId"]) {
        "Missing navigation argument: taskId"
    }

    val uiState = getTaskDetailUseCase(taskId)
        .map { TaskDetailUiState.Success(it) as TaskDetailUiState }
        .onStart { emit(TaskDetailUiState.Loading) }
        .catch { emit(TaskDetailUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskDetailUiState.Loading
        )
}