package com.kausar.messmanagementapp.presentation.viewmodels


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.AddMoneyWithUser
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.MemberShoppingList
import com.kausar.messmanagementapp.data.model.Shopping
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FirebaseFirestoreDbViewModel @Inject constructor(
    private val repo: FirebaseFirestoreRepo
) : ViewModel() {
    private val _res: MutableState<ItemState> = mutableStateOf(ItemState())
    val response: State<ItemState> = _res

    private val _todayMeal: MutableState<SingleMeal> = mutableStateOf(SingleMeal())
    val mealInfo: State<SingleMeal> = _todayMeal

    private val _newMeal: MutableState<SingleMeal> = mutableStateOf(SingleMeal())
    val newMealInfo: State<SingleMeal> = _newMeal

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)

    private val _addMoney = mutableStateOf(AddMoneyStatus())
    val addMoneyInfo: State<AddMoneyStatus> = _addMoney

    val totalMoneyPerMember = mutableStateOf(HashMap<String, Double>()) //userid,total money
    val addMoneyListPerMember =
        mutableStateOf(HashMap<String, List<AddMoney>>()) //userId,list<info>

    val shoppingPerMember =
        mutableStateOf(HashMap<String, MemberShoppingList>()) //userid, (shopping list, cost)

    private var _shoppingList = mutableStateListOf<Shopping>()
    val shoppingList: SnapshotStateList<Shopping> = _shoppingList

    private val _newShopping = mutableStateOf(NewShoppingStatus())
    val newShoppingInfo: State<NewShoppingStatus> = _newShopping

    fun getAllMeal(userId: String) {
        viewModelScope.launch {
            repo.getUserMealByMonth(userId).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _res.value = ItemState(
                            item = it.data
                        )
                    }

                    is ResultState.Failure -> {
                        _res.value = ItemState(
                            error = it.message.localizedMessage!!.toString()
                        )
                    }

                    is ResultState.Loading -> {
                        _res.value = ItemState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun getMealByUserId(userId: String) = viewModelScope.launch {
        repo.getUserMealByMonth(userId).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    _res.value = ItemState(
                        item = it.data
                    )
                }

                is ResultState.Failure -> {
                    _res.value = ItemState(
                        error = it.message.localizedMessage!!.toString()
                    )
                }

                is ResultState.Loading -> {
                    _res.value = ItemState(
                        isLoading = true
                    )
                }
            }
        }
    }
    fun addMemberMoney(data: AddMoneyWithUser) =
        repo.addMoneyInfo(user = data.user, newEntry = data.info)

    fun addNewShopping(user: User, data: Shopping) = repo.newShoppingEntry(user, data)

    fun setAccountBalance() {
        var sum = 0.0
        if (totalMoneyPerMember.value.isNotEmpty()) {
            for (money in totalMoneyPerMember.value.values) {
                sum += money
            }
        }
        addAccountBalance(sum.toString())
    }

    private fun addAccountBalance(money: String) {
        viewModelScope.launch {
            repo.addAccountBalance(money).collectLatest {
                when (it) {
                    is ResultState.Success -> {
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
    private fun getMoneySumPerMember(data: List<AddMoney>): String {
        var total = 0.0
        if (data.isNotEmpty()) {
            for (item in data) {
                if (item.amount.isNotEmpty()) {
                    total += item.amount.toDouble()
                }

            }
        }
        return total.toString()
    }

    private fun getShoppingCostPerMember(data: List<Shopping>): String {
        var total = 0.0
        if (data.isNotEmpty()) {
            for (item in data) {
                if (item.totalCost.isNotEmpty()) {
                    total += item.totalCost.toDouble()
                }

            }
        }
        return total.toString()
    }

    private fun saveShoppingInfo(
        userId: String,
        data: List<Shopping>,
    ) {
        _newShopping.value = NewShoppingStatus(
            info = data
        )
        val sum = getShoppingCostPerMember(data)
        shoppingPerMember.value[userId] =
            MemberShoppingList(info = data, totalCost = sum)
    }

    fun clearShoppingList(){
        _shoppingList.clear()
        Log.d("TAG", "getShoppingInfoClear: ${shoppingList.toList()}")
    }

    fun getShoppingInfo(member: User) {
        viewModelScope.launch {
            repo.getUserShoppingInfo(member).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _shoppingList.addAll(it.data)
                        Log.d("TAG", "getShoppingInfoAfterAdd: ${shoppingList.toList()}")
                        saveShoppingInfo(member.userId,it.data)
                    }

                    is ResultState.Failure -> {
                        _newShopping.value = NewShoppingStatus(
                            error = it.message.localizedMessage?.toString()
                                ?: "Unknown error occur!"
                        )
                    }

                    is ResultState.Loading -> {
                        _newShopping.value = NewShoppingStatus(
                            isLoading = true
                        )
                    }
                }

            }

        }
    }

    private fun saveBalanceInfo(
        userId: String,
        info: List<AddMoney>,
    ) {
        _addMoney.value = AddMoneyStatus(
            info = info
        )
        addMoneyListPerMember.value[userId] = info
        var sum = "0.0"
        if (info.isNotEmpty()) {
            sum = getMoneySumPerMember(info)
        }
        totalMoneyPerMember.value[userId] = sum.toDouble()
    }

    fun getMoneyInfo(member: User) {
        viewModelScope.launch {
            repo.getUserMoneyInfo(member).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        saveBalanceInfo(member.userId, it.data)
                    }

                    is ResultState.Failure -> {
                        _addMoney.value = AddMoneyStatus(
                            error = it.message.localizedMessage?.toString()
                                ?: "Unknown error occur!"
                        )
                    }

                    is ResultState.Loading -> {
                        _addMoney.value = AddMoneyStatus(
                            isLoading = true
                        )
                    }
                }

            }
        }

    }

    fun insert(meal: MealInfo) = repo.insertCurrentUserMeal(meal)

    fun update(meal: MealInfo) = repo.updateCurrentUserMeal(meal)

    fun getMealForToday() = viewModelScope.launch {
        repo.getUserMealByDay(today, null).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _todayMeal.value = SingleMeal(
                            meal = meal, success = "Meal fetch successfully!"
                        )

                    }

                }

                is ResultState.Failure -> {
                    _todayMeal.value = SingleMeal(
                        error = it.message.localizedMessage!!.toString()
                    )
                }

                is ResultState.Loading -> {
                    _todayMeal.value = SingleMeal(
                        isLoading = true
                    )
                }
            }
        }

    }

    fun getMealAtDate(date: String) = viewModelScope.launch {
        _newMeal.value = SingleMeal()
        repo.getUserMealByDay(date, null).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _newMeal.value = SingleMeal(
                            meal = meal, success = "Meal fetch successfully!"
                        )

                    }

                }

                is ResultState.Failure -> {
                    _newMeal.value = SingleMeal(
                        error = it.message.localizedMessage!!.toString()
                    )
                }

                is ResultState.Loading -> {
                    _newMeal.value = SingleMeal(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun registerUser(user: User) = repo.entryUserInfo(user)

    data class ItemState(
        val item: List<MealInfo> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class SingleMeal(
        val error: String = "",
        val success: String = "",
        val meal: MealInfo = MealInfo(),
        val isLoading: Boolean = false
    )

    data class AddMoneyStatus(
        val info: List<AddMoney> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class NewShoppingStatus(
        val info: List<Shopping> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )
}

