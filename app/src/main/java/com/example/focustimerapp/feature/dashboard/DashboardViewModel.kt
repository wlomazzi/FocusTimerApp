package com.example.focustimerapp.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.model.PeriodFilter
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import com.example.focustimerapp.core.domain.usecase.ObserveAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val observeAllTasksUseCase: ObserveAllTasksUseCase,
    private val workSessionRepository: WorkSessionRepository
) : ViewModel() {

    private val runningSessionFlow =
        workSessionRepository.observeRunningSession()

    private val periodFilter =
        MutableStateFlow(PeriodFilter.ALL)

    private val _customRange =
        MutableStateFlow<Pair<LocalDate?, LocalDate?>>(null to null)

    /*
     * Core flow
     */
    private val tasksFlow =
        combine(periodFilter, _customRange) { period, range ->
            buildDateRange(period, range)
        }
            .flatMapLatest { (start, end) ->
                //println("FILTER DEBUG -> start: $start | end: $end")
                observeAllTasksUseCase(start, end)
            }

    /*
     * UI state
     */
    val uiState: StateFlow<DashboardUiState> =
        combine(
            tasksFlow,
            runningSessionFlow,
            periodFilter
        ) { tasks, runningSession, period ->

            val archivedTasks = tasks.filter { it.isArchived }
            val activeTasks = tasks.filter { !it.isArchived }

            val finalTasks =
                (activeTasks + archivedTasks)
                    .distinctBy { it.id }

            val totalEarnedCents =
                activeTasks.sumOf { it.totalEarnedCents }

            val totalSeconds =
                activeTasks.sumOf { it.totalSeconds }

            DashboardUiState(
                tasks = finalTasks,
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
     * Set period
     */
    fun setPeriod(period: PeriodFilter) {
        periodFilter.value = period
        _customRange.value = null to null
    }

    /*
     * Set custom range
     */
    fun setCustomRange(start: LocalDate?, end: LocalDate?) {
        _customRange.value = start to end
    }

    /*
     * Build date range (DATE only)
     */
    private fun buildDateRange(
        period: PeriodFilter,
        range: Pair<LocalDate?, LocalDate?>
    ): Pair<String?, String?> {

        val (start, end) = range

        // Custom range
        if (start != null && end != null) {
            return start.toDbDate() to end.toDbDate()
        }

        val today = LocalDate.now()

        return when (period) {

            PeriodFilter.TODAY -> {
                today.toDbDate() to today.toDbDate()
            }

            PeriodFilter.WEEK -> {
                today.minusDays(7).toDbDate() to today.toDbDate()
            }

            PeriodFilter.MONTH -> {
                today.minusMonths(1).toDbDate() to today.toDbDate()
            }

            PeriodFilter.ALL -> {
                null to null
            }
        }
    }

    /*
     * Format helper (DATE only)
     */
    private fun LocalDate.toDbDate(): String =
        this.toString()
}