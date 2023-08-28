package com.kausar.messmanagementapp.data.firebase_firestore

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kausar.messmanagementapp.data.model.MealCount
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.MemberType
import com.kausar.messmanagementapp.data.model.Mess
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.model.toMap
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.network_connection.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

class FirebaseFirestoreRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) : FirebaseFirestoreRepo {

    private var messMembers = listOf<User>()

    object CollectionRef {
        const val userDb = "UserInfo"
        const val mealDb = "MealInfo"
        const val messNameDb = "MessNameInfo"
    }

    private fun getMonthYear(date: String): String {
        date.isNotEmpty().let {
            val month = date.substring(startIndex = 3, endIndex = date.length)
            return month.replace("/", "_")
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
                            val currentUser = firebaseAuth.currentUser?.uid

                            currentUser?.let {
                                //new userinfo entry
                                val userInfo = user.copy(messId = currentUser, userId = currentUser)
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
                                                    "Registration completed successfully!"
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

                            val currentUser = firebaseAuth.currentUser?.uid

                            currentUser?.let {
                                //new userinfo entry
                                val userInfo =
                                    user.copy(messId = mess!!.messId, userId = currentUser).toMap()

                                val ref =
                                    firestore.collection(CollectionRef.userDb).document(currentUser)
                                ref.set(userInfo)
                                    .addOnSuccessListener {
                                        trySend(
                                            ResultState.Success(
                                                "Registration completed successfully!"
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

    override fun getCurrentUserInfo(): Flow<ResultState<User?>> = callbackFlow {
        trySend(ResultState.Loading)
        if (Network.isNetworkAvailable(context)) {

            val currentUser = firebaseAuth.currentUser?.uid
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get().addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        user?.let {
                            trySend(ResultState.Success(user))
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

    override fun getMessNames(): Flow<ResultState<List<Mess>>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            firestore.collection(CollectionRef.messNameDb).get()
                .addOnSuccessListener { result ->
                    val lisOfMess = mutableListOf<Mess>()
                    for (document in result.documents) {
                        val mess = document.toObject(Mess::class.java)
                        if (mess != null) {
                            lisOfMess.add(mess)
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

    private val calender: Calendar
        get() = Calendar.getInstance()

    private val today: String
        get() = getDate(calender)

    private val getMonthYear: String
        get() = getMonthYear(today)

    override fun getSingleMealCount(): Flow<ResultState<MealCount>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            val currentUser = firebaseAuth.currentUser?.uid
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get().addOnSuccessListener {
                        if (it.exists()) {
                            val user = it.toObject(User::class.java)
                            user?.let {
                                firestore.collection(CollectionRef.mealDb)
                                    .document(user.messName)
                                    .collection(user.messId)
                                    .document(user.userId)
                                    .collection(getMonthYear)
                                    .document(user.userId)
                                    .get()
                                    .addOnSuccessListener { cnt ->
                                        cnt?.let {
                                            val count = cnt.toObject(MealCount::class.java)
                                            count?.let {
                                                Log.d("getSingleMealCount: ", "success")
                                                trySend(ResultState.Success(count))
                                            }

                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("getSingleMealCount: ", "failed")
                                        trySend(ResultState.Failure(Exception(e)))
                                    }
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
    override fun getMessMembers(): Flow<ResultState<List<User>?>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            val currentUser = firebaseAuth.currentUser?.uid
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get().addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        user?.let {
                            firestore.collection(CollectionRef.userDb)
                                .whereEqualTo("messId", user.messId)
                                .get()
                                .addOnSuccessListener { res ->
                                    val listOfMessMembers = mutableListOf<User>()
                                    for (info in res.documents) {
                                        val data = info.toObject(User::class.java)
                                        data?.let {
                                            listOfMessMembers.add(data)
                                        }
                                    }
                                    messMembers = listOfMessMembers
                                    Log.d("getMessMembers: ", messMembers.toString())
                                    trySend(ResultState.Success(listOfMessMembers))
                                }
                                .addOnFailureListener { exp ->
                                    trySend(ResultState.Failure(exp))
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

    private fun needToCount(currentMealDate: String): Boolean {
        val day = today.substring(0, 2)
        val currDay = currentMealDate.substring(0, 2)
        //current meal date in behind today
        return day.toInt() > currDay.toInt()
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun updateSingleMealCount(
//        monthYear: String
//    ): Flow<ResultState<String>> =
//        callbackFlow {
//            var cntBreakfast = 0.0
//            var cntLunch = 0.0
//            var cntDinner = 0.0
//
////            getUserMealByMonth(currentUser).collectLatest { response ->
////                when (response) {
////                    is ResultState.Success -> {
////                        if (response.data.isNotEmpty()) {
////                            for (meal in response.data) {
////                                if (needToCount(meal.date!!)) {
////                                    if (meal.breakfast!!) {
////                                        cntBreakfast += 1.0
////                                    }
////                                    if (meal.lunch!!) {
////                                        cntLunch += 1.0
////                                    }
////                                    if (meal.dinner!!) {
////                                        cntDinner += 1.0
////                                    }
////                                }
////                            }
////                            val cntTotal = cntBreakfast * 0.5 + cntLunch * 1.0 + cntDinner * 1.0
////
////                            val cnt = MealCount(
////                                breakfast = cntBreakfast,
////                                lunch = cntLunch,
////                                dinner = cntDinner,
////                                total = cntTotal
////                            )
////
////                            firestore.collection(CollectionRef.mealDb)
////                                .document(member.messName)
////                                .collection(member.messId)
////                                .document(member.userId)
////                                .collection(monthYear)
////                                .document(member.userId)
////                                .set(cnt.toMap())
////                                .addOnSuccessListener {
////                                    Log.d("updateMealCount: ", "success")
////                                    trySend(
////                                        ResultState.Success(
////                                            "Data inserted successfully!"
////                                        )
////                                    )
////
////                                }
////                                .addOnFailureListener { e ->
////                                    Log.d("updateMealCount: ", "failed")
////                                    trySend(ResultState.Failure(Exception(e)))
////                                }
////
////                        } else {
////                            trySend(ResultState.Failure(Exception("No meal history found!")))
////                        }
////                    }
////
////                    is ResultState.Failure -> {
////                        trySend(ResultState.Failure(Exception("No meal history found!")))
////                    }
////
////                    else -> {
////                        Log.d("updateMealCount: ", "loading")
////                    }
////                }
////            }
//
//        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun countSingleMeal(
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(today)
        val currentUser = firebaseAuth.currentUser?.uid

        var cntBreakfast = 0.0
        var cntLunch = 0.0
        var cntDinner = 0.0

        if (Network.isNetworkAvailable(context)) {
            currentUser?.let {
                getCurrentUserInfo().collectLatest { res ->
                    when (res) {
                        is ResultState.Success -> {
                            res.data?.let { user ->
                                getUserMealByMonth(currentUser).collectLatest { response ->
                                    when (response) {
                                        is ResultState.Success -> {
                                            if (response.data.isNotEmpty()) {
                                                for (meal in response.data) {
                                                    if (needToCount(meal.date!!)) {
                                                        if (meal.breakfast!!) {
                                                            cntBreakfast += 1.0
                                                        }
                                                        if (meal.lunch!!) {
                                                            cntLunch += 1.0
                                                        }
                                                        if (meal.dinner!!) {
                                                            cntDinner += 1.0
                                                        }
                                                    }
                                                }
                                                val cntTotal =
                                                    cntBreakfast * 0.5 + cntLunch * 1.0 + cntDinner * 1.0

                                                val cnt = MealCount(
                                                    breakfast = cntBreakfast,
                                                    lunch = cntLunch,
                                                    dinner = cntDinner,
                                                    total = cntTotal
                                                )

                                                firestore.collection(CollectionRef.mealDb)
                                                    .document(user.messName)
                                                    .collection(user.messId)
                                                    .document(user.userId)
                                                    .collection(monthYear)
                                                    .document(user.userId)
                                                    .set(cnt.toMap())
                                                    .addOnSuccessListener {
                                                        Log.d("updateMealCount: ", "success")
                                                        trySend(
                                                            ResultState.Success(
                                                                "Data inserted successfully!"
                                                            )
                                                        )

                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.d("updateMealCount: ", "failed")
                                                        trySend(ResultState.Failure(Exception(e)))
                                                    }

                                            } else {
                                                trySend(ResultState.Failure(Exception("No meal history found!")))
                                            }
                                        }

                                        is ResultState.Failure -> {
                                            trySend(ResultState.Failure(Exception("No meal history found!")))
                                        }

                                        else -> {

                                        }
                                    }
                                }
                            }
                        }

                        is ResultState.Failure -> {
                            trySend(ResultState.Failure(Exception("User information not found!")))

                        }

                        else -> {

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


    override fun insertCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>> =
        callbackFlow {
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
                                user?.let {
                                    firestore.collection(CollectionRef.mealDb)
                                        .document(user.messName)
                                        .collection(user.messId).document(currentUser)
                                        .collection(monthYear).whereEqualTo("date", meal.date).get()
                                        .addOnSuccessListener {
                                            it?.let { result ->
                                                if (result.documents.isNotEmpty()) {
                                                    trySend(ResultState.Failure(Exception("Data already exists!")))
                                                } else {
                                                    val hashMeal = meal.toMap()

                                                    val ref =
                                                        firestore.collection(CollectionRef.mealDb)
                                                            .document(user.messName)
                                                            .collection(user.messId)
                                                            .document(currentUser)
                                                            .collection(monthYear)

                                                    ref.add(hashMeal)
                                                        .addOnSuccessListener { rst ->
                                                            if (rst?.id != null) {
                                                                trySend(
                                                                    ResultState.Success(
                                                                        "Meal info inserted successfully!"
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

    override fun updateCurrentUserMeal(meal: MealInfo): Flow<ResultState<String>> = callbackFlow {
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
                            user?.let {
                                firestore.collection(CollectionRef.mealDb).document(user.messName)
                                    .collection(user.messId).document(currentUser)
                                    .collection(monthYear).whereEqualTo("date", meal.date)
                                    .get().addOnSuccessListener {
                                        it?.let { result ->
                                            if (result.documents.isNotEmpty()) {
                                                val doc = result.documents[0]
                                                val hashMeal = meal.toMap()
                                                doc.reference.update(hashMeal)
                                                    .addOnSuccessListener {
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

    override fun getAppUser(): Flow<ResultState<List<User>?>> = callbackFlow {
        trySend(ResultState.Loading)

        if (Network.isNetworkAvailable(context)) {
            firestore.collection(CollectionRef.userDb).get()
                .addOnSuccessListener { result ->
                    val lisOfUser = mutableListOf<User>()
                    for (document in result.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            lisOfUser.add(user)
                        }
                    }
                    trySend(ResultState.Success(lisOfUser))

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

    override fun getCurrentUserMealByDay(date: String): Flow<ResultState<MealInfo?>> =
        callbackFlow {
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
                                user?.let {
                                    firestore.collection(CollectionRef.mealDb)
                                        .document(user.messName)
                                        .collection(user.messId).document(currentUser)
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
    override fun getUserMealByMonth(
        currentUser: String?
    ): Flow<ResultState<List<MealInfo>>> = callbackFlow {
        trySend(ResultState.Loading)

        val monthYear = getMonthYear(today)

        if (Network.isNetworkAvailable(context)) {
            currentUser?.let {
                firestore.collection(CollectionRef.userDb).document(currentUser)
                    .get()
                    .addOnSuccessListener { data ->
                        data?.let {
                            val user = data.toObject(User::class.java)
                            user?.let {
                                firestore.collection(CollectionRef.mealDb).document(user.messName)
                                    .collection(user.messId).document(currentUser)
                                    .collection(monthYear).get()
                                    .addOnSuccessListener { result ->
                                        val meals = mutableListOf<MealInfo>()
                                        for (document in result.documents) {
                                            document?.data.let { data ->
                                                data?.let {
                                                    //meal info
                                                    if (it.containsKey("date")) {
                                                        val meal =
                                                            document.toObject(MealInfo::class.java)
                                                        if (meal != null) {
                                                            meals.add(meal)
                                                        }
                                                    }
                                                }

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