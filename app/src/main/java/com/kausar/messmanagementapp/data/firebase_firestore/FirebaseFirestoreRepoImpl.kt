package com.kausar.messmanagementapp.data.firebase_firestore

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.CollectionReference
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.toMap
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseFirestoreRepoImpl @Inject constructor(
    private val firestoreDb: CollectionReference
) : FirebaseFirestoreRepo {

    override fun insert(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        firestoreDb.whereEqualTo("date", meal.date).get().addOnSuccessListener {
            it?.let { result ->
                if (result.documents.isNotEmpty()) {
                    trySend(ResultState.Failure(Exception("Data already exists!")))
                } else {
                    val hashMeal = meal.toMap()

                    firestoreDb.add(hashMeal)
                        .addOnSuccessListener { rst->
                            if (rst?.id != null) {
                                trySend(
                                    ResultState.Success(
                                        "Data Inserted Successfully!"
                                    )
                                )

                            }
                        }.addOnFailureListener {exp->
                            trySend(ResultState.Failure(exp))
                        }
                }
            }
        }

        awaitClose {
            close()
        }
    }

    override fun getMealByDate(date: String): Flow<ResultState<MealInfo?>> = callbackFlow {
        trySend(ResultState.Loading)

        firestoreDb.whereEqualTo("date", date)
            .get().addOnSuccessListener { result ->
                result?.let {
                    if (it.documents.size == 0) {
                        trySend(ResultState.Failure(Exception("Not found any meal for today!")))
                    } else {
                        val data = it.documents[0].toObject(MealInfo::class.java)
                        trySend(ResultState.Success(data))
                    }

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
    override fun getAllMeal(): Flow<ResultState<List<MealInfo>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestoreDb.get().addOnSuccessListener { result ->
            val meals = mutableListOf<MealInfo>()
            for (document in result.documents) {
                val meal = document.toObject(MealInfo::class.java)
                if (meal != null) {
                    meals.add(meal)
                }
            }
            //sort by date ascending
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val sortedMeals =
                meals.sortedByDescending { LocalDate.parse(it.date, formatter) }

            trySend(ResultState.Success(sortedMeals))

        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose {
            close()
        }
    }
}