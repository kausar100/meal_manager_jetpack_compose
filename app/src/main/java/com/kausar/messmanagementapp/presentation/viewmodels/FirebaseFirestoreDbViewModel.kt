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

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)

    private val _singleMealcnt = MutableStateFlow(SingleMealCount())
    val singleMealCnt: StateFlow<SingleMealCount> = _singleMealcnt

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
                    _singleMealcnt.update { cnt ->
                        cnt.copy(
                            breakfast = 0.0,
                            lunch = 0.0,
                            dinner = 0.0,
                            totalMeal = 0.0
                        )
                    }
                    val meals = it.data.asReversed()
                    getSingleMealCnt(meals)
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

    private fun needToStop(mealDate: String): Boolean {
        val currentDay = mealDate.substring(0, 2).toInt()

        val todayDay = today.substring(0, 2).toInt()

        return currentDay.minus(todayDay) >= 0
    }

    private fun getSingleMealCnt(meals: List<MealInfo>) {
        if (meals.isNotEmpty()) {
            for (meal in meals) {
                if (needToStop(meal.date!!))
                    break
                if (meal.breakfast == true) {
                    _singleMealcnt.update {
                        it.copy(
                            breakfast = it.breakfast.plus(1.0),
                            totalMeal = it.totalMeal.plus(0.5)
                        )
                    }
                }
                if (meal.lunch == true) {
                    _singleMealcnt.update {
                        it.copy(
                            lunch = it.lunch.plus(1.0),
                            totalMeal = it.totalMeal.plus(1.0),
                        )
                    }
                }
                if (meal.dinner == true) {
                    _singleMealcnt.update {
                        it.copy(
                            dinner = it.dinner.plus(1.0),
                            totalMeal = it.totalMeal.plus(1.0),
                        )
                    }
                }
            }
        }

    }

    fun insert(meal: MealInfo) = repo.insertCurrentUserMeal(meal)

    fun update(meal: MealInfo) = repo.updateCurrentUserMeal(meal)

    fun getMealForToday() = viewModelScope.launch {
        repo.getCurrentUserMealByDay(today).collectLatest {
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
        repo.getCurrentUserMealByDay(date).collectLatest {
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
        val breakfast: Double = 0.0,
        val lunch: Double = 0.0,
        val dinner: Double = 0.0,
        val totalMeal: Double = 0.0
    )
}

