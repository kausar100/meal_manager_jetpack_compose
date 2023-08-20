package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class Mess (
    val messName: String = "",
    val messId: String = "",
    val managerContactNumber: String = "",
    val profilePhoto: String = ""
)

fun Mess.toMap(): Map<String, Any?> = Mess::class.memberProperties.associate {
    it.name to it.get(this)
}