package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseFirestoreRepo {

    fun insert(meal: MealInfo) : Flow<ResultState<String>>

    fun getMealByDate(date: String) : Flow<ResultState<MealInfo?>>

    fun getAllMeal() : Flow<ResultState<List<MealInfo>>>
}