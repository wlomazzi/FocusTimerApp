package com.example.focustimerapp.core.domain.model

data class Client(
    val id: Long = 0,
    val name: String,
    val email: String,
    val companyName: String?,
    val isActive: Boolean = true
)