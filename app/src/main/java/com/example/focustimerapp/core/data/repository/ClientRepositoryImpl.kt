package com.example.focustimerapp.core.data.repository

import com.example.focustimerapp.core.data.mapper.toDomain
import com.example.focustimerapp.core.data.mapper.toEntity
import com.example.focustimerapp.core.database.dao.ClientDao
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
) : ClientRepository {

    override fun observeClients(): Flow<List<Client>> {
        return clientDao
            .observeClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observeActiveClients(): Flow<List<Client>> {
        return clientDao
            .observeActiveClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun addClient(client: Client): Long {
        return clientDao.insert(client.toEntity())
    }

    override suspend fun updateClient(client: Client) {
        clientDao.update(client.toEntity())
    }

    override suspend fun updateClientStatus(clientId: Long, isActive: Boolean) {
        clientDao.updateClientStatus(clientId, isActive)
    }

    override suspend fun getClientById(id: Long): Client? {
        return clientDao.getById(id)?.toDomain()
    }

    override suspend fun isEmailTaken(
        email: String,
        ignoreClientId: Long?
    ): Boolean {
        val existing = clientDao.getByEmail(email) ?: return false
        return ignoreClientId == null || existing.clientId != ignoreClientId
    }
}