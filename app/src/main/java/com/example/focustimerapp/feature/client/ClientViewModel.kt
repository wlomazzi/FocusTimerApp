package com.example.focustimerapp.feature.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.domain.model.Client
import com.example.focustimerapp.core.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

<<<<<<< HEAD
=======
/**
 * ViewModel responsible for client management.
 * Handles list observation, form state, validation, and persistence.
 */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

<<<<<<< HEAD
=======
    /*
     * Clients list stream.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    val clients: StateFlow<List<Client>> =
        clientRepository.observeClients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

<<<<<<< HEAD
    private val _formState = MutableStateFlow(ClientFormState())
    val formState: StateFlow<ClientFormState> = _formState.asStateFlow()

=======
    /*
     * UI state for form operations (Add/Edit).
     */
    private val _formState = MutableStateFlow(ClientFormState())
    val formState: StateFlow<ClientFormState> = _formState.asStateFlow()

    /*
     * Loads a client for editing.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
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

<<<<<<< HEAD
=======
    /*
     * Updates local form state fields.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    fun updateName(value: String) {
        _formState.update { it.copy(name = value) }
    }

    fun updateEmail(value: String) {
        _formState.update { it.copy(email = value) }
    }

    fun updateCompany(value: String) {
        _formState.update { it.copy(companyName = value) }
    }

<<<<<<< HEAD
=======
    /*
     * Saves client (Add or Edit).
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
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

<<<<<<< HEAD
            val existingClient = if (state.id != 0L) {
                clientRepository.getClientById(state.id)
            } else null

=======
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
            val client = Client(
                id = state.id,
                name = state.name.trim(),
                email = normalizedEmail,
<<<<<<< HEAD
                companyName = state.companyName.trim().ifBlank { null },
                isActive = existingClient?.isActive ?: true
=======
                companyName = state.companyName.trim().ifBlank { null }
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
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

<<<<<<< HEAD
    fun updateClientStatus(clientId: Long, isActive: Boolean) {
        viewModelScope.launch {
            clientRepository.updateClientStatus(clientId, isActive)
        }
    }

=======
    /*
     * Clears success flag after navigation.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    fun clearSuccess() {
        _formState.update { it.copy(isSuccess = false) }
    }

<<<<<<< HEAD
=======
    /*
     * Clears error after Snackbar is shown.
     */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
    fun clearError() {
        _formState.update { it.copy(errorMessage = null) }
    }
}

<<<<<<< HEAD
=======
/*
 * UI state data holder for client form.
 */
>>>>>>> e73227e05336b7ff4a19e96e56a7da79ff7f58fe
data class ClientFormState(
    val id: Long = 0L,
    val name: String = "",
    val email: String = "",
    val companyName: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)