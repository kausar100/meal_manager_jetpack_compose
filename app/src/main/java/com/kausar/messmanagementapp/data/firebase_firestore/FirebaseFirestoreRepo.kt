package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseFirestoreRepo {

    fun entryUserInfo(user: User): Flow<ResultState<String>>

    fun updateUserMessInfo(contact: String, messId: String): Flow<ResultState<String>>

    fun getUserInfo(): Flow<ResultState<User?>>

    fun getMessNames(): Flow<ResultState<List<String>>>

    fun insert(meal: MealInfo): Flow<ResultState<String>>

    fun update(meal: MealInfo): Flow<ResultState<String>>

    fun getMealByDate(date: String): Flow<ResultState<MealInfo?>>

    fun getAllMeal(date: String): Flow<ResultState<List<MealInfo>>>
}