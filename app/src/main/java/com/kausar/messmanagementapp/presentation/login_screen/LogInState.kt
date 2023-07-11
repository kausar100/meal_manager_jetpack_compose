package com.kausar.messmanagementapp.presentation.login_screen

data class LogInState(
 val isLoading: Boolean = false,
 val isSuccess: String? = "",
 val isError: String? = "",
)
