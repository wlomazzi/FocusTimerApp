package com.example.focustimerapp.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.ClientRepository
import com.example.focustimerapp.core.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    /**
     * Exposes the list of clients as StateFlow for dropdown usage.
     */
    val clients: StateFlow<List<Client>> =
        clientRepository.observeActiveClients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /**
     * Creates a new task aligned with the updated domain model.
     */
    fun createTask(
        clientId: Long,
        description: String,
        hourlyRateCents: Long,
        scheduledStartDate: String?
    ) {
        viewModelScope.launch {

            val now = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val task = Task(
                id = 0,
                clientId = clientId,
                description = description,
                hourlyRateCents = hourlyRateCents,
                scheduledStartDate = scheduledStartDate,
                isCompleted = false,
                completedAt = null,
                totalSeconds = 0,
                totalEarnedCents = 0,
                createdAt = now,
                updatedAt = now
            )

            taskRepository.insert(task)
        }
    }
}