package com.example.focustimerapp.feature.task

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    var expanded by remember { mutableStateOf(false) }

    val selectedClientName = clients
        .firstOrNull { it.id == selectedClientId }
        ?.name ?: ""

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
    val formatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    fun openDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                scheduledDate = formatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
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
                .fillMaxSize(),
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

            Spacer(modifier = Modifier.height(8.dp))

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