package com.kausar.messmanagementapp.data.firebase_db

import com.kausar.messmanagementapp.data.model.Meal
import com.kausar.messmanagementapp.data.model.RealtimeMealResponse
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface RealtimeDbRepository {

    fun insert(meal: Meal) : Flow<ResultState<String>>

    fun getAllMeal() : Flow<ResultState<List<RealtimeMealResponse>>>

//    fun delete(id: String) : Flow<ResultState<String>>

    fun update(res: RealtimeMealResponse) : Flow<ResultState<String>>
}