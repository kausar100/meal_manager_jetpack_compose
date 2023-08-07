package com.kausar.messmanagementapp.data.firebase_firestore

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.CollectionReference
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.toMap
import com.kausar.messmanagementapp.data.shared_pref.LoginPreference
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseFirestoreRepoImpl @Inject constructor(
    private val firestoreDb: CollectionReference,
    private val datastore: LoginPreference
) : FirebaseFirestoreRepo {

    var phoneNumber: String = ""

    private fun getMonthYear(date: String): String {
        val month = date.substring(startIndex = 3, endIndex = date.length)
        return month.replace("/", "_")
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            datastore.getContactNumber().collectLatest {
                phoneNumber = it
            }
        }
    }

    override fun insert(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(meal.date!!)

        firestoreDb.document(phoneNumber).collection(monthYear).whereEqualTo("date", meal.date)
            .get().addOnSuccessListener {
                it?.let { result ->
                    if (result.documents.isNotEmpty()) {
                        trySend(ResultState.Failure(Exception("Data already exists!")))
                    } else {
                        val hashMeal = meal.toMap()

                        firestoreDb.document(phoneNumber).collection(monthYear).add(hashMeal)
                            .addOnSuccessListener { rst ->
                                if (rst?.id != null) {
                                    trySend(
                                        ResultState.Success(
                                            "Data Inserted Successfully!"
                                        )
                                    )

                                }
                            }.addOnFailureListener { exp ->
                                trySend(ResultState.Failure(exp))
                            }
                    }
                }
            }

        awaitClose {
            close()
        }
    }

    override fun update(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(meal.date!!)

        firestoreDb.document(phoneNumber).collection(monthYear).whereEqualTo("date", meal.date)
            .get().addOnSuccessListener {
                it?.let { result ->
                    if (result.documents.isNotEmpty()) {
                        val doc = result.documents[0]
                        val hashMeal = meal.toMap()
                        doc.reference.update(hashMeal).addOnSuccessListener {
                            trySend(
                                ResultState.Success(
                                    "Data updated Successfully!"
                                )
                            )

                        }.addOnFailureListener { exp ->
                            trySend(ResultState.Failure(exp))
                        }

                    } else {
                        trySend(ResultState.Failure(Exception("Data doesn't exists!")))
                    }
                }
            }.addOnFailureListener { exp ->
                trySend(ResultState.Failure(exp))
            }

        awaitClose {
            close()
        }

    }

    override fun getMealByDate(date: String): Flow<ResultState<MealInfo?>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(date)

        firestoreDb.document(phoneNumber).collection(monthYear).whereEqualTo("date", date)
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
    override fun getAllMeal(date: String): Flow<ResultState<List<MealInfo>>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(date)

        firestoreDb.document(phoneNumber).collection(monthYear).get()
            .addOnSuccessListener { result ->
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