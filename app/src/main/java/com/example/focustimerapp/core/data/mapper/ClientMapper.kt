package com.example.focustimerapp.core.data.mapper

import com.example.focustimerapp.core.database.entity.ClientEntity
import com.example.focustimerapp.core.domain.model.Client

fun ClientEntity.toDomain(): Client =
    Client(
        id = clientId,
        name = name,
        email = email,
        companyName = companyName
    )

fun Client.toEntity(): ClientEntity =
    ClientEntity(
        clientId = id,
        name = name,
        email = email,
        companyName = companyName
    )