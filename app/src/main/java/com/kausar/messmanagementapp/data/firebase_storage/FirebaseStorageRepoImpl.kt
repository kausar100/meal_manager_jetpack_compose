package com.kausar.messmanagementapp.data.firebase_storage

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.network_connection.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseStorageRepoImpl @Inject constructor(
    private val context: Context,
    private val storageReference: StorageReference
) : FirebaseStorageRepo {

    var downloadUri: Uri? = null

    override fun uploadProfilePicture(imageUri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            val uploadTask = storageReference.putFile(imageUri)

            val urlTask = uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    it.exception?.let { exp ->
                        trySend(ResultState.Failure(exp))
                    }
                }
                storageReference.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        downloadUri = task.result
                        trySend(
                            ResultState.Success(
                                "Profile updated Successfully!"
                            )
                        )
                    } else {
                        trySend(ResultState.Failure(Exception("Error occurred during picture uploading!")))

                    }
                }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }

        awaitClose {
            close()
        }


    }

    override fun getProfilePicture(): Flow<ResultState<String>> {
        TODO("Not yet implemented")
    }
}