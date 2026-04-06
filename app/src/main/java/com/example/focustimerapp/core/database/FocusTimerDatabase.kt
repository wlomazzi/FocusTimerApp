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
<<<<<<< HEAD
    version = 3,
=======
    version = 1,
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
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