package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseFirestoreRepo {

    fun entryUserInfo(user: User): Flow<ResultState<String>>

    fun getUserInfo(): Flow<ResultState<User?>>

    fun getMessNames(): Flow<ResultState<List<String>>>

    fun getMemberList(): Flow<ResultState<List<User>?>>

    fun insertMeal(meal: MealInfo): Flow<ResultState<String>>

    fun updateMeal(meal: MealInfo): Flow<ResultState<String>>

    fun getAppUser(): Flow<ResultState<List<User>?>>

    fun getMealByDate(date: String): Flow<ResultState<MealInfo?>>

    fun getAllMeal(date: String, currentUser: String? = null): Flow<ResultState<List<MealInfo>>>
}