package com.kausar.messmanagementapp.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import com.kausar.messmanagementapp.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginPref: LoginDataStore,
    private val firestoreRepo: FirebaseFirestoreRepo
) : ViewModel() {
    private val _loginStatus = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _loginStatus

    private val _contact = mutableStateOf("")
    val contact: State<String> = _contact

    private val _userProfile = mutableStateOf(User())
    val userInfo: State<User> = _userProfile

    private val _listOfAppUser = mutableStateOf(listOf<User>())
    val appUser: State<List<User>> = _listOfAppUser


    init {
        getUserInfo()
        getAppUser()
        getContactNumber()
        viewModelScope.launch {
            loginPref.getLoginStatus().collectLatest {
                _loginStatus.value = it
            }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            firestoreRepo.getUserInfo().collectLatest { res ->
                when (res) {
                    is ResultState.Success -> {
                        res.data?.let {
                            _userProfile.value = it
                        }
                    }

                    is ResultState.Failure -> {
                        println(res.message.localizedMessage)
                    }

                    is ResultState.Loading -> {

                    }
                }
            }
        }
    }

    fun isAppUser(phone: String): Boolean {
        var status = false
        if (appUser.value.isNotEmpty()) {
            for (user in appUser.value) {
                println("app user contact number ${user.contactNo}")
                if (user.contactNo == phone) {
                    status = true
                    _userProfile.value = user
                    break
                }
            }
        }
        return status
    }

    private fun getAppUser() = viewModelScope.launch {
        firestoreRepo.getAppUser().collectLatest { res ->
            when (res) {
                is ResultState.Success -> {
                    res.data?.let {
                        _listOfAppUser.value = res.data
                    }
                }

                is ResultState.Failure -> {
                    println(res.message.localizedMessage)
                }

                is ResultState.Loading -> {

                }
            }

        }
    }

    private fun getContactNumber() = viewModelScope.launch {
        loginPref.getContactNumber().collectLatest {
            _contact.value = it
        }
    }

    fun saveLoginStatus(status: Boolean) =
        viewModelScope.launch { loginPref.saveLoginStatus(status) }

    fun saveContact(phone: String) = viewModelScope.launch { loginPref.saveContactNumber(phone) }
}