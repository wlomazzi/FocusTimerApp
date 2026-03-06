package com.example.focustimerapp.core.domain.usecase

import com.example.focustimerapp.core.domain.repository.ClientRepository

class ObserveClientsUseCase(
    private val repo: ClientRepository
) {
    operator fun invoke() = repo.observeClients()
}