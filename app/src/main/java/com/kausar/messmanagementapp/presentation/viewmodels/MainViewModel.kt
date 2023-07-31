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

    private val _user = mutableStateOf("")
    val userName: State<String> = _user

    private val _contact = mutableStateOf("")
    val contact: State<String> = _contact

    init {
        viewModelScope.launch {
            loginPref.getLoginStatus().collectLatest {
                _loginStatus.value = it
            }
            loginPref.getUsername().collectLatest {
                _user.value = it
            }
            loginPref.getContactNumber().collectLatest {
                _contact.value = it
            }
        }
    }



    fun saveLoginStatus(status: Boolean) = viewModelScope.launch { loginPref.saveLoginStatus(status) }

    fun saveUsername(username: String) = viewModelScope.launch { loginPref.saveUsername(username) }

    fun saveContact(phone: String) = viewModelScope.launch { loginPref.saveContactNumber(phone) }
}