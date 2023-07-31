package com.kausar.messmanagementapp.presentation.viewmodels


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseFirestoreDbViewModel @Inject constructor(
    private val repo: FirebaseFirestoreRepo
) : ViewModel() {
    private val _res: MutableState<ItemState> = mutableStateOf(ItemState())
    val response: State<ItemState> = _res


    init {
        viewModelScope.launch {
            repo.getAllMeal().collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        _res.value = ItemState(
                            item = it.data
                        )
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

    fun insert(meal: MealInfo) = repo.insert(meal)

    fun getMealAtDate(date: String) = viewModelScope.launch {
        repo.getMealByDate(date).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { meal ->
                        _res.value = ItemState(
                            meal = meal,
                            success = "Meal fetch successfully!"
                        )

                    }

                }

                is ResultState.Failure -> {
                    _res.value = ItemState(
                        error = it.message?.localizedMessage.toString()
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

    data class ItemState(
        val item: List<MealInfo> = emptyList(),
        val error: String = "",
        val success: String = "",
        val meal: MealInfo = MealInfo(),
        val isLoading: Boolean = false
    )
}

