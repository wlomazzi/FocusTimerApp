package com.example.focustimerapp.feature.timer

import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task

/*
 * Represents the UI state of task execution screen.
 */
data class TaskExecutionUiState(
    val task: Task? = null,
    val sessions: List<WorkSession> = emptyList(),
    val totalSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val currentSessionElapsed: Long = 0L,
    val earnedCents: Long = 0L,
    val errorMessage: String? = null
)