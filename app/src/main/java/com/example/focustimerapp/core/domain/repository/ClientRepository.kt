package com.example.focustimerapp.core.domain.repository

import com.example.focustimerapp.core.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun observeClients(): Flow<List<Client>>

    suspend fun addClient(client: Client): Long

    suspend fun updateClient(client: Client)

    suspend fun getClientById(id: Long): Client?

    suspend fun isEmailTaken(email: String, ignoreClientId: Long? = null): Boolean
}