package com.example.focustimerapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "client_id")
    val clientId: Long,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "hourly_rate_cents")
    val hourlyRateCents: Long,

    @ColumnInfo(name = "scheduled_start_date")
    val scheduledStartDate: String?,

    /**
     * NEW FIELD → source of truth
     */
    @ColumnInfo(name = "status")
    val status: TaskStatus = TaskStatus.PENDING,

    /**
     * OLD FIELD (temporary - will be removed later)
     */
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,

    @ColumnInfo(name = "total_seconds")
    val totalSeconds: Int = 0,

    @ColumnInfo(name = "total_earned_cents")
    val totalEarnedCents: Long = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)