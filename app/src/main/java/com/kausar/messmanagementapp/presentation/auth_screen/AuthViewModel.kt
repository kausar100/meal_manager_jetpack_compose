package com.kausar.messmanagementapp.presentation.auth_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.kausar.messmanagementapp.data.firebase_auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun createUserWithPhoneNumber(
        phone: String,
        activity: Activity
    ) = repository.createUserWithPhone(phoneNumber = phone, activity = activity)

    fun resendOtp(
        phone: String,
        activity: Activity
    ) = repository.resendOtp(phoneNumber = phone, activity = activity)

    fun signInWithCredential(code: String) = repository.signWithCredential(code)


    fun logout() = repository.logout()

}