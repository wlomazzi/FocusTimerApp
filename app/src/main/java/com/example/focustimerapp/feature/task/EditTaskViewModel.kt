package com.example.focustimerapp.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * EditTaskViewModel
 *
 * Responsible for:
 * - Loading a task by its identifier
 * - Exposing the selected task as observable state
 * - Providing available clients for selection
 * - Updating an existing task in the repository
 */
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    /*
     * Internal mutable state that holds the task currently being edited.
     * It is exposed as an immutable StateFlow to the UI layer.
     */
    private val _task = MutableStateFlow<Task?>(null)

    /*
     * Public immutable view of the current task.
     * The UI observes this state to reactively update fields.
     */
    val task: StateFlow<Task?> = _task.asStateFlow()

    /*
     * Stream of clients retrieved from the repository.
     * Used to populate the client selection dropdown in the UI.
     */
    val clients = clientRepository.observeClients()

    /**
     * Loads a task from the repository using its unique identifier.
     * This should be called when the Edit screen is opened.
     */
    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val result = taskRepository.getTaskById(taskId)
            //println("DEBUG TASK RESULT: $result")
            _task.value = result
        }
    }

    /**
     * Persists updated task data to the repository.
     * Should be triggered when the user confirms the update action.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }
}