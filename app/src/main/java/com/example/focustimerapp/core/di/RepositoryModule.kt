package com.example.focustimerapp.core.di

import com.example.focustimerapp.core.data.repository.ClientRepositoryImpl
import com.example.focustimerapp.core.data.repository.TaskRepositoryImpl
import com.example.focustimerapp.core.data.repository.WorkSessionRepositoryImpl
import com.example.focustimerapp.core.domain.repository.ClientRepository
import com.example.focustimerapp.core.domain.repository.TaskRepository
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * Provides bindings between repository interfaces and implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /*
     * Binds ClientRepository implementation.
     */
    @Binds
    @Singleton
    abstract fun bindClientRepository(
        impl: ClientRepositoryImpl
    ): ClientRepository

    /*
     * Binds TaskRepository implementation.
     */
    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    /*
     * Binds WorkSessionRepository implementation.
     */
    @Binds
    @Singleton
    abstract fun bindWorkSessionRepository(
        impl: WorkSessionRepositoryImpl
    ): WorkSessionRepository
}