package com.example.focustimerapp.core.domain.usecase

import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository

class AddClientUseCase(
    private val repo: ClientRepository
) {
    suspend operator fun invoke(client: Client): Long {
        require(client.name.isNotBlank()) { "Client name is required" }
        require(client.email.isNotBlank()) { "Client email is required" }
        return repo.addClient(client)
    }
}