package com.kausar.messmanagementapp.data.firebase_firestore

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.RealtimeMealResponse
import com.kausar.messmanagementapp.data.model.toMap
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseFirestoreRepoImpl @Inject constructor(
    private val firestoreDb: FirebaseFirestore
) : FirebaseFirestoreRepo {

    private val dbCollectionPath = "Meal_info"

    override fun insert(meal: MealInfo): Flow<ResultState<Response>> = callbackFlow {
        trySend(ResultState.Loading)

        val hashMeal = meal.toMap()

        firestoreDb.collection(dbCollectionPath).add(hashMeal)
            .addOnSuccessListener {
                if (it?.id != null) {
                    trySend(
                        ResultState.Success(
                            data = Response(
                                key = it.id,
                                message = "Data Inserted Successfully!"
                            )
                        )
                    )
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }

        awaitClose {
            close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllMeal(): Flow<ResultState<List<RealtimeMealResponse>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestoreDb.collection(dbCollectionPath).get()
            .addOnSuccessListener { result ->
                val meals = mutableListOf<RealtimeMealResponse>()
                for (document in result) {
                    meals.add(
                        RealtimeMealResponse(
                            meal = document.toObject(),
                            key = document.id
                        )
                    )
                }
                //sort by date ascending
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val sortedMeals = meals.sortedByDescending { LocalDate.parse(it.meal!!.date, formatter) }

                trySend(ResultState.Success(sortedMeals))

            }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }

        awaitClose {
            close()
        }
    }
}