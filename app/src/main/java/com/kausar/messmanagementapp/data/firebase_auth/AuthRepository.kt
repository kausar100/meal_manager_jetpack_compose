package com.kausar.messmanagementapp.data.firebase_auth

import android.app.Activity
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun createUserWithPhone(
        phoneNumber: String,
        activity: Activity
    ): Flow<ResultState<String>>

    fun signWithCredential(otp: String) : Flow<ResultState<String>>

    fun logout(): Flow<ResultState<String>>
}
