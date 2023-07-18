package com.kausar.messmanagementapp.data.firebase_firestore

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.kausar.messmanagementapp.data.model.Meal
import com.kausar.messmanagementapp.data.model.MealStatus
import com.kausar.messmanagementapp.data.model.RealtimeMealResponse
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.cancel
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

    override fun insert(meal: Meal): Flow<ResultState<Response>> = callbackFlow {
        trySend(ResultState.Loading)
        val newMeal = hashMapOf<String, Any>(
            "date" to meal.date!!,
            "day" to meal.dayName!!,
            "breakfast" to meal.breakfast!!,
            "lunch" to meal.lunch!!,
            "dinner" to meal.dinner!!,
            "status" to meal.status!!
        )

        firestoreDb.collection(dbCollectionPath).add(newMeal)
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
                lateinit var meals: MutableList<RealtimeMealResponse>
                for (document in result) {
                    println(document.toString())
                    meals.add(
                        RealtimeMealResponse(
                            meal = Meal(
                                date = document.data["date"] as String,
                                dayName = document.data["day"] as String,
                                breakfast = document.data["breakfast"] as Boolean,
                                lunch = document.data["lunch"] as Boolean,
                                dinner = document.data["dinner"] as Boolean,
                                status = document.data["status"] as MealStatus,
                            ),
                            key = document.id
                        )
                    )
                }

                //sort by date ascending
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val sortedMeals = meals.sortedBy { LocalDate.parse(it.meal!!.date, formatter) }

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