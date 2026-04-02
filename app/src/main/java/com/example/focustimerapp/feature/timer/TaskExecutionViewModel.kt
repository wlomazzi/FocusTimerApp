package com.example.focustimerapp.feature.timer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.repository.TaskRepository
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskExecutionViewModel @Inject constructor(
    private val workSessionRepository: WorkSessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    var uiState = mutableStateOf(TaskExecutionUiState())
        private set

    private var timerJob: Job? = null
    private var sessionStartTime: LocalDateTime? = null
    private var hourlyRateCents: Long = 0L

    fun loadTask(taskId: Long) {

        timerJob?.cancel()

        viewModelScope.launch {

            val task = taskRepository.getTaskById(taskId)
            val sessions = workSessionRepository.getSessionsForTask(taskId)

            val runningSession =
                sessions.find { it.endedAt == null }

            val completedSeconds =
                sessions
                    .filter { it.endedAt != null }
                    .sumOf { it.durationSeconds.toLong() }

            hourlyRateCents = task?.hourlyRateCents ?: 0L

            if (runningSession != null) {

                sessionStartTime = runningSession.startedAt

                val runningSeconds =
                    Duration.between(
                        runningSession.startedAt,
                        LocalDateTime.now()
                    ).seconds

                uiState.value = TaskExecutionUiState(
                    task = task,
                    sessions = sessions,
                    totalSeconds = completedSeconds + runningSeconds,
                    isRunning = true
                )

                startTimer(completedSeconds)

            } else {

                uiState.value = TaskExecutionUiState(
                    task = task,
                    sessions = sessions,
                    totalSeconds = completedSeconds,
                    isRunning = false
                )
            }
        }
    }

    fun startTask() {

        val task = uiState.value.task ?: return

        if (uiState.value.isRunning) return

        val scheduledDate = task.scheduledStartDate?.let {
            try { LocalDate.parse(it) } catch (_: Exception) { null }
        }

        if (scheduledDate != null && LocalDate.now().isBefore(scheduledDate)) {
            uiState.value = uiState.value.copy(
                errorMessage = "Task cannot start before scheduled date"
            )
            return
        }

        viewModelScope.launch {

            try {

                workSessionRepository.startSession(
                    taskId = task.id,
                    rateCents = task.hourlyRateCents
                )

                sessionStartTime = LocalDateTime.now()
                hourlyRateCents = task.hourlyRateCents

                uiState.value = uiState.value.copy(
                    isRunning = true,
                    errorMessage = null
                )

                startTimer(uiState.value.totalSeconds)

            } catch (e: IllegalStateException) {

                uiState.value = uiState.value.copy(
                    errorMessage = "Another task is already running"
                )
            }
        }
    }

    fun pauseTask() {

        if (!uiState.value.isRunning) return

        timerJob?.cancel()

        viewModelScope.launch {

            workSessionRepository.pauseSession()

            loadTask(uiState.value.task!!.id)
        }

        uiState.value = uiState.value.copy(
            isRunning = false
        )
    }

    fun finishTask() {

        if (!uiState.value.isRunning) return

        timerJob?.cancel()

        val task = uiState.value.task ?: return

        viewModelScope.launch {

            workSessionRepository.finishSession()

            taskRepository.markTaskAsCompleted(task.id)

            loadTask(task.id)
        }

        sessionStartTime = null

        uiState.value = uiState.value.copy(
            isRunning = false
        )
    }

    private fun startTimer(completedSeconds: Long) {

        timerJob?.cancel()

        timerJob = viewModelScope.launch {

            while (isActive) {

                delay(1000)

                val startedAt = sessionStartTime ?: return@launch

                val runningSeconds =
                    Duration.between(startedAt, LocalDateTime.now()).seconds

                val total = completedSeconds + runningSeconds

                val earned = calculateEarned(total)

                uiState.value = uiState.value.copy(
                    totalSeconds = total,
                    earnedCents = earned
                )
            }
        }
    }

    private fun calculateEarned(seconds: Long): Long {

        val valuePerSecond = hourlyRateCents / 3600.0
        return (seconds * valuePerSecond).toLong()
    }
}