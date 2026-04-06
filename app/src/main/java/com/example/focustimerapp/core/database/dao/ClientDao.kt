package com.example.focustimerapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.focustimerapp.core.database.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {


    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun observeClients(): Flow<List<ClientEntity>>


    @Query("SELECT * FROM clients WHERE isActive = 1 ORDER BY name ASC")
    fun observeActiveClients(): Flow<List<ClientEntity>>


    @Query("SELECT * FROM clients WHERE clientId = :id LIMIT 1")
    suspend fun getById(id: Long): ClientEntity?


    @Query("SELECT * FROM clients WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): ClientEntity?


    @Insert
    suspend fun insert(client: ClientEntity): Long


    @Update
    suspend fun update(client: ClientEntity)


    @Query("UPDATE clients SET isActive = :isActive WHERE clientId = :clientId")
    suspend fun updateClientStatus(clientId: Long, isActive: Boolean)
}