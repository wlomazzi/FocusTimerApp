package com.example.focustimerapp.feature.task

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCreateTaskClick: () -> Unit
) {

    val clients by viewModel.clients.collectAsStateWithLifecycle()

    var taskDescription by remember { mutableStateOf("") }
    var selectedClientName by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableLongStateOf(0L) }
    var hourlyRate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // ERROR STATES
    var descriptionError by remember { mutableStateOf(false) }
    var clientError by remember { mutableStateOf(false) }
    var hourlyRateError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Task",
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // DESCRIPTION
            OutlinedTextField(
                value = taskDescription,
                onValueChange = {
                    taskDescription = it
                    descriptionError = false
                },
                label = { Text("Task Description *") },
                placeholder = { Text("Enter task description") },
                isError = descriptionError,
                supportingText = {
                    if (descriptionError) Text("Required field")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // CLIENT
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedClientName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assign to Client *") },
                    placeholder = { Text("Select a client") },
                    isError = clientError,
                    supportingText = {
                        if (clientError) Text("Required field")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    clients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = {
                                selectedClientName = client.name
                                selectedClientId = client.id
                                clientError = false
                                expanded = false
                            }
                        )
                    }
                }
            }

            // HOURLY RATE
            OutlinedTextField(
                value = hourlyRate,
                onValueChange = {
                    hourlyRate = it
                    hourlyRateError = false
                },
                label = { Text("Hourly Rate ($) *") },
                placeholder = { Text("0.00") },
                isError = hourlyRateError,
                supportingText = {
                    if (hourlyRateError) Text("Required field")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            val context = LocalContext.current
            val calendar = Calendar.getInstance()
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            fun openDatePicker() {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        startDate = dateFormatter.format(calendar.time)
                        startDateError = false
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // DATE
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                label = { Text("Scheduled Start Date *") },
                placeholder = { Text("dd/mm/yyyy") },
                isError = startDateError,
                supportingText = {
                    if (startDateError) Text("Required field")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Open calendar")
                    }
                },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    // VALIDATION
                    descriptionError = taskDescription.isBlank()
                    clientError = selectedClientId == 0L
                    hourlyRateError = hourlyRate.isBlank()
                    startDateError = startDate.isBlank()

                    val isValid =
                        !descriptionError &&
                                !clientError &&
                                !hourlyRateError &&
                                !startDateError

                    if (isValid) {

                        val isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .parse(startDate)!!
                            )

                        viewModel.createTask(
                            clientId = selectedClientId,
                            description = taskDescription,
                            hourlyRateCents = (hourlyRate.toDouble() * 100).toLong(),
                            scheduledStartDate = isoDate
                        )

                        onCreateTaskClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("CREATE TASK")
            }
        }
    }
}