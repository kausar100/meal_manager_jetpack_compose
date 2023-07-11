package com.kausar.messmanagementapp.presentation.auth_screen

data class AuthState(
 val isLoading: Boolean = false,
 val isSuccess: String? = "",
 val isError: String? = "",
)
