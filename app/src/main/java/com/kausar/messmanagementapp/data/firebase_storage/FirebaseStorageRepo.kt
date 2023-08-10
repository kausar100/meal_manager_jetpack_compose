package com.kausar.messmanagementapp.data.firebase_storage

import android.net.Uri
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirebaseStorageRepo {

    fun uploadProfilePicture(imageUri: Uri) : Flow<ResultState<String>>

    fun getProfilePicture() : Flow<ResultState<String>>
}