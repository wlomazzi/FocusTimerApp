package com.example.focustimerapp.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val clientId: Long = 0,
    val name: String,
    val email: String,
    val companyName: String?
)

