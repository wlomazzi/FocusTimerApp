package com.example.focustimerapp.feature.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for client management.
 * Handles list observation, form state, validation, and persistence.
 */
@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    /*
     * Clients list stream.
     */
    val clients: StateFlow<List<Client>> =
        clientRepository.observeClients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /*
     * UI state for form operations (Add/Edit).
     */
    private val _formState = MutableStateFlow(ClientFormState())
    val formState: StateFlow<ClientFormState> = _formState.asStateFlow()

    /*
     * Loads a client for editing.
     */
    fun loadClient(clientId: Long) {
        if (clientId == 0L) return

        viewModelScope.launch {
            val client = clientRepository.getClientById(clientId) ?: return@launch

            _formState.update {
                it.copy(
                    id = client.id,
                    name = client.name,
                    email = client.email,
                    companyName = client.companyName ?: ""
                )
            }
        }
    }

    /*
     * Updates local form state fields.
     */
    fun updateName(value: String) {
        _formState.update { it.copy(name = value) }
    }

    fun updateEmail(value: String) {
        _formState.update { it.copy(email = value) }
    }

    fun updateCompany(value: String) {
        _formState.update { it.copy(companyName = value) }
    }

    /*
     * Saves client (Add or Edit).
     */
    fun saveClient() {
        viewModelScope.launch {

            val state = _formState.value
            val normalizedEmail = state.email.trim()

            if (normalizedEmail.isBlank()) {
                _formState.update { it.copy(errorMessage = "Email cannot be empty.") }
                return@launch
            }

            if (
                clientRepository.isEmailTaken(
                    email = normalizedEmail,
                    ignoreClientId = if (state.id == 0L) null else state.id
                )
            ) {
                _formState.update {
                    it.copy(errorMessage = "This email is already registered.")
                }
                return@launch
            }

            val client = Client(
                id = state.id,
                name = state.name.trim(),
                email = normalizedEmail,
                companyName = state.companyName.trim().ifBlank { null }
            )

            if (state.id == 0L) {
                clientRepository.addClient(client)
            } else {
                clientRepository.updateClient(client)
            }

            _formState.update {
                it.copy(
                    isSuccess = true,
                    errorMessage = null
                )
            }
        }
    }

    /*
     * Clears success flag after navigation.
     */
    fun clearSuccess() {
        _formState.update { it.copy(isSuccess = false) }
    }

    /*
     * Clears error after Snackbar is shown.
     */
    fun clearError() {
        _formState.update { it.copy(errorMessage = null) }
    }
}

/*
 * UI state data holder for client form.
 */
data class ClientFormState(
    val id: Long = 0L,
    val name: String = "",
    val email: String = "",
    val companyName: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)