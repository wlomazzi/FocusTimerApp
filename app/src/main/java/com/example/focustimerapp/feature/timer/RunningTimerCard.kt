package com.example.focustimerapp.feature.timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

    val statusText =
        if (session.status == SessionStatus.RUNNING)
            "RUNNING"
        else
            "PAUSED"

    val statusColor =
        if (session.status == SessionStatus.RUNNING)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.tertiary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.description,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}