package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.MealCount
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

    fun countSingleMeal(): Flow<ResultState<String>>

    fun getSingleMealCount() : Flow<ResultState<MealCount>>

//    fun updateSingleMealCount(monthYear: String): Flow<ResultState<String>>

    fun insertCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun updateCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun getAppUser(): Flow<ResultState<List<User>?>>

    fun getCurrentUserMealByDay(date: String): Flow<ResultState<MealInfo?>>

    fun getUserMealByMonth(currentUser: String? = null): Flow<ResultState<List<MealInfo>>>
}