package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.ClientDao
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Concrete implementation of ClientRepository.
 *
 * Responsible for:
 * - Mapping between Entity and Domain models
 * - Delegating database operations to ClientDao
 * - Keeping domain layer independent from Room
 */
class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
) : ClientRepository {

    /**
     * Observes all clients from database.
     * Converts ClientEntity list into Domain Client list.
     */
    override fun observeClients(): Flow<List<Client>> {
        return clientDao
            .observeClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

    /**
     * Inserts a new client into the database.
     * Returns the generated clientId.
     */
    override suspend fun addClient(client: Client): Long {
        return clientDao.insert(client.toEntity())
    }

    /**
     * Updates an existing client in the database.
     */
    override suspend fun updateClient(client: Client) {
        clientDao.update(client.toEntity())
    }

    /**
     * Retrieves a client by its id.
     * Returns null if not found.
     */
    override suspend fun getClientById(id: Long): Client? {
        return clientDao.getById(id)?.toDomain()
    }

    /**
     * Checks whether a given email is already used by another client.
     * If ignoreClientId is provided, it will exclude that client from validation.
     */
    override suspend fun isEmailTaken(
        email: String,
        ignoreClientId: Long?
    ): Boolean {
        val existing = clientDao.getByEmail(email) ?: return false
        return ignoreClientId == null || existing.clientId != ignoreClientId
    }
}