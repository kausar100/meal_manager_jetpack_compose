package com.kausar.messmanagementapp.data.shared_pref

import kotlinx.coroutines.flow.Flow

interface LoginPreference {
    fun getLoginStatus(): Flow<Boolean>
    suspend fun saveLoginStatus(status: Boolean)

    suspend fun saveContactNumber(contact: String)
    fun getContactNumber(): Flow<String>
}