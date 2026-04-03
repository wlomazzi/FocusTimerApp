package com.example.focustimerapp.feature.task

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: Long,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: EditTaskViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val taskState by viewModel.task.collectAsState()
    val clients by viewModel.clients.collectAsState(initial = emptyList())
    val sessions by viewModel.editableSessions.collectAsState(initial = emptyList())


    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val task = taskState

    var description by remember(task?.id) {
        mutableStateOf(task?.description ?: "")
    }

    var selectedClientId by remember(task?.id) {
        mutableStateOf(task?.clientId ?: 0L)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedClientName = clients
        .firstOrNull { it.id == selectedClientId }
        ?.name
        ?: ""

    var hourlyRate by remember(task?.id) {
        mutableStateOf(
            task?.hourlyRateCents
                ?.let { String.format(Locale.US, "%.2f", it / 100.0) }
                ?: ""
        )
    }

    var scheduledDate by remember(task?.id) {
        mutableStateOf(task?.scheduledStartDate ?: "")
    }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    fun openDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                scheduledDate = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Task",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Task Description *")

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Text("Assign to Client *")
            var expanded by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = selectedClientName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                clients.forEach { client ->
                    DropdownMenuItem(
                        text = { Text(client.name) },
                        onClick = {
                            selectedClientId = client.id
                            expanded = false
                        }
                    )
                }
            }

            Text("Hourly Rate ($) *")

            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Text("Scheduled Start Date *")

            OutlinedTextField(
                value = scheduledDate,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                readOnly = true
            )

            if (sessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Completion Details",
                    style = MaterialTheme.typography.titleMedium
                )
                sessions.forEachIndexed { index, session ->
                    EditableSessionCard(
                        index = index + 1,
                        session = session,
                        hourlyRateCents = task?.hourlyRateCents ?: 0L,
                        onDeleteClick = {
                            viewModel.deleteSession(session.id)
                        },
                        onStartChange = {
                            viewModel.updateSessionStart(session.id, it)
                        },
                        onEndChange = {
                            viewModel.updateSessionEnd(session.id, it)
                        }
                    )
                }
            }

            Button(
                onClick = {
                    task?.let { originalTask ->
                        val updatedRateCents =
                            hourlyRate.toDoubleOrNull()?.times(100)?.toLong()
                                ?: originalTask.hourlyRateCents

                        val updatedTask = originalTask.copy(
                            description = description,
                            clientId = selectedClientId,
                            hourlyRateCents = updatedRateCents,
                            scheduledStartDate = scheduledDate,
                            updatedAt = LocalDateTime.now()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )

                        viewModel.updateTask(updatedTask)
                        viewModel.saveSessionEdits()
                        onSaveClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text("UPDATE TASK")
            }
        }
    }
}

@Composable
private fun EditableSessionCard(
    index: Int,
    session: EditTaskViewModel.EditableSession,
    hourlyRateCents: Long,
    onDeleteClick: () -> Unit,
    onStartChange: (LocalDateTime) -> Unit,
    onEndChange: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session #$index",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete session",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
/*
            Text(
                text = "Duration: ${formatSessionTime(session.original.durationSeconds.toLong())}",
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Earned: ${formatSessionCurrency(session.original.earnedCents)}",
                color = MaterialTheme.colorScheme.onSurface
            )
*/

            val durationSeconds = remember(session.startedAt, session.endedAt) {
                val end = session.endedAt ?: return@remember 0L
                java.time.Duration.between(session.startedAt, end).seconds
            }

            val earnedCents: Long = remember(durationSeconds, hourlyRateCents) {
                (durationSeconds * hourlyRateCents) / 3600
            }

            Text(
                text = "Duration: ${formatSessionTime(durationSeconds)}",
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Earned: ${formatSessionCurrency(earnedCents)}",
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formatSessionDateTime(session.startedAt),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Start Time") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val current = session.startedAt

                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            onStartChange(
                                                LocalDateTime.of(
                                                    year,
                                                    month + 1,
                                                    dayOfMonth,
                                                    hour,
                                                    minute
                                                )
                                            )
                                        },
                                        current.hour,
                                        current.minute,
                                        true
                                    ).show()
                                },
                                current.year,
                                current.monthValue - 1,
                                current.dayOfMonth
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Edit start time"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            session.endedAt?.let { end ->
                OutlinedTextField(
                    value = formatSessionDateTime(end),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("End Time") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                onEndChange(
                                                    LocalDateTime.of(
                                                        year,
                                                        month + 1,
                                                        dayOfMonth,
                                                        hour,
                                                        minute
                                                    )
                                                )
                                            },
                                            end.hour,
                                            end.minute,
                                            true
                                        ).show()
                                    },
                                    end.year,
                                    end.monthValue - 1,
                                    end.dayOfMonth
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Edit end time"
                            )
                        }
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete session")
            },
            text = {
                Text("Are you sure you want to delete this session?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}



private fun formatSessionTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, secs)
}

private fun formatSessionCurrency(cents: Long): String {
    return "$" + "%.2f".format(cents / 100.0)
}

private fun formatSessionDateTime(value: LocalDateTime?): String {
    if (value == null) return ""

    return try {
        value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        value.toString()
    }
}