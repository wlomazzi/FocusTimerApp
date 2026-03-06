package com.example.focustimerapp.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.PeriodFilter
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import com.example.focustimerapp.core.domain.usecase.ObserveAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeAllTasksUseCase: ObserveAllTasksUseCase,
    workSessionRepository: WorkSessionRepository
) : ViewModel() {

    /*
     * Flow containing all tasks from repository
     */
    private val tasksFlow = observeAllTasksUseCase()

    /*
     * Flow observing the currently running session
     */
    private val runningSessionFlow =
        workSessionRepository.observeRunningSession()

    /*
     * Period filter selected in the UI
     */
    private val periodFilter =
        MutableStateFlow(PeriodFilter.ALL)

    /*
     * Public UI state
     */
    val uiState: StateFlow<DashboardUiState> =
        combine(
            tasksFlow,
            runningSessionFlow,
            periodFilter
        ) { tasks, runningSession, period ->

            val filteredTasks =
                filterTasksByPeriod(tasks, period)

            /*
             * DO NOT remove the running task from the list.
             * The UI (DashboardScreen) will decide how to display it.
             */
            val totalEarnedCents =
                filteredTasks.sumOf { it.totalEarnedCents }

            val totalSeconds =
                filteredTasks.sumOf { it.totalSeconds }

            DashboardUiState(
                tasks = filteredTasks,
                totalEarningsCents = totalEarnedCents,
                totalSeconds = totalSeconds,
                runningSession = runningSession,
                period = period
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                DashboardUiState()
            )

    /*
     * Update selected period
     */
    fun setPeriod(period: PeriodFilter) {
        periodFilter.value = period
    }

    /*
     * Filter tasks by period
     */
    private fun filterTasksByPeriod(
        tasks: List<Task>,
        period: PeriodFilter
    ): List<Task> {

        if (period == PeriodFilter.ALL) {
            return tasks
        }

        val now = LocalDateTime.now()

        val startDate = when (period) {

            PeriodFilter.TODAY ->
                now.toLocalDate().atStartOfDay()

            PeriodFilter.WEEK ->
                now.minusDays(7)

            PeriodFilter.MONTH ->
                now.minusMonths(1)

            PeriodFilter.ALL ->
                return tasks

/*            PeriodFilter.CUSTOM ->
                return tasks*/
        }

        return tasks.filter { task ->

            val completedAt =
                task.completedAt ?: return@filter false

            val completedDate =
                runCatching {
                    LocalDateTime.parse(completedAt)
                }.getOrNull()
                    ?: return@filter false

            completedDate.isAfter(startDate)
        }
    }
}