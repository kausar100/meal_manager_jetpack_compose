package com.kausar.messmanagementapp.presentation.viewmodels


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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

    private val _cnt = MutableStateFlow(MealCount())
    val mealCnt: StateFlow<MealCount> = _cnt

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)

    private val _messList: MutableState<MessState> = mutableStateOf(MessState())
    val messNames: State<MessState> = _messList

    private val _memberList: MutableState<MemberState> = mutableStateOf(MemberState())
    val memberInfo: State<MemberState> = _memberList

    fun getAllMeal(userId: String) {
        viewModelScope.launch {
            delay(500)
               repo.getAllMeal(today, userId).collectLatest {
                        when (it) {
                            is ResultState.Success -> {
                                _res.value = ItemState(
                                    item = it.data
                                )
                                _cnt.update { cnt ->
                                    cnt.copy(
                                        breakfast = 0.0,
                                        lunch = 0.0,
                                        dinner = 0.0,
                                        totalMeal = 0.0
                                    )
                                }
                                val meals = it.data.asReversed()
                                getMealCnt(meals)
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
        repo.getAllMeal(today, userId).collectLatest {
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
        getMessNames()
        getMembers()
    }

    fun getMembers() {
        viewModelScope.launch {
            repo.getMemberList().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        _memberList.value = MemberState(
                            listOfMember = result.data ?: emptyList()
                        )
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

    private fun needToStop(mealDate: String): Boolean {
        val currentDay = mealDate.substring(0, 2).toInt()

        val todayDay = today.substring(0, 2).toInt()

        return currentDay.minus(todayDay) >= 0
    }

    private fun getMealCnt(meals: List<MealInfo>) {
        if (meals.isNotEmpty()) {
            for (meal in meals) {
                if (needToStop(meal.date!!))
                    break
                if (meal.breakfast == true) {
                    _cnt.update {
                        it.copy(
                            breakfast = it.breakfast.plus(1.0),
                            totalMeal = it.totalMeal.plus(0.5)
                        )
                    }
                }
                if (meal.lunch == true) {
                    _cnt.update {
                        it.copy(
                            lunch = it.lunch.plus(1.0),
                            totalMeal = it.totalMeal.plus(1.0),
                        )
                    }
                }
                if (meal.dinner == true) {
                    _cnt.update {
                        it.copy(
                            dinner = it.dinner.plus(1.0),
                            totalMeal = it.totalMeal.plus(1.0),
                        )
                    }
                }
            }
        }

    }

    fun insert(meal: MealInfo) = repo.insertMeal(meal)

    fun update(meal: MealInfo) = repo.updateMeal(meal)

    fun getMealForToday() = viewModelScope.launch {
        repo.getMealByDate(today).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _todayMeal.value = SingleMeal(
                            meal = meal,
                            success = "Meal fetch successfully!"
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
        repo.getMealByDate(date).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _newMeal.value = SingleMeal(
                            meal = meal,
                            success = "Meal fetch successfully!"
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

    fun getMessNames() = viewModelScope.launch {

        repo.getMessNames().collectLatest {
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

    data class MealCount(
        val breakfast: Double = 0.0,
        val lunch: Double = 0.0,
        val dinner: Double = 0.0,
        val totalMeal: Double = 0.0
    )

    data class MessState(
        val listOfMess: List<String> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )

    data class MemberState(
        val listOfMember: List<User> = emptyList(),
        val error: String = "",
        val isLoading: Boolean = false
    )
}

