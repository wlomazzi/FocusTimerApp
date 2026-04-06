package com.example.focustimerapp.core.data.mapper

import com.example.focustimerapp.core.database.entity.ClientEntity
import com.example.focustimerapp.core.domain.model.Client

fun ClientEntity.toDomain(): Client =
    Client(
        id = clientId,
        name = name,
        email = email,
<<<<<<< HEAD
        companyName = companyName,
        isActive = isActive
=======
        companyName = companyName
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    )

fun Client.toEntity(): ClientEntity =
    ClientEntity(
        clientId = id,
        name = name,
        email = email,
<<<<<<< HEAD
        companyName = companyName,
        isActive = isActive
=======
        companyName = companyName
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    )