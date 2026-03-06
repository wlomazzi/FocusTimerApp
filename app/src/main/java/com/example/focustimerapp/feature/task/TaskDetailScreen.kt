package com.example.focustimerapp.feature.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        when (state) {

            is TaskDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TaskDetailUiState.Error -> {
                Text(
                    text = (state as TaskDetailUiState.Error).message,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(paddingValues)
                )
            }

            is TaskDetailUiState.Success -> {

                val data = (state as TaskDetailUiState.Success).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {

                    Text(
                        text = data.task.description,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Total Earned: $${data.totalEarnedCents / 100.0}")
                    Text("Total Duration: ${data.totalDurationSeconds}s")

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn {
                        items(data.sessions) { session ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text("Duration: ${session.durationSeconds}s")
                                    Text("Earned: $${session.earnedCents / 100.0}")
                                    Text("Status: ${session.status}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}