package com.example.focustimerapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.focustimerapp.feature.client.*
import com.example.focustimerapp.feature.dashboard.DashboardScreen
import com.example.focustimerapp.feature.task.CreateTaskScreen
import com.example.focustimerapp.feature.task.EditTaskScreen
import com.example.focustimerapp.feature.task.TaskDetailScreen
import com.example.focustimerapp.feature.timer.TaskExecutionScreen
import com.example.focustimerapp.feature.timer.TaskExecutionViewModel
import com.example.focustimerapp.ui.theme.ThemeViewModel
import com.example.focustimerapp.feature.settings.SettingsScreen
import kotlinx.coroutines.launch

/*
 * Centralized navigation routes.
 */
object Routes {

    const val DASHBOARD = "dashboard"
    const val CREATE_TASK = "create_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val TASK_EXECUTION = "task_execution/{taskId}"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val CLIENTS = "clients"
    const val ADD_CLIENT = "clients/add"
    const val EDIT_CLIENT = "clients/edit/{clientId}"
    const val SETTINGS = "settings"
    fun editTask(taskId: Long) = "edit_task/$taskId"
    fun taskExecution(taskId: Long) = "task_execution/$taskId"
    fun taskDetail(taskId: Long) = "task_detail/$taskId"
    fun editClient(clientId: Long) = "clients/edit/$clientId"
}

/*
 * Root navigation host.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        /*
        * Dashboard
        */
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onAddTaskClick = {
                    navController.navigate(Routes.CREATE_TASK)
                },
                onEditTaskClick = { taskId ->
                    navController.navigate(Routes.editTask(taskId))
                },
                onStartTaskClick = { taskId ->
                    navController.navigate(Routes.taskExecution(taskId))
                },
                onClientsClick = {
                    navController.navigate(Routes.CLIENTS)
                },
                onCompletedTaskClick = { taskId ->
                    navController.navigate(Routes.taskDetail(taskId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        /*
        * Create Task
        */
        composable(Routes.CREATE_TASK) {
            CreateTaskScreen(
                onBackClick = { navController.popBackStack() },
                onCreateTaskClick = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        /*
        * Edit Task
        */
        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val taskId =
                backStackEntry.arguments?.getLong("taskId")
                    ?: return@composable

            EditTaskScreen(
                taskId = taskId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        /*
        * Task Execution
        */
        composable(
            route = Routes.TASK_EXECUTION,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
                    ?: return@composable
            val viewModel: TaskExecutionViewModel = hiltViewModel()
            TaskExecutionScreen(
                taskId = taskId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        /*
        * Task Detail
        */
        composable(
            route = Routes.TASK_DETAIL,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getLong("taskId")
                ?: return@composable
            TaskDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        /*
        * Clients
        */
        composable(Routes.CLIENTS) {
            ClientScreen(
                onBackClick = { navController.popBackStack() },
                onAddClientClick = {
                    navController.navigate(Routes.ADD_CLIENT)
                },
                onClientClick = { clientId ->
                    navController.navigate(Routes.editClient(clientId))
                }
            )
        }

        /*
        * Add Client
        */
        composable(Routes.ADD_CLIENT) {
            val viewModel: ClientListViewModel = hiltViewModel()
            val state = viewModel.formState.collectAsState().value
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            Scaffold(
                topBar = { TopAppBar(title = { Text("Add Client") }) },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                ClientFormContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onNameChange = viewModel::updateName,
                    onEmailChange = viewModel::updateEmail,
                    onCompanyChange = viewModel::updateCompany,
                    onSaveClick = { viewModel.saveClient() }
                )
                state.errorMessage?.let { message ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                        viewModel.clearError()
                    }
                }
                if (state.isSuccess) {
                    viewModel.clearSuccess()
                    navController.popBackStack()
                }
            }
        }

        /*
        * Edit Client
        */
        composable(
            route = Routes.EDIT_CLIENT,
            arguments = listOf(
                navArgument("clientId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val clientId =
                backStackEntry.arguments?.getLong("clientId")
                    ?: return@composable
            val viewModel: ClientListViewModel = hiltViewModel()
            val state = viewModel.formState.collectAsState().value
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(clientId) {
                viewModel.loadClient(clientId)
            }
            Scaffold(
                topBar = { TopAppBar(title = { Text("Edit Client") }) },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                ClientFormContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onNameChange = viewModel::updateName,
                    onEmailChange = viewModel::updateEmail,
                    onCompanyChange = viewModel::updateCompany,
                    onSaveClick = { viewModel.saveClient() }
                )
                state.errorMessage?.let { message ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                        viewModel.clearError()
                    }
                }
                if (state.isSuccess) {
                    viewModel.clearSuccess()
                    navController.popBackStack()
                }
            }
        }

        /*
        * Settings
        */
        composable(Routes.SETTINGS) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onToggleTheme = { themeViewModel.toggleTheme() }
            )
        }
    }
}