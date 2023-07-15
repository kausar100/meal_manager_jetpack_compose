package com.kausar.messmanagementapp.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(message: String? = null, data: T? = null) : Resource<T>(data,message)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

sealed class ResultState<out T>{
    data class Success<out R>(val data: R) : ResultState<R>()

    data class Failure(val message: Throwable) : ResultState<Nothing>()

    object Loading: ResultState<Nothing>()
}