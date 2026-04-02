package com.example.focustimerapp.core.database

import androidx.room.TypeConverter
import com.example.focustimerapp.core.database.entity.SessionStatus
import com.example.focustimerapp.core.database.entity.TaskStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

class DatabaseConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromSessionStatus(status: SessionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSessionStatus(value: String): SessionStatus {
        return SessionStatus.valueOf(value)
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }
}