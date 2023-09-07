package com.kausar.messmanagementapp.presentation.viewmodels


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.AddMoneyWithUser
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.MemberShoppingList
import com.kausar.messmanagementapp.data.model.Shopping
import com.kausar.messmanagementapp.data.model.TotalMoneyPerMember
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

    private val _singleMemberMealCount = mutableStateOf(SingleMealCount())
    val selectedMemberMealCnt: State<SingleMealCount> = _singleMemberMealCount

    private val _addMoney = mutableStateOf(AddMoneyStatus())
    val addMoneyInfo: State<AddMoneyStatus> = _addMoney

    val moneyPerMember = mutableStateOf(HashMap<String, TotalMoneyPerMember>()) //userid,total money
    val addMoneyListPerMember =
        mutableStateOf(HashMap<String, List<AddMoney>>()) //userId,list<info>
    val moneySum = mutableStateOf("") //members total money

    val shoppingPerMember =
        mutableStateOf(HashMap<String, MemberShoppingList>()) //userid, (shopping list, cost)
    val shoppingCostSum = mutableStateOf("") //members shopping total cost
    val shoppingList = mutableListOf<Shopping>()

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

    init {
        getMealForToday()
    }

    fun addMemberMoney(data: AddMoneyWithUser) =
        repo.addMoneyInfo(user = data.user, newEntry = data.info)

    fun addNewShopping(user: User, data: Shopping) = repo.newShoppingEntry(user, data)

    fun clearMoneyInfo() {
        _addMoney.value = AddMoneyStatus()
    }

    private fun getTotalMoneySum() {
        var sum = 0.0
        if (moneyPerMember.value.isNotEmpty()) {
            for (money in moneyPerMember.value) {
                if (money.value.total.isNotEmpty()) {
                    sum += money.value.total.toDouble()
                }

            }
        }
        moneySum.value = sum.toString()
    }

    private fun getTotalShoppingCostSum() {
        var sum = 0.0
        if (shoppingPerMember.value.isNotEmpty()) {
            for (money in shoppingPerMember.value) {
                if (money.value.totalCost.isNotEmpty()) {
                    sum += money.value.totalCost.toDouble()
                }

            }
        }
        shoppingCostSum.value = sum.toString()
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

    fun getShoppingInfo(member: User) {
        viewModelScope.launch {
            repo.getUserShoppingInfo(member).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        it.data?.let { data ->
                            _newShopping.value = NewShoppingStatus(
                                info = data
                            )
                            val sum = getShoppingCostPerMember(data)
                            shoppingPerMember.value[member.userId] =
                                MemberShoppingList(info = data, totalCost = sum)

                            shoppingList.addAll(data)
                            getTotalShoppingCostSum()
                            repo.addShoppingCost(shoppingCostSum.value).collectLatest { }
                        }

                    }

                    is ResultState.Failure -> {
                        _newShopping.value = NewShoppingStatus(
                            error = it.message.localizedMessage!!.toString()
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

    fun getMoneyInfo(member: User) {
        viewModelScope.launch {
            repo.getUserMoneyInfo(member).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        it.data?.let { data ->
                            _addMoney.value = AddMoneyStatus(
                                info = data
                            )
                            addMoneyListPerMember.value[member.userId] = data
                            val sum = getMoneySumPerMember(data)
                            moneyPerMember.value[member.userId] =
                                TotalMoneyPerMember(userId = member.userId, total = sum)
                            getTotalMoneySum()
                            repo.addAccountBalance(moneySum.value).collectLatest { }
                        }

                    }

                    is ResultState.Failure -> {
                        _addMoney.value = AddMoneyStatus(
                            error = it.message.localizedMessage!!.toString()
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

    fun getMemberMealCount(userId: String) = viewModelScope.launch {
        repo.getSingleMealCount(userId).collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    _singleMemberMealCount.value = SingleMealCount(
                        cnt = result.data,
                    )
                    Log.d("getMemberMealCount: ", selectedMemberMealCnt.value.cnt.toString())
                }

                is ResultState.Failure -> {
                    _singleMemberMealCount.value = SingleMealCount(
                        error = result.message.localizedMessage ?: "some error occurred"
                    )
                }

                is ResultState.Loading -> {
                    _singleMemberMealCount.value = SingleMealCount(
                        isLoading = true
                    )
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

    data class SingleMealCount(
        val cnt: MealCount? = null, val error: String = "", val isLoading: Boolean = false
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

