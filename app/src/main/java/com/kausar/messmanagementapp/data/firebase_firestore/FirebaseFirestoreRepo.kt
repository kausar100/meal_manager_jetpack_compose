package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseFirestoreRepo {

    fun entryUserInfo(user: User): Flow<ResultState<String>>

    fun getCurrentUserInfo(): Flow<ResultState<User?>>

    fun getMessNames(): Flow<ResultState<List<Mess>>>

    fun getMessMembers(): Flow<ResultState<List<User>?>>

    fun insertCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun updateCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun getAppUser(): Flow<ResultState<List<User>?>>

    fun getCurrentUserMealByDay(date: String): Flow<ResultState<MealInfo?>>

    fun getUserMealByMonth(date: String, currentUser: String? = null): Flow<ResultState<List<MealInfo>>>
}