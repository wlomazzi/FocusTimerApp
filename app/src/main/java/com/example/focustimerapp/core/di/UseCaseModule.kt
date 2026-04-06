package com.example.focustimerapp.core.di

import com.example.focustimerapp.core.domain.repository.ClientRepository
import com.example.focustimerapp.core.domain.repository.TaskRepository
import com.example.focustimerapp.core.domain.repository.WorkSessionRepository

import com.example.focustimerapp.core.domain.usecase.AddClientUseCase
import com.example.focustimerapp.core.domain.usecase.ObserveClientsUseCase
import com.example.focustimerapp.core.domain.usecase.ObserveAllTasksUseCase
import com.example.focustimerapp.core.domain.usecase.GetTaskDetailUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent



@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ==============================
    // CLIENT USE CASES
    // ==============================

    @Provides
    fun provideObserveClientsUseCase(
        repository: ClientRepository
    ): ObserveClientsUseCase {
        return ObserveClientsUseCase(repository)
    }

    @Provides
    fun provideAddClientUseCase(
        repository: ClientRepository
    ): AddClientUseCase {
        return AddClientUseCase(repository)
    }

    // ==============================
    // TASK USE CASES
    // ==============================

    @Provides
    fun provideObserveAllTasksUseCase(
        repository: TaskRepository
    ): ObserveAllTasksUseCase {
        return ObserveAllTasksUseCase(repository)
    }

    @Provides
    fun provideGetTaskDetailUseCase(
        taskRepository: TaskRepository,
        workSessionRepository: WorkSessionRepository
    ): GetTaskDetailUseCase {
        return GetTaskDetailUseCase(
            taskRepository,
            workSessionRepository
        )
    }
}