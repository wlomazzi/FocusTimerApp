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

    // Collects clients from the database and keeps UI updated automatically.
    val clients by viewModel.clients.collectAsStateWithLifecycle()

    var taskDescription by remember { mutableStateOf("") }
    var selectedClientName by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableLongStateOf(0L) }
    var hourlyRate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
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

            // Task description input field.
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Task Description *") },
                placeholder = { Text("Enter task description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // Client selection dropdown populated from database.
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

                    // Iterates through real clients coming from Room database.
                    clients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = {
                                selectedClientName = client.name
                                selectedClientId = client.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Hourly rate numeric input field.
            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                label = { Text("Hourly Rate ($) *") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            val context = LocalContext.current
            val calendar = Calendar.getInstance()
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Opens native Android date picker dialog.
            fun openDatePicker() {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        startDate = dateFormatter.format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // Scheduled start date field with calendar picker.
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                label = { Text("Scheduled Start Date *") },
                placeholder = { Text("dd/mm/yyyy") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Open calendar"
                        )
                    }
                },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Creates a new task only if required fields are filled.
            Button(
                onClick = {

                    if (
                        taskDescription.isNotBlank() &&
                        hourlyRate.isNotBlank() &&
                        selectedClientId != 0L
                    ) {

                        // Converts dd/MM/yyyy to ISO yyyy-MM-dd
                        val isoDate = if (startDate.isNotBlank()) {
                            val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .parse(startDate)

                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(parsedDate!!)
                        } else null

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