package com.example.focustimerapp.feature.navigation

object Routes {

    const val DASHBOARD = "dashboard"
    const val CREATE_TASK = "create_task"

    // ==========================
    // TASKS
    // ==========================

    const val EDIT_TASK = "edit_task/{taskId}"
    const val TASK_DETAIL = "task_detail/{taskId}"

    fun editTaskRoute(taskId: Long) = "edit_task/$taskId"
    fun taskDetailRoute(taskId: Long) = "task_detail/$taskId"

    // ==========================
    // CLIENTS
    // ==========================

    const val CLIENTS = "clients"
    const val ADD_CLIENT = "clients/add"
    const val EDIT_CLIENT = "clients/edit/{clientId}"

    fun editClientRoute(clientId: Long): String =
        "clients/edit/$clientId"
}