package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class User(
    val userName: String = "",
    val contactNo: String = "",
    val userType: String = "",
    val messName: String = "",
    val profilePhoto: String = "",
    val messId: String = "",
    val userId: String = ""
)

fun User.toMap(): Map<String, Any?> = User::class.memberProperties.associate {
    it.name to it.get(this)
}

enum class MemberType {
    Member, Manager
}
