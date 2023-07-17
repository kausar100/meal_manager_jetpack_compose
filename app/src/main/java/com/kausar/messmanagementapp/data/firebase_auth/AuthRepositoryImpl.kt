package com.kausar.messmanagementapp.data.firebase_auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.kausar.messmanagementapp.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private var storedVerificationId: String = ""
//    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun createUserWithPhone(
        phoneNumber: String,
        activity: Activity
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val onVerificationCallback =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationFailed(e: FirebaseException) {
                    trySend(ResultState.Failure(e))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    trySend(ResultState.Success("OTP Sent Successfully!"))
                    storedVerificationId = verificationId
                }

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    TODO("Not yet implemented")
                }
            }

        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+88$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)

        awaitClose { close() }
    }

    override fun signWithCredential(otp: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(ResultState.Success("OTP Verified Successfully!"))
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }

        awaitClose { close() }

    }

    override fun logout(): Flow<ResultState<String>>  = callbackFlow{
        trySend(ResultState.Loading)

        if (firebaseAuth.currentUser!=null){
            firebaseAuth.signOut()
            trySend(ResultState.Success("Successfully log out!"))
        }else{
            trySend(ResultState.Failure(Exception("No user found!")))

        }
        awaitClose { close() }
    }
}
