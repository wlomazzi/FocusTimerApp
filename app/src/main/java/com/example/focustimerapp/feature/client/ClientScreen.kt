package com.example.focustimerapp.feature.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focustimerapp.core.domain.model.Client
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(
    viewModel: ClientListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddClientClick: () -> Unit,
    onClientClick: (Long) -> Unit
) {

    val clients by viewModel.clients.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }

    val normalizedQuery = query.trim().lowercase(Locale.getDefault())

    val filteredClients = remember(clients, normalizedQuery) {
        if (normalizedQuery.isBlank()) {
            clients
        } else {
            clients.filter { client ->
                val name = client.name.lowercase(Locale.getDefault())
                val email = client.email.lowercase(Locale.getDefault())
                val company = (client.companyName ?: "").lowercase(Locale.getDefault())

                name.contains(normalizedQuery) ||
                        email.contains(normalizedQuery) ||
                        company.contains(normalizedQuery)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Clients",
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        /*
                         Show total number of clients as a badge
                         */
                        if (clients.isNotEmpty()) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = clients.size.toString(),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },

                /*
                 Apply primary color to match Dashboard
                 */
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),

                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {
                    /*
                     Use circular add button for better visual consistency
                     */
                    FloatingActionButton(
                        onClick = onAddClientClick,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp),
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add client"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search clients") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )

            Text(
                text = "${filteredClients.size} results",
                style = MaterialTheme.typography.bodySmall
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredClients,
                    key = { it.id }
                ) { client ->
                    ClientItem(
                        client = client,
                        onClick = { onClientClick(client.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientItem(
    client: Client,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = client.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = client.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            client.companyName?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}