package com.kausar.messmanagementapp.presentation.viewmodels


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.MealInfo
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

    private val _singleMeal: MutableState<SingleMeal> = mutableStateOf(SingleMeal())
    val mealInfo: State<SingleMeal> = _singleMeal

    private val _cnt = MutableStateFlow(MealCount())
    val mealCnt: StateFlow<MealCount> = _cnt

    fun getAllMeal() {
        val calender = Calendar.getInstance()
        val date = getDate(calender)
        viewModelScope.launch {
            delay(300)
            repo.getAllMeal(date).collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _res.value = ItemState(
                            item = it.data
                        )
                        _cnt.update { cnt->
                            cnt.copy(
                                breakfast = 0.0,
                                lunch = 0.0,
                                dinner = 0.0,
                                totalMeal = 0.0
                            )
                        }
                        getMealCnt(it.data)
                    }

                    is ResultState.Failure -> {
                        _res.value = ItemState(
                            error = it.message.toString()
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

    init {
        getAllMeal()
    }

    private fun getMealCnt(meals: List<MealInfo>) {
        for (meal in meals) {
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


    fun insert(meal: MealInfo) = repo.insert(meal)

    fun update(meal: MealInfo) = repo.update(meal)

    fun getMealAtDate(date: String) = viewModelScope.launch {
        repo.getMealByDate(date).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _singleMeal.value = SingleMeal(
                            meal = meal,
                            success = "Meal fetch successfully!"
                        )

                    }

                }

                is ResultState.Failure -> {
                    _singleMeal.value = SingleMeal(
                        error = it.message?.localizedMessage.toString()
                    )
                }

                is ResultState.Loading -> {
                    _singleMeal.value = SingleMeal(
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
}

