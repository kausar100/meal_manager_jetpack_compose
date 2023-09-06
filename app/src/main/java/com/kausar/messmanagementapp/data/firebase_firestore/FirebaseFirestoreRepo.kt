package com.kausar.messmanagementapp.data.firebase_firestore

import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseFirestoreRepo {

    fun entryUserInfo(user: User): Flow<ResultState<String>>

    fun addMoneyInfo(user: User,newEntry: AddMoney): Flow<ResultState<String>>
    fun getUserMoneyInfo(user: User): Flow<ResultState<List<AddMoney>>>

    fun getCurrentUserInfo(): Flow<ResultState<User?>>

    fun getMessNames(): Flow<ResultState<List<Mess>>>

    fun getMessMembers(): Flow<ResultState<List<User>?>>

    fun countSingleMeal(): Flow<ResultState<String>>

    fun getSingleMealCount(currentUID: String?) : Flow<ResultState<MealCount>>

    fun countMeal(): Flow<ResultState<String>>

    fun getMealCount(): Flow<ResultState<List<MealCount>>>

    fun updateMealCount( member: User, monthYear: String): Flow<ResultState<String>>

    fun insertCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun updateCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>>

    fun getAppUser(): Flow<ResultState<List<User>?>>

    fun getMembersMealCountForToday(today: String) : Flow<ResultState<List<MealInfo>>>

    fun getUserMealByDay(date: String, uid: String?): Flow<ResultState<MealInfo?>>

    fun getUserMealByMonth(currentUser: String? = null): Flow<ResultState<List<MealInfo>>>
}