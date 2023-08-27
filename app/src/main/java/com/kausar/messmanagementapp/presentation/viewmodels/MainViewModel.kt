package com.kausar.messmanagementapp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.Mess
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
    private val appUser: State<List<User>> = _listOfAppUser

    private val _messList: MutableState<MessState> = mutableStateOf(MessState())
    val messNames: State<MessState> = _messList

    private val _memberList: MutableState<MemberState> = mutableStateOf(MemberState())
    val memberInfo: State<MemberState> = _memberList


    private val _count: MutableState<CountState> = mutableStateOf(CountState())
    val count: State<CountState> = _count

    private val _messPic = mutableStateOf("")
    val messPicture: State<String> = _messPic

    init {
        getContactNumber()
        getAppUser()
        getMessNames()
        viewModelScope.launch {
            loginPref.getLoginStatus().collectLatest {
                _loginStatus.value = it
            }
        }
    }

    fun getMessMembers() {
        viewModelScope.launch {
            firestoreRepo.getMessMembers().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        _memberList.value = MemberState(
                            listOfMember = result.data ?: emptyList()
                        )
                        countMeal()
                    }

                    is ResultState.Failure -> {
                        _memberList.value = MemberState(
                            error = result.message.localizedMessage
                        )
                    }

                    is ResultState.Loading -> {
                        _memberList.value = MemberState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun countMeal() = viewModelScope.launch {
        firestoreRepo.addMealCount().collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    getMealCnt()
                }
                else -> {
                }
            }
        }
    }

    private fun getMealCnt() = viewModelScope.launch {
        firestoreRepo.getSingleMealCount().collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    _count.value = CountState(
                        cnt = result.data,
                        success = "Meal count fetched successfully!"
                    )
                }

                is ResultState.Failure -> {
                    _count.value = CountState(
                        error = result.message.localizedMessage
                    )
                }

                is ResultState.Loading -> {
                    _count.value = CountState(
                        isLoading = true
                    )

                }
            }

        }
    }


    fun getMessNames() = viewModelScope.launch {

        firestoreRepo.getMessNames().collectLatest {
            when (it) {
                is ResultState.Success -> {
                    _messList.value = MessState(
                        listOfMess = it.data
                    )
                }

                is ResultState.Failure -> {
                    _messList.value = MessState(
                        error = it.message.localizedMessage
                    )
                }

                is ResultState.Loading -> {
                    _messList.value = MessState(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            firestoreRepo.getCurrentUserInfo().collectLatest { res ->
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

    fun getMessProfilePic(messId: String, messName: String) {
        for (mess in messNames.value.listOfMess) {
            if (mess.messId == messId && mess.messName == messName) {
                _messPic.value = mess.profilePhoto
                Log.d("getMessProfilePic: ", "${_messPic.value}")
                break
            }
        }
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


    data class MessState(
        val listOfMess: List<Mess> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class MemberState(
        val listOfMember: List<User> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class CountState(
        val cnt: MealCount? = null,
        val success: String = "",
        val error: String = "",
        val isLoading: Boolean = false
    )
}