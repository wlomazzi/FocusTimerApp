package com.example.focustimerapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.focustimerapp.core.database.dao.ClientDao
import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.dao.WorkSessionDao
import com.example.focustimerapp.core.database.entity.ClientEntity
import com.example.focustimerapp.core.database.entity.TaskEntity
import com.example.focustimerapp.core.database.entity.WorkSession

/*
 * Main Room database for FocusTimerApp.
 * Responsible for providing DAOs and registering entities.
 */
@Database(
    entities = [
        ClientEntity::class,
        TaskEntity::class,
        WorkSession::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class FocusTimerDatabase : RoomDatabase() {

    /*
     * Provides access to client operations.
     */
    abstract fun clientDao(): ClientDao

    /*
     * Provides access to task operations.
     */
    abstract fun taskDao(): TaskDao

    /*
     * Provides access to work session operations.
     */
    abstract fun workSessionDao(): WorkSessionDao
}