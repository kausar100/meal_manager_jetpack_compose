package com.kausar.messmanagementapp.utils

sealed class ResultState<out T>{
    data class Success<out R>(val data: R) : ResultState<R>()

    data class Failure(val message: Throwable) : ResultState<Nothing>()

    object Loading: ResultState<Nothing>()
}