package com.example.focustimerapp.feature.task

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focustimerapp.core.database.entity.WorkSession
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
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val taskState by viewModel.task.collectAsState()
    val clients by viewModel.clients.collectAsState(initial = emptyList())
    val sessions by viewModel.sessions.collectAsState(initial = emptyList<WorkSession>())

    /*
     * Loads task data when the screen is opened.
     */
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val task = taskState

    /*
     * Initializes form state based on the loaded task.
     */
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

    /*
     * Opens the native Android date picker.
     */
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

            /*
             * Task description field.
             */
            Text("Task Description *")

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            /*
             * Client selection field.
             */
            Text("Assign to Client *")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedClientName,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
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
            }

            /*
             * Hourly rate field.
             */
            Text("Hourly Rate ($) *")

            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            /*
             * Scheduled start date field.
             */
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

            /*
             * Completion details section.
             */
            if (sessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Completion Details",
                    style = MaterialTheme.typography.titleMedium
                )

                sessions.forEachIndexed { index, session ->
                    SessionCard(
                        index = index,
                        session = session,
                        onDeleteClick = {
                            viewModel.deleteSession(session.id)
                        }
                    )
                }
            }

            /*
             * Save button updates the task.
             */
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
private fun SessionCard(
    index: Int,
    session: WorkSession,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session #${index + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatDuration(session.durationSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete session"
                    )
                }
            }

            OutlinedTextField(
                value = formatDateTime(session.startedAt),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Start Time") },
                readOnly = true,
                shape = MaterialTheme.shapes.small
            )

            OutlinedTextField(
                value = formatDateTime(session.endedAt),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("End Time") },
                readOnly = true,
                shape = MaterialTheme.shapes.small
            )
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}m ${seconds}s"
}

private fun formatDateTime(value: LocalDateTime?): String {
    if (value == null) return ""

    return try {
        value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        value.toString()
    }
}