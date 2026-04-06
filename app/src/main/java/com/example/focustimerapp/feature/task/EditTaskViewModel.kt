package com.example.focustimerapp.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.ClientRepository
import com.example.focustimerapp.core.domain.repository.TaskRepository
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository,
    private val workSessionRepository: WorkSessionRepository
) : ViewModel() {

    data class EditableSession(
        val id: Long,
        val original: WorkSession,
        val startedAt: LocalDateTime,
        val endedAt: LocalDateTime?
    )

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    private val _sessions = MutableStateFlow<List<WorkSession>>(emptyList())
    val sessions: StateFlow<List<WorkSession>> = _sessions.asStateFlow()

    private val _editableSessions = MutableStateFlow<List<EditableSession>>(emptyList())
    val editableSessions: StateFlow<List<EditableSession>> = _editableSessions.asStateFlow()

    val clients = clientRepository.observeClients()

    private var currentTaskId: Long? = null

    /**
     * Load task and sessions.
     */
    fun loadTask(taskId: Long) {
        currentTaskId = taskId

        viewModelScope.launch {
            reloadTaskAndSessions()
        }
    }

    /**
     * Update task fields directly.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
            reloadTaskAndSessions()
        }
    }

    /**
     * Delete a session.
     *
     * Task totals are recalculated inside the repository.
     */
    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            taskRepository.deleteSession(sessionId)
            reloadTaskAndSessions()
        }
    }

    /**
     * Update session start time.
     *
     * Session duration and earned values are recalculated inside the repository.
     */
    fun updateSessionStart(id: Long, newStart: LocalDateTime) {
        viewModelScope.launch {
            val session = _sessions.value.find { it.id == id } ?: return@launch

            val updated = session.copy(
                startedAt = newStart,
                updatedAt = LocalDateTime.now()
            )

            workSessionRepository.updateSession(updated)
            reloadTaskAndSessions()
        }
    }

    /**
     * Update session end time.
     *
     * Session duration and earned values are recalculated inside the repository.
     */
    fun updateSessionEnd(id: Long, newEnd: LocalDateTime) {
        viewModelScope.launch {
            val session = _sessions.value.find { it.id == id } ?: return@launch

            val updated = session.copy(
                endedAt = newEnd,
                updatedAt = LocalDateTime.now()
            )

            workSessionRepository.updateSession(updated)
            reloadTaskAndSessions()
        }
    }

    /**
     * Persist all edited sessions.
     */
    fun saveSessionEdits() {
        viewModelScope.launch {
            _editableSessions.value.forEach { editable ->
                val updated = editable.original.copy(
                    startedAt = editable.startedAt,
                    endedAt = editable.endedAt,
                    updatedAt = LocalDateTime.now()
                )

                workSessionRepository.updateSession(updated)
            }

            reloadTaskAndSessions()
        }
    }

    /**
     * Reload task and sessions from repositories.
     */
    private suspend fun reloadTaskAndSessions() {
        val taskId = currentTaskId ?: return

        _task.value = taskRepository.getTaskById(taskId)

        val sessions = taskRepository.getSessionsByTaskId(taskId)
        _sessions.value = sessions

        _editableSessions.value = sessions.map {
            EditableSession(
                id = it.id,
                original = it,
                startedAt = it.startedAt,
                endedAt = it.endedAt
            )
        }
    }
}