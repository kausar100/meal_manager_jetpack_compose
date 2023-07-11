package com.kausar.messmanagementapp.presentation.auth_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.AuthRepository
import com.kausar.messmanagementapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    val _authState = Channel<AuthState>()
    val authState = _authState.receiveAsFlow()

    fun registerUser(activity: Activity, userName: String?, phoneNumber: String) = viewModelScope.launch {
        repository.registerUser(activity,userName?:"", phoneNumber).collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    _authState.send(AuthState(isLoading = false, isSuccess = result.message?:"Success"))
                }

                is Resource.Loading -> {
                    _authState.send(AuthState(isLoading = true))
                }

                is Resource.Error -> {
                    _authState.send(AuthState(isLoading = false,isError = result.message))
                }
            }
        }
    }
}