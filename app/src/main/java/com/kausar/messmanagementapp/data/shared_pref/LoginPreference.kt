package com.kausar.messmanagementapp.data.shared_pref

import kotlinx.coroutines.flow.Flow

interface LoginPreference {

    /**
     * returns login status flow
     * */
    fun getLoginStatus(): Flow<Boolean>

    /**
     * saves login status in data store
     * */
    suspend fun saveLoginStatus(status: Boolean)
}