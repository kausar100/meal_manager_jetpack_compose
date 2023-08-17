package com.kausar.messmanagementapp.data.firebase_firestore

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseFirestoreRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val datastore: LoginPreference,
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) : FirebaseFirestoreRepo {

    object CollectionRef {
        const val userDb = "UserInfo"
        const val mealDb = "MealInfo"
        const val messNameDb = "MessNameInfo"
    }

    private fun getMonthYear(date: String): String {
        val month = date.substring(startIndex = 3, endIndex = date.length)
        return month.replace("/", "_")
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
                            val currentUser = firebaseAuth.currentUser?.uid

                            currentUser?.let {
                                //new userinfo entry
                                val userInfo = user.copy(messId = currentUser)
                                val ref =
                                    firestore.collection(CollectionRef.userDb).document(currentUser)

                                ref.set(userInfo)
                                    .addOnSuccessListener {
                                        val newMess =
                                            Mess(
                                                user.messName,
                                                currentUser,
                                                user.contactNo,
                                                ""
                                            ).toMap()

                                        //new mess entry
                                        firestore.collection(CollectionRef.messNameDb).add(
                                            newMess
                                        ).addOnSuccessListener {
                                            trySend(
                                                ResultState.Success(
                                                    "Data inserted successfully!"
                                                )
                                            )
                                        }
                                            .addOnFailureListener { e ->
                                                trySend(ResultState.Failure(e))
                                            }

                                    }
                                    .addOnFailureListener { e ->
                                        trySend(ResultState.Failure(e))
                                    }

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

                            val currentUser = firebaseAuth.currentUser?.uid

                            currentUser?.let {
                                //new userinfo entry
                                val ref =
                                    firestore.collection(CollectionRef.userDb).document(currentUser)
                                ref.set(userInfo)
                                    .addOnSuccessListener {
                                        trySend(
                                            ResultState.Success(
                                                "Data inserted successfully!"
                                            )
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        trySend(ResultState.Failure(e))
                                    }
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

    override fun getUserInfo(): Flow<ResultState<User?>> = callbackFlow {
        trySend(ResultState.Loading)
        if (Network.isNetworkAvailable(context)) {
            val currentUser = firebaseAuth.currentUser?.uid
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get().addOnSuccessListener {
                        println(it.data.toString())
                    }
                    .addOnFailureListener {

                    }
            }


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
        val currentUser = firebaseAuth.currentUser?.uid

        if (Network.isNetworkAvailable(context)) {

            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get()
                    .addOnSuccessListener { data ->
                        data?.let {
                            val user = data.toObject(User::class.java)
                            firestore.collection(CollectionRef.mealDb).document(user!!.messName)
                                .collection(monthYear).whereEqualTo("date", meal.date).get()
                                .addOnSuccessListener {
                                    it?.let { result ->
                                        if (result.documents.isNotEmpty()) {
                                            trySend(ResultState.Failure(Exception("Data already exists!")))
                                        } else {
                                            val hashMeal = meal.toMap()

                                            firestore.collection(CollectionRef.mealDb)
                                                .document(user.messName)
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
                                .addOnFailureListener { exp ->
                                    trySend(ResultState.Failure(exp))
                                }
                        }

                    }
                    .addOnFailureListener { }
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
        val currentUser = firebaseAuth.currentUser?.uid


        if (Network.isNetworkAvailable(context)) {
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get()
                    .addOnSuccessListener { data ->
                        data?.let {
                            val user = data.toObject(User::class.java)
                            firestore.collection(CollectionRef.mealDb).document(user!!.messName)
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

                    }
                    .addOnFailureListener { exp ->
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
        val currentUser = firebaseAuth.currentUser?.uid


        if (Network.isNetworkAvailable(context)) {
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get()
                    .addOnSuccessListener { data ->
                        data?.let {
                            val user = data.toObject(User::class.java)
                            firestore.collection(CollectionRef.mealDb).document(user!!.messName)
                                .collection(monthYear).whereEqualTo("date", date)
                                .get().addOnSuccessListener { result ->
                                    result?.let {
                                        if (it.documents.size == 0) {
                                            trySend(ResultState.Failure(Exception("Not found any meal for today!")))
                                        } else {
                                            val mealInformation =
                                                it.documents[0].toObject(MealInfo::class.java)
                                            trySend(ResultState.Success(mealInformation))
                                        }

                                    }

                                }
                                .addOnFailureListener {
                                    trySend(ResultState.Failure(it))
                                }
                        }

                    }
                    .addOnFailureListener { exp ->
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllMeal(date: String): Flow<ResultState<List<MealInfo>>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(date)
        val currentUser = firebaseAuth.currentUser?.uid


        if (Network.isNetworkAvailable(context)) {
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get()
                    .addOnSuccessListener { data ->
                        data?.let {
                            val user = data.toObject(User::class.java)
                            firestore.collection(CollectionRef.mealDb).document(user!!.messName)
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
                                    val formatter: DateTimeFormatter =
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    val sortedMeals =
                                        meals.sortedByDescending {
                                            LocalDate.parse(
                                                it.date,
                                                formatter
                                            )
                                        }

                                    trySend(ResultState.Success(sortedMeals))

                                }.addOnFailureListener {
                                    trySend(ResultState.Failure(it))
                                }
                        }

                    }
                    .addOnFailureListener { exp ->
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
}