package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.ClientDao
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

<<<<<<< HEAD
=======
/**
 * Concrete implementation of ClientRepository.
 *
 * Responsible for:
 * - Mapping between Entity and Domain models
 * - Delegating database operations to ClientDao
 * - Keeping domain layer independent from Room
 */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
) : ClientRepository {

<<<<<<< HEAD
=======
    /**
     * Observes all clients from database.
     * Converts ClientEntity list into Domain Client list.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    override fun observeClients(): Flow<List<Client>> {
        return clientDao
            .observeClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

<<<<<<< HEAD
    override fun observeActiveClients(): Flow<List<Client>> {
        return clientDao
            .observeActiveClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

=======
    /**
     * Inserts a new client into the database.
     * Returns the generated clientId.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    override suspend fun addClient(client: Client): Long {
        return clientDao.insert(client.toEntity())
    }

<<<<<<< HEAD
=======
    /**
     * Updates an existing client in the database.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    override suspend fun updateClient(client: Client) {
        clientDao.update(client.toEntity())
    }

<<<<<<< HEAD
    override suspend fun updateClientStatus(clientId: Long, isActive: Boolean) {
        clientDao.updateClientStatus(clientId, isActive)
    }

=======
    /**
     * Retrieves a client by its id.
     * Returns null if not found.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    override suspend fun getClientById(id: Long): Client? {
        return clientDao.getById(id)?.toDomain()
    }

<<<<<<< HEAD
=======
    /**
     * Checks whether a given email is already used by another client.
     * If ignoreClientId is provided, it will exclude that client from validation.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    override suspend fun isEmailTaken(
        email: String,
        ignoreClientId: Long?
    ): Boolean {
        val existing = clientDao.getByEmail(email) ?: return false
        return ignoreClientId == null || existing.clientId != ignoreClientId
    }
}