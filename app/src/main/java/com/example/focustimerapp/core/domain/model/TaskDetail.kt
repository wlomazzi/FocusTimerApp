package com.example.focustimerapp.core.domain.model

import com.example.focustimerapp.core.database.entity.WorkSession

data class TaskDetail(
    val task: Task,
    val sessions: List<WorkSession>,
    val totalEarnedCents: Long,
    val totalDurationSeconds: Int
)