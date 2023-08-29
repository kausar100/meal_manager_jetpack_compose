package com.kausar.messmanagementapp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import com.kausar.messmanagementapp.presentation.home_screen.Keys
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
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


    private val _currentUserMealCnt: MutableState<SingleMealCountState> =
        mutableStateOf(SingleMealCountState())
    val currentUserMealCount: State<SingleMealCountState> = _currentUserMealCnt

    private val _membersMealCnt: MutableState<AllMealCountState> =
        mutableStateOf(AllMealCountState())
    private val membersMealCount: State<AllMealCountState> = _membersMealCnt

    private val _totalMealCnt = mutableStateMapOf<String, Double>()
    val totalMealCount = _totalMealCnt

    private val _membersTodayMealCnt: MutableState<AllMemberTodayCountState> =
        mutableStateOf(AllMemberTodayCountState())
    private val membersTodayMealCount: State<AllMemberTodayCountState> = _membersTodayMealCnt

    private val _todayMealCnt = mutableStateMapOf<String, Double>()
    val todayMealCount = _todayMealCnt

    private val _messPic = mutableStateOf("")
    val messPicture: State<String> = _messPic

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)

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
                        addMembersMealCount()
                        getMembersTodayMealCount()
                    }

                    is ResultState.Failure -> {
                        _memberList.value = MemberState(
                            error = result.message.localizedMessage ?: "some error occurred"
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

    private fun addMembersMealCount() {
        viewModelScope.launch {
            firestoreRepo.countMeal().collectLatest {
                if (it is ResultState.Success) {
                    getSingleMealCount()
                }
            }
        }
    }

    private fun getSingleMealCount() = viewModelScope.launch {
        firestoreRepo.getSingleMealCount(null).collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    _currentUserMealCnt.value = SingleMealCountState(
                        cnt = result.data,
                        success = "Meal count fetched successfully!"
                    )
                }

                is ResultState.Failure -> {
                    _currentUserMealCnt.value = SingleMealCountState(
                        error = result.message.localizedMessage ?: "some error occurred"
                    )
                }

                is ResultState.Loading -> {
                    _currentUserMealCnt.value = SingleMealCountState(
                        isLoading = true
                    )

                }
            }

        }
    }

    private fun getMembersTodayMealCount() = viewModelScope.launch {
        firestoreRepo.getMembersMealCountForToday(today).collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    Log.d("membersTodayMealCount: ", result.data.size.toString())
                    _membersTodayMealCnt.value = AllMemberTodayCountState(
                        info = result.data
                    )
                    val count = getTodayMealCount()
                    for (item in count) {
                        _todayMealCnt[item.key] = item.value
                    }
                }

                is ResultState.Failure -> {
                    _membersTodayMealCnt.value = AllMemberTodayCountState(
                        error = result.message.localizedMessage ?: "some error occurred"
                    )
                    val count = hashMapOf(
                        Keys.Total.name to 0.0,
                        Keys.Breakfast.name to 0.0,
                        Keys.Lunch.name to 0.0,
                        Keys.Dinner.name to 0.0
                    )
                    for (item in count) {
                        _todayMealCnt[item.key] = item.value
                    }

                }

                is ResultState.Loading -> {
                    _membersTodayMealCnt.value = AllMemberTodayCountState(
                        isLoading = true
                    )
                }
            }

        }
    }

    private fun getTodayMealCount(): HashMap<String, Double> {
        membersTodayMealCount.value.info.isNotEmpty().let {
            var breakfast = 0.0
            var lunch = 0.0
            var dinner = 0.0

            for (meal in membersTodayMealCount.value.info) {
                meal.breakfast?.let {
                    if (meal.breakfast) breakfast += 1.0
                }
                meal.lunch?.let {
                    if (meal.lunch) lunch += 1.0
                }
                meal.dinner?.let {
                    if (meal.dinner) dinner += 1.0
                }
            }

            return hashMapOf(
                Keys.Breakfast.name to breakfast,
                Keys.Lunch.name to lunch,
                Keys.Dinner.name to dinner
            )
        }
    }

    fun getAllMealCount() = viewModelScope.launch {
        firestoreRepo.getMealCount().collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    Log.d("getAllMealCount: ", result.data.size.toString())
                    _membersMealCnt.value = AllMealCountState(
                        cnt = result.data
                    )
                    val count = getTotalCount()
                    for (item in count) {
                        _totalMealCnt[item.key] = item.value
                    }
                }

                is ResultState.Failure -> {
                    _membersMealCnt.value = AllMealCountState(
                        error = result.message.localizedMessage ?: "some error occurred"
                    )
                    val count = hashMapOf(
                        Keys.Total.name to 0.0,
                        Keys.Breakfast.name to 0.0,
                        Keys.Lunch.name to 0.0,
                        Keys.Dinner.name to 0.0
                    )
                    for (item in count) {
                        _totalMealCnt[item.key] = item.value
                    }
                }

                is ResultState.Loading -> {
                    _membersMealCnt.value = AllMealCountState(
                        isLoading = true
                    )
                }
            }

        }
    }

    private fun getTotalCount(): HashMap<String, Double> {

        membersMealCount.value.cnt.isNotEmpty().let {
            var breakfast = 0.0
            var lunch = 0.0
            var dinner = 0.0
            var total = 0.0
            for (meal in membersMealCount.value.cnt) {
                breakfast += meal.breakfast
                lunch += meal.lunch
                dinner += meal.dinner
                total += meal.total
            }

            return hashMapOf(
                Keys.Total.name to total,
                Keys.Breakfast.name to breakfast,
                Keys.Lunch.name to lunch,
                Keys.Dinner.name to dinner
            )
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
                        error = it.message.localizedMessage ?: "some error occurred"
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
                Log.d("getMessProfilePic: ", _messPic.value)
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

    data class SingleMealCountState(
        val cnt: MealCount? = null,
        val success: String = "",
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class AllMealCountState(
        val cnt: List<MealCount> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class AllMemberTodayCountState(
        val info: List<MealInfo> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )
}