package com.kausar.messmanagementapp.data.firebase_firestore

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.MemberType
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.model.toMap
import com.kausar.messmanagementapp.data.shared_pref.LoginPreference
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.network_connection.Network
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
    private val firestore: FirebaseFirestore,
    private val datastore: LoginPreference,
    private val context: Context
) : FirebaseFirestoreRepo {

    object CollectionRef {
        const val userDb = "UserInfo"
        const val mealDb = "MealInfo"
        const val messNameDb = "MessNameInfo"
    }

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

    override fun entryUserInfo(user: User): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            //check user type
            if (user.userType == MemberType.Manager.name) {
                //for manager
                //check if new mess or not
                firestore.collection(CollectionRef.messNameDb)
                    .whereEqualTo("messName", user.messName).get()
                    .addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            //old mess
                            val mess = it.documents[0].toObject<Mess>()
                            //check if this is manager or not
                            if (mess!!.managerContactNumber != user.contactNo) {
                                trySend(ResultState.Failure(Exception("Manager Access denied!")))
                            } else {
                                trySend(
                                    ResultState.Success(
                                        "Welcome back ${user.userName}!"
                                    )
                                )
                            }

                        } else {
                            //new mess
                            val userInfo = user.toMap()

                            //new userinfo entry
                            firestore.collection(CollectionRef.userDb).add(userInfo)
                                .addOnSuccessListener { rst ->
                                    if (rst?.id != null) {
                                        val newMess =
                                            Mess(user.messName, rst.id, user.contactNo, "").toMap()

                                        //new mess entry
                                        firestore.collection(CollectionRef.messNameDb).add(
                                            newMess
                                        ).addOnSuccessListener {
                                            // mess_id = manager userinfo doc ref

                                            //update mess_id of user
                                            CoroutineScope(Dispatchers.IO).launch {
                                                updateUserMessInfo(
                                                    user.contactNo,
                                                    rst.id
                                                ).collectLatest { response ->
                                                    trySend(response)
                                                }
                                            }

                                        }
                                            .addOnFailureListener { e ->
                                                trySend(ResultState.Failure(e))
                                            }

                                    }
                                }
                                .addOnFailureListener { e ->
                                    trySend(ResultState.Failure(e))
                                }

                        }
                    }
                    .addOnFailureListener { e ->
                        trySend(ResultState.Failure(e))
                    }

            } else if (user.userType == MemberType.Member.name) {
                //for member
                //find mess_id
                firestore.collection(CollectionRef.messNameDb)
                    .whereEqualTo("messName", user.messName).get()
                    .addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            val mess = it.documents[0].toObject<Mess>()
                            //add mess_id
                            val userInfo = user.copy(messId = mess!!.messId).toMap()

                            //new userinfo entry
                            firestore.collection(CollectionRef.userDb).add(userInfo)
                                .addOnSuccessListener { rst ->
                                    if (rst?.id != null) {
                                        trySend(
                                            ResultState.Success(
                                                "Data inserted successfully!"
                                            )
                                        )
                                    }
                                }
                                .addOnFailureListener { e ->
                                    trySend(ResultState.Failure(e))
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        trySend(ResultState.Failure(e))
                    }
            }
        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }

        awaitClose {
            close()
        }

    }

    override fun updateUserMessInfo(contact: String, messId: String): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            if (Network.isNetworkAvailable(context)) {
                firestore.collection(CollectionRef.userDb).whereEqualTo("contactNo", contact)
                    .get().addOnSuccessListener {
                        it?.let { result ->
                            if (result.documents.isNotEmpty()) {
                                val doc = result.documents[0]
                                doc.reference.update(
                                    "messId",
                                    messId,
                                )
                                    .addOnSuccessListener {
                                        trySend(
                                            ResultState.Success(
                                                "Data inserted Successfully!"
                                            )
                                        )
                                    }
                                    .addOnFailureListener { exp ->
                                        trySend(ResultState.Failure(exp))
                                    }

                            } else {
                                trySend(ResultState.Failure(Exception("User doesn't exists!")))
                            }
                        }
                    }.addOnFailureListener { exp ->
                        trySend(ResultState.Failure(exp))
                    }

            } else {
                trySend(ResultState.Failure(Exception("Please check your internet connection!")))
            }


            awaitClose {
                close()
            }

        }

    override fun getUserInfo(): Flow<ResultState<User?>> = callbackFlow {
        trySend(ResultState.Loading)
        if (Network.isNetworkAvailable(context)) {

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }

        awaitClose {
            close()
        }
    }

    override fun getMessNames(): Flow<ResultState<List<String>>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            firestore.collection(CollectionRef.messNameDb).get()
                .addOnSuccessListener { result ->
                    val lisOfMess = mutableListOf<String>()
                    for (document in result.documents) {
                        val mess = document.toObject(Mess::class.java)
                        if (mess != null) {
                            lisOfMess.add(mess.messName)
                        }
                    }
                    trySend(ResultState.Success(lisOfMess))

                }.addOnFailureListener {
                    trySend(ResultState.Failure(it))
                }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }

        awaitClose {
            close()
        }
    }


    override fun insert(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(meal.date!!)

        if (Network.isNetworkAvailable(context)) {
            if (phoneNumber.isNotEmpty()) {
                firestore.collection(CollectionRef.mealDb).document(phoneNumber)
                    .collection(monthYear).whereEqualTo("date", meal.date)
                    .get().addOnSuccessListener {
                        it?.let { result ->
                            if (result.documents.isNotEmpty()) {
                                trySend(ResultState.Failure(Exception("Data already exists!")))
                            } else {
                                val hashMeal = meal.toMap()

                                firestore.collection(CollectionRef.mealDb).document(phoneNumber)
                                    .collection(monthYear).add(hashMeal)
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
            }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }


        awaitClose {
            close()
        }
    }

    override fun update(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(meal.date!!)

        if (Network.isNetworkAvailable(context)) {
            if (phoneNumber.isNotEmpty()) {
                firestore.collection(CollectionRef.mealDb).document(phoneNumber)
                    .collection(monthYear).whereEqualTo("date", meal.date)
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
            }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }



        awaitClose {
            close()
        }

    }

    override fun getMealByDate(date: String): Flow<ResultState<MealInfo?>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(date)

        if (Network.isNetworkAvailable(context)) {
            if (phoneNumber.isNotEmpty()) {
                firestore.collection(CollectionRef.mealDb).document(phoneNumber)
                    .collection(monthYear).whereEqualTo("date", date)
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
            }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }


        awaitClose {
            close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllMeal(date: String): Flow<ResultState<List<MealInfo>>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(date)

        if (Network.isNetworkAvailable(context)) {
            if (phoneNumber.isNotEmpty()) {
                firestore.collection(CollectionRef.mealDb).document(phoneNumber)
                    .collection(monthYear).get()
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
            }

        } else {
            trySend(ResultState.Failure(Exception("Please check your internet connection!")))
        }

        awaitClose {
            close()
        }
    }
}