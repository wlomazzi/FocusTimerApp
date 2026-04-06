package com.example.focustimerapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.focustimerapp.core.database.FocusTimerDatabase
import com.example.focustimerapp.core.database.dao.ClientDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.focustimerapp.core.database.dao.TaskDao
import com.example.focustimerapp.core.database.dao.WorkSessionDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FocusTimerDatabase {
        return Room.databaseBuilder(
            context,
            FocusTimerDatabase::class.java,
            "focustimer.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideClientDao(
        database: FocusTimerDatabase
    ): ClientDao {
        return database.clientDao()
    }
    @Provides
    fun provideTaskDao(
        database: FocusTimerDatabase
    ): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideWorkSessionDao(
        database: FocusTimerDatabase
    ): WorkSessionDao {
        return database.workSessionDao()
    }

}