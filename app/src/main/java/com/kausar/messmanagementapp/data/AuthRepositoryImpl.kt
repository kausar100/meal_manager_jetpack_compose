package com.kausar.messmanagementapp.data

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.kausar.messmanagementapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class Result {
    Success, Error, Loading
}

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private var resultStatus: Result = Result.Loading
    private lateinit var onSuccess: AuthResult
    private var onError: String = ""
    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override suspend fun loginUser(
        verificationId: String,
        code: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("msg", it.result.toString())
                    resultStatus = Result.Success
                    onSuccess = it.result
                } else {
                    resultStatus = Result.Error
                    onError = it.exception.toString()
                }
            }
            when (resultStatus.name) {
                Result.Success.name -> {
                    emit(Resource.Success(data = onSuccess))
                }

                Result.Error.name -> {
                    emit(Resource.Error(onError))
                }
                Result.Loading.name ->{
                    emit(Resource.Success(message = storedVerificationId))
                }
            }
        }
    }

    override suspend fun registerUser(
        activity: Activity,
        username: String,
        phoneNumber: String
    ): Flow<Resource<AuthResult>> {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("msg", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(activity,credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("msg", "onVerificationFailed", e)
                resultStatus = Result.Error
                onError = e.message.toString()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("msg", "onCodeSent:$verificationId")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }

        }
        return flow {
            emit(Resource.Loading())

            val option = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(option)

            when (resultStatus.name) {
                Result.Success.name -> {
                    emit(Resource.Success(data = onSuccess))
                }

                Result.Error.name -> {
                    emit(Resource.Error(onError))
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(activity: Activity, credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("msg", "signInWithCredential:success")
                    resultStatus = Result.Success
                    onSuccess = task.result
                    val user = task.result?.user
                    Log.d("msg", "signInWithCredential:success User ${user.toString()}")
                } else {
                    resultStatus = Result.Error
                    onError = task.exception.toString()
                    Log.w("msg", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    override suspend fun logout(): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.signOut()
                emit(Resource.Success(message = "Log out successfully!"))

            } else {
                emit(Resource.Error(message = "Could not log out for some reason!"))
            }
        }
    }
}
