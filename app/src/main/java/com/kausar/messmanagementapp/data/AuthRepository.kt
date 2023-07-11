package com.kausar.messmanagementapp.data

import android.app.Activity
import com.google.firebase.auth.AuthResult
import com.kausar.messmanagementapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser(phoneNumber: String, otp: String): Flow<Resource<AuthResult>>
    suspend fun registerUser(activity: Activity, username: String, phoneNumber: String): Flow<Resource<AuthResult>>
    suspend fun logout(): Flow<Resource<AuthResult>>
}
