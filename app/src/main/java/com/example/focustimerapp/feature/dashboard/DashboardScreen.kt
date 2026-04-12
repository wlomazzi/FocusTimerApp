package com.example.focustimerapp.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focustimerapp.core.domain.model.PeriodFilter
import com.example.focustimerapp.core.domain.model.Task
import com.example.focustimerapp.feature.timer.RunningTimerCard
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onEditTaskClick: (Long) -> Unit = {},
    onStartTaskClick: (Long) -> Unit = {},
    onCompletedTaskClick: (Long) -> Unit = {},
    onClientsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var showArchived by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val runningSession = uiState.runningSession

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var isCustomDateSelected by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()

    if (uiState.period != PeriodFilter.ALL && isCustomDateSelected) {
        isCustomDateSelected = false
    }

    val activeTasks = uiState.tasks.filter { !it.isArchived }
    val archivedTasks = uiState.tasks.filter { it.isArchived }

    val runningTask =
        activeTasks.find { it.id == runningSession?.taskId }

    val pendingTasks =
        activeTasks.filter { task ->
            !task.isCompleted && task.id != runningSession?.taskId
        }

    val completedTasks =
        activeTasks.filter { it.isCompleted }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DashboardTopBar(
                onClientsClick = onClientsClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SummaryCard(
                    totalEarningsCents = uiState.totalEarningsCents,
                    totalSeconds = uiState.totalSeconds
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PeriodFilterRow(
                        selected = uiState.period,
                        onFilterSelected = viewModel::setPeriod
                    )

                    IconButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date range"
                        )
                    }
                }

                if (showDatePicker) {

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {

                                    val startMillis = dateRangePickerState.selectedStartDateMillis
                                    val endMillis = dateRangePickerState.selectedEndDateMillis

                                    if (startMillis != null && endMillis != null) {

                                        val start = Instant.ofEpochMilli(startMillis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()

                                        val end = Instant.ofEpochMilli(endMillis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()

                                        if (!end.isBefore(start)) {
                                            selectedStartDate = start
                                            selectedEndDate = end
                                            viewModel.setCustomRange(start, end)
                                            isCustomDateSelected = true
                                        }
                                    }

                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DateRangePicker(
                            state = dateRangePickerState
                        )
                    }
                }

                if (
                    selectedStartDate != null &&
                    selectedEndDate != null &&
                    isCustomDateSelected
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$selectedStartDate - $selectedEndDate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = "Show archived tasks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Include inactive tasks in dashboard",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = showArchived,
                        onCheckedChange = { showArchived = it }
                    )
                }
            }

            if (runningSession != null && runningTask != null) {

                item {
                    SectionTitle("Running Task")
                }

                item {
                    RunningTimerCard(
                        task = runningTask,
                        session = runningSession,
                        onClick = {
                            onStartTaskClick(runningTask.id)
                        }
                    )
                }
            }

            item {
                SectionHeaderWithFab(
                    title = "Pending Tasks",
                    onFabClick = onAddTaskClick
                )
            }

            items(
                items = pendingTasks,
                key = { it.id }
            ) { task ->

                ActiveTaskCard(
                    task = task,
                    isAnotherTaskRunning = runningSession != null,
                    onEditClick = onEditTaskClick,
                    onPlayClick = {
                        onStartTaskClick(task.id)
                    }
                )
            }

            if (completedTasks.isNotEmpty()) {

                item {
                    SectionTitle("Completed Tasks")
                }

                items(
                    items = completedTasks,
                    key = { it.id }
                ) { task ->
                    CompletedTaskCard(
                        task = task,
                        onDetailsClick = onCompletedTaskClick,
                        onEditClick = onEditTaskClick
                    )
                }
            }

            if (showArchived && archivedTasks.isNotEmpty()) {

                item {
                    SectionTitle("Archived Tasks")
                }

                items(
                    items = archivedTasks,
                    key = { it.id }
                ) { task ->
                    CompletedTaskCard(
                        task = task,
                        onDetailsClick = onCompletedTaskClick,
                        onEditClick = onEditTaskClick
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    onClientsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "FocusTimerApp",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = onClientsClick) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "Clients"
                )
            }

            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
private fun PeriodFilterRow(
    selected: PeriodFilter,
    onFilterSelected: (PeriodFilter) -> Unit
) {

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        filter.name
                            .lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                }
            )
        }
    }
}

@Composable
private fun SummaryCard(
    totalEarningsCents: Long,
    totalSeconds: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = "Total Earnings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatCurrencyFromCents(totalEarningsCents),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "Total Hours",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatSecondsToHours(totalSeconds),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
private fun ActiveTaskCard(
    task: Task,
    isAnotherTaskRunning: Boolean,
    onEditClick: (Long) -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    RateChip(task.hourlyRateCents)

                    task.scheduledStartDate?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) }
                        )
                    }

                    IconButton(
                        onClick = { onEditClick(task.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit task"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            FilledTonalIconButton(
                onClick = onPlayClick,
                enabled = !isAnotherTaskRunning,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "▶",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun CompletedTaskCard(
    task: Task,
    onDetailsClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                RateChip(task.hourlyRateCents)
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = formatCurrencyFromCents(task.totalEarnedCents),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatSecondsToHours(task.totalSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    IconButton(
                        onClick = { onEditClick(task.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit completed task"
                        )
                    }

                    TextButton(
                        onClick = { onDetailsClick(task.id) }
                    ) {
                        Text("Details")
                    }
                }
            }
        }
    }
}

@Composable
private fun RateChip(hourlyRateCents: Long) {
    AssistChip(
        onClick = {},
        label = { Text("${formatCurrencyFromCents(hourlyRateCents)}/h") }
    )
}

@Composable
private fun SectionHeaderWithFab(
    title: String,
    onFabClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        FloatingActionButton(
            onClick = onFabClick,
            shape = CircleShape,
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add task"
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall
    )
}

private fun formatCurrencyFromCents(cents: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(cents / 100.0)
}

private fun formatSecondsToHours(seconds: Long): String {
    val hours = seconds / 3600.0
    return String.format(Locale.US, "%.1fh", hours)
}