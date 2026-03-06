package com.example.focustimerapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * TaskEntity
 *
 * Represents a work task associated with a client.
 *
 * This entity mirrors the structure of the "tasks" table in the database.
 * Monetary values are stored in cents to prevent floating point precision issues.
 * Date values are stored as ISO strings (yyyy-MM-dd or ISO datetime format).
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("client_id")]
)
data class TaskEntity(

    /**
     * Primary key of the task.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /**
     * Identifier of the client associated with this task.
     */
    @ColumnInfo(name = "client_id")
    val clientId: Long,

    /**
     * Main description of the task.
     */
    @ColumnInfo(name = "description")
    val description: String,

    /**
     * Hourly rate stored in cents.
     */
    @ColumnInfo(name = "hourly_rate_cents")
    val hourlyRateCents: Long,

    /**
     * Scheduled start date in ISO format (yyyy-MM-dd).
     */
    @ColumnInfo(name = "scheduled_start_date")
    val scheduledStartDate: String?,

    /**
     * Indicates whether the task is completed.
     */
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    /**
     * DateTime when the task was completed.
     */
    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,

    /**
     * Total tracked work duration in seconds.
     */
    @ColumnInfo(name = "total_seconds")
    val totalSeconds: Int = 0,

    /**
     * Total earned amount in cents.
     */
    @ColumnInfo(name = "total_earned_cents")
    val totalEarnedCents: Long = 0,

    /**
     * Record creation timestamp (ISO datetime).
     */
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    /**
     * Last update timestamp (ISO datetime).
     */
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)