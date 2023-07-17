package com.kausar.messmanagementapp.data.firebase_db

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.kausar.messmanagementapp.data.model.Meal
import com.kausar.messmanagementapp.data.model.RealtimeMealResponse
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RealtimeDbRepositoryImpl @Inject constructor(
    private val db: DatabaseReference
) : RealtimeDbRepository {

     private val phone_number = "01315783246"
    override fun insert(meal: Meal): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        db.child(phone_number).push().setValue(
            meal
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(ResultState.Success(data = "Data inserted Successfully!"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose {
            close()
        }

    }

    override fun getAllMeal(): Flow<ResultState<List<RealtimeMealResponse>>> = callbackFlow {
        trySend(ResultState.Loading)

        val valueEvent = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val meals = snapshot.child(phone_number).children.map {
                    RealtimeMealResponse(
                        meal = it.getValue(Meal::class.java),
                        key = it.key
                    )
                }

                //sort by date ascending
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val sortedMeals = meals.sortedBy { LocalDate.parse(it.meal!!.date, formatter) }

                trySend(ResultState.Success(sortedMeals))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }

        db.addValueEventListener(valueEvent)

        awaitClose {
            db.removeEventListener(valueEvent)
            close()
        }

    }

    override fun update(res: RealtimeMealResponse): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val map = HashMap<String, Any>()
        map["date"] = res.meal?.date!!
        map["day_name"] = res.meal?.dayName!!
        map["breakfast"] = res.meal?.breakfast!!
        map["lunch"] = res.meal?.lunch!!
        map["dinner"] = res.meal?.dinner!!
        map["status"] = res.meal?.status!!

        db.child(phone_number).child(res.key!!).updateChildren(
            map
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(ResultState.Success(data = "Data updated Successfully!"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose {
            close()
        }
    }
}