package com.example.focustimerapp.core.database.model

/*
 * Model used to receive aggregated values from WorkSessionDao
 * This is NOT a database entity
 */
data class TaskTotals(
    val totalSeconds: Long,
    val totalEarnedCents: Long
)