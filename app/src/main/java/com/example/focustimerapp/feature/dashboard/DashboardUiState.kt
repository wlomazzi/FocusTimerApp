package com.example.focustimerapp.feature.dashboard

import com.example.focustimerapp.core.domain.model.PeriodFilter
import com.example.focustimerapp.core.domain.model.Task
import java.time.LocalDateTime
import com.example.focustimerapp.core.database.entity.WorkSession
/**
 * Represents the UI state of the dashboard screen.
 *
 * Aggregates:
 * - The list of tasks
 * - The total earnings across all tasks
 * - The total worked time in seconds
 * - The currently selected period filter
 * - Optional custom date range
 */


data class DashboardUiState(
    val tasks: List<Task> = emptyList(),
    val totalEarningsCents: Long = 0,
    val totalSeconds: Long = 0,
    val runningSession: WorkSession? = null,
    val period: PeriodFilter = PeriodFilter.ALL,
    val customStart: LocalDateTime? = null,
    val customEnd: LocalDateTime? = null
){

}