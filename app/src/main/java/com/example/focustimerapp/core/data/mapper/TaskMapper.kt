package com.example.focustimerapp.core.data.mapper

import com.example.focustimerapp.core.database.entity.TaskEntity
import com.example.focustimerapp.core.domain.model.Task

/**
 * Converts TaskEntity (database layer) to Task (domain layer).
 *
 * Ensures proper mapping between persistence and business layers.
 */
fun TaskEntity.toDomain(
    totalEarnedCents: Long = 0,
    totalSeconds: Int = 0
): Task {

    return Task(
        id = id,
        clientId = clientId,
        description = description,
        hourlyRateCents = hourlyRateCents,
        isCompleted = isCompleted,
        scheduledStartDate = scheduledStartDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt,
        totalEarnedCents = totalEarnedCents,
        totalSeconds = totalSeconds
    )
}

/**
 * Converts Task (domain layer) to TaskEntity (database layer).
 *
 * Maintains separation of concerns between domain and persistence models.
 */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        clientId = clientId,
        description = description,
        hourlyRateCents = hourlyRateCents,
        scheduledStartDate = scheduledStartDate,
        isCompleted = isCompleted,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}