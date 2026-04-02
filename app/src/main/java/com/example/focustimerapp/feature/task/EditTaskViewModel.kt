package com.example.focustimerapp.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.ClientRepository
import com.example.focustimerapp.core.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for editing an existing task.
 *
 * Responsibilities:
 * - Load task details
 * - Load all sessions associated with the task
 * - Expose reactive state to the UI
 * - Update task data
 * - Delete individual sessions
 */
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    /*
     * Holds the task currently being edited.
     */
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    /*
     * Holds all sessions associated with the current task.
     */
    private val _sessions = MutableStateFlow<List<WorkSession>>(emptyList())
    val sessions: StateFlow<List<WorkSession>> = _sessions.asStateFlow()

    /*
     * Provides the list of clients for the dropdown selection.
     */
    val clients = clientRepository.observeClients()

    /*
     * Stores the current task id for future refresh operations.
     */
    private var currentTaskId: Long? = null

    /**
     * Loads the task and its related sessions.
     */
    fun loadTask(taskId: Long) {
        currentTaskId = taskId

        viewModelScope.launch {
            _task.value = taskRepository.getTaskById(taskId)
            refreshSessions()
        }
    }

    /**
     * Updates the task in the repository.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }

    /**
     * Deletes a work session and refreshes the local session list.
     */
    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            taskRepository.deleteSession(sessionId)
            refreshSessions()
        }
    }

    /**
     * Reloads all sessions associated with the current task.
     */
    private suspend fun refreshSessions() {
        val taskId = currentTaskId ?: return
        _sessions.value = taskRepository.getSessionsByTaskId(taskId)
    }
}