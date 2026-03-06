package com.example.focustimerapp.core.domain.model

/**
 * Domain model representing a work task.
 *
 * Monetary values are stored in cents.
 * Dates are represented as ISO strings.
 */
data class Task(
    val id: Long = 0,

    /** Identifier of the client associated with the task. */
    val clientId: Long,

    /** Main description of the task. */
    val description: String,

    /** Hourly rate stored in cents. */
    val hourlyRateCents: Long,

    /** Scheduled start date in ISO format (yyyy-MM-dd). */
    val scheduledStartDate: String? = null,

    /** Indicates whether the task is completed. */
    val isCompleted: Boolean = false,

    /** DateTime when the task was completed. */
    val completedAt: String? = null,

    /** Total tracked duration in seconds (aggregated from work sessions). */
    val totalSeconds: Int = 0,

    /** Total earned amount in cents (aggregated from work sessions). */
    val totalEarnedCents: Long = 0,

    /** Creation timestamp in ISO datetime format. */
    val createdAt: String,

    /** Last update timestamp in ISO datetime format. */
    val updatedAt: String
)