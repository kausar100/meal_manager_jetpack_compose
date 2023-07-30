package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.RealtimeMealResponse
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

data class Response(
    val key: String?,
    val message: String?
)


interface FirebaseFirestoreRepo {

    fun insert(meal: MealInfo) : Flow<ResultState<Response>>

    fun getMealByDate(date: String) : Flow<ResultState<MealInfo?>>

    fun getAllMeal() : Flow<ResultState<List<RealtimeMealResponse>>>
}