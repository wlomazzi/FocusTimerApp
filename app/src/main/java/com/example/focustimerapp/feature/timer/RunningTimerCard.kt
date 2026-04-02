package com.example.focustimerapp.feature.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focustimerapp.core.database.entity.SessionStatus
import com.example.focustimerapp.core.database.entity.WorkSession
import com.example.focustimerapp.core.domain.model.Task

@Composable
fun RunningTimerCard(
    task: Task,
    session: WorkSession,
    onClick: () -> Unit
) {

    /*
     Determine current session state
     */
    //val isRunning = session.status == SessionStatus.RUNNING
    val isRunning = session.endedAt == null

    //val statusText =
    //    if (isRunning) "RUNNING" else "PAUSED"
    val statusText =
        if (session.endedAt == null) "Running"
        else "Completed"

    val statusColor =
        if (isRunning)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.tertiary

    /*
     Adjust card color based on session state
     */
    val cardColor =
        if (isRunning)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else
            MaterialTheme.colorScheme.secondaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        shape = MaterialTheme.shapes.small,

        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isRunning) 0.dp else 4.dp
        ),

        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            /*
             Status badge with visual differentiation between states
             Running uses subtle filled background
             Paused uses outlined style
             */
            if (isRunning) {

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }

            } else {

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            /*
             Task title remains unchanged to preserve layout behavior
             */
            Text(
                text = task.description,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}