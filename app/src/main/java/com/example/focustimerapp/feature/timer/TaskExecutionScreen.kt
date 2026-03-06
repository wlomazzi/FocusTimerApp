package com.example.focustimerapp.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focustimerapp.core.database.entity.WorkSession
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskExecutionScreen(
    taskId: Long,
    viewModel: TaskExecutionViewModel,
    onBackClick: () -> Unit
) {

    val state by viewModel.uiState

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val task = state.task ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task.description) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(timerGradient())
                .padding(paddingValues),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                TimerContent(
                    state = state,
                    hourlyRateCents = task.hourlyRateCents
                )
            }

            item {
                TimerControls(
                    state = state,
                    isTaskCompleted = task.isCompleted,
                    onPause = { viewModel.pauseTask() },
                    onResume = { viewModel.startTask() },
                    onFinish = { viewModel.finishTask() }
                )
            }

            if (state.sessions.isNotEmpty()) {

                item {
                    Text(
                        text = "Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }

                items(
                    items = state.sessions.sortedByDescending { it.startedAt },
                    key = { it.id }
                ) { session ->

                    val index = state.sessions.indexOf(session) + 1

                    SessionCard(
                        index = index,
                        session = session
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

/* --------------------------------------------------- */
/* ------------------ TIMER CONTENT ------------------ */
/* --------------------------------------------------- */

@Composable
private fun TimerContent(
    state: TaskExecutionUiState,
    hourlyRateCents: Long
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = formatTime(state.totalSeconds),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        EarningsCard(state.earnedCents)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hourly Rate: ${formatCurrency(hourlyRateCents)}/h",
            color = Color.White.copy(alpha = 0.8f)
        )

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = Color.Red)
        }
    }
}

/* --------------------------------------------------- */
/* ------------------ EARNINGS CARD ------------------ */
/* --------------------------------------------------- */

@Composable
private fun EarningsCard(earnedCents: Long) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Current Task Value",
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatCurrency(earnedCents),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* --------------------------------------------------- */
/* ------------------ TIMER CONTROLS ----------------- */
/* --------------------------------------------------- */

@Composable
private fun TimerControls(
    state: TaskExecutionUiState,
    isTaskCompleted: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (state.isRunning && !isTaskCompleted) {

            IconButton(
                onClick = onPause,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Pause, contentDescription = "Pause")
            }

        } else {

            IconButton(
                onClick = onResume,
                enabled = !isTaskCompleted,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFD1C4E9), CircleShape)
            ) {
                Text("▶")
            }
        }

        IconButton(
            onClick = onFinish,
            enabled = state.isRunning && !isTaskCompleted,
            modifier = Modifier
                .size(80.dp)
                .background(
                    if (state.isRunning && !isTaskCompleted)
                        Color(0xFF4CAF50)
                    else
                        Color(0xFF4CAF50).copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Finish",
                tint = Color.White
            )
        }
    }
}

/* --------------------------------------------------- */
/* ------------------ SESSION CARD ------------------- */
/* --------------------------------------------------- */

@Composable
private fun SessionCard(
    index: Int,
    session: WorkSession
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Session #$index",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${formatTime(session.durationSeconds.toLong())}",
                color = Color.White
            )

            Text(
                text = "Earned: ${formatCurrency(session.earnedCents)}",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Started: ${session.startedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )

            session.endedAt?.let {
                Text(
                    text = "Ended: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/* --------------------------------------------------- */
/* ------------------ UTILITIES ---------------------- */
/* --------------------------------------------------- */

private fun timerGradient(): Brush =
    Brush.verticalGradient(
        listOf(
            Color(0xFF5E4B8B),
            Color(0xFF4B3C7A)
        )
    )

fun formatTime(seconds: Long): String {
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, secs)
}

fun formatCurrency(cents: Long): String =
    "$" + "%.2f".format(cents / 100.0)