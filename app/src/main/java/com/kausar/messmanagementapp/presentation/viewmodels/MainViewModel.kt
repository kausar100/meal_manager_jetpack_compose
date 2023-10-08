package com.kausar.messmanagementapp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.Balance
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import com.kausar.messmanagementapp.presentation.shopping_info.ListType
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginPref: LoginDataStore, private val firestoreRepo: FirebaseFirestoreRepo
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


    private val _totalMealCntPerMember =mutableStateMapOf<String,MealCount>()
    val totalMealCntPerMember = _totalMealCntPerMember

    private val _todayTotalMealCnt: MutableState<MealCount> = mutableStateOf(MealCount())
    val todayTotalMealCnt: State<MealCount> = _todayTotalMealCnt

    private val _messPic = mutableStateOf("")
    val messPicture: State<String> = _messPic

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)


    private var _balance = mutableStateOf(Balance())
    val balanceInfo: State<Balance> = _balance


    init {
        getLoginStatus()
        getContactNumber()
        getAppUser()
        getMessNames()
        getBalanceInformation()
    }

    private fun getLoginStatus() {
        viewModelScope.launch {
            loginPref.getLoginStatus().collectLatest {
                _loginStatus.value = it
            }
        }
    }

    private var _listType = mutableStateOf(ListType.Grid)
    val listType: State<ListType> = _listType

    fun toggleList() {
        _listType.value = if (_listType.value == ListType.List) ListType.Grid else ListType.List
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
        Log.d("now", "addMembersMealCount: true")
        viewModelScope.launch {
            firestoreRepo.countMeal().collectLatest {
                if (it is ResultState.Success) {
                    setSingleMemberMealCount(it.data.first, it.data.second)
                }
            }
        }
    }

    private fun setTodayTotalMealCount(data: List<Pair<String, MealInfo>>) {
        var breakfast = 0.0
        var lunch = 0.0
        var dinner = 0.0
        var total = 0.0
        if (data.isNotEmpty()) {
            for (cnt in data) {
                breakfast += cnt.second.cntBreakFast
                lunch += cnt.second.cntLunch
                dinner += cnt.second.cntDinner
            }
            total = (breakfast * 0.5) + lunch + dinner
        }
        _todayTotalMealCnt.value = MealCount(breakfast, lunch, dinner, total)
    }

    fun setSingleMemberMealCount(userId: String, entry: MealCount = MealCount()) {
        _totalMealCntPerMember[userId] = entry
        if (memberInfo.value.listOfMember.isNotEmpty() && totalMealCntPerMember.size == memberInfo.value.listOfMember.size){
            setTotalMealCount()
        }
    }

    private fun setTotalMealCount() {
        var totalMealCount = 0.0
        if (totalMealCntPerMember.isNotEmpty()) {
            for (cnt in totalMealCntPerMember.values) {
                totalMealCount += cnt.total
            }
        }
        addTotalMealCount(totalMealCount.toString())
    }

    fun getMembersTodayMealCount() = viewModelScope.launch {
        firestoreRepo.getMembersMealCountForToday(today).collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    setTodayTotalMealCount(result.data)
                }

                is ResultState.Failure -> {

                }

                is ResultState.Loading -> {

                }
            }

        }
    }

    fun getBalanceInformation() {
        viewModelScope.launch {
            firestoreRepo.getBalanceInformation().collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _balance.value = it.data
                    }

                    is ResultState.Failure -> {

                    }

                    is ResultState.Loading -> {

                    }
                }
            }
        }
    }

    fun addAccountBalance(money: Double) {
        val amount = money + balanceInfo.value.totalReceivingAmount.ifEmpty { "0.0" }.toDouble()
        viewModelScope.launch {
            firestoreRepo.addAccountBalance(amount.toString()).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        Log.d("TAG", "adBalanceAfterUpdate: ${it.data}")
                        _balance.value = it.data
                        Log.d("TAG", "addAccountBalance: success")
                    }

                    is ResultState.Failure -> {
                        Log.d("TAG", "addAccountBalance: failure")
                    }

                    is ResultState.Loading -> {

                    }
                }

            }
        }
    }

    fun addShoppingCostToBalance(money: Double) {
        val amount = money + balanceInfo.value.totalShoppingCost.ifEmpty { "0.0" }.toDouble()
        viewModelScope.launch {
            firestoreRepo.addShoppingCost(amount.toString()).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _balance.value = it.data
                        Log.d("TAG", "addShoppingCostAfterUpdate: ${it.data}")
                        Log.d("TAG", "addShoppingCost: success")
                    }

                    is ResultState.Failure -> {
                        Log.d("TAG", "addShoppingCost: failure")
                    }

                    is ResultState.Loading -> {

                    }
                }

            }
        }
    }


    private fun addTotalMealCount(totalMeal: String) = viewModelScope.launch {
        Log.d("TAG", "addTotalMealCount: $totalMeal")
        firestoreRepo.addTotalMeal(totalMeal).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    Log.d("TAG", "addTotalMealCount: success")
                }

                is ResultState.Failure -> {
                    Log.d("TAG", "addTotalMealCount: failure")
                }
                else ->{}

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
}