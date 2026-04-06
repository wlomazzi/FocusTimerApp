package com.example.focustimerapp.core.domain.repository

import com.example.focustimerapp.core.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun observeClients(): Flow<List<Client>>

<<<<<<< HEAD
    fun observeActiveClients(): Flow<List<Client>>

=======
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    suspend fun addClient(client: Client): Long

    suspend fun updateClient(client: Client)

<<<<<<< HEAD
    suspend fun updateClientStatus(clientId: Long, isActive: Boolean)

    suspend fun getClientById(id: Long): Client?

    suspend fun isEmailTaken(
        email: String,
        ignoreClientId: Long? = null
    ): Boolean
=======
    suspend fun getClientById(id: Long): Client?

    suspend fun isEmailTaken(email: String, ignoreClientId: Long? = null): Boolean
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
}