package com.example.focustimerapp.core.database.model

import androidx.room.Embedded
import com.example.focustimerapp.core.database.entity.TaskEntity

data class TaskWithStats(

    @Embedded
    val task: TaskEntity,

    val totalEarnedCents: Long,

    val totalSeconds: Int
)