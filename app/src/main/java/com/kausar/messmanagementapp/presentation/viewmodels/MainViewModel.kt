package com.kausar.messmanagementapp.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginPref: LoginDataStore
) : ViewModel() {
    private val _loginStatus = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _loginStatus

    init {
        viewModelScope.launch {
            loginPref.getLoginStatus().collectLatest {
                _loginStatus.value = it
            }
        }
    }

    fun saveLoginStatus(status: Boolean) = viewModelScope.launch { loginPref.saveLoginStatus(status) }
}