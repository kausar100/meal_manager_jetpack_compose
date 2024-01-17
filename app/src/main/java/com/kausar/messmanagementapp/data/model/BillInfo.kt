package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class BillInfo(
    val billName: String = "",
    val billCost: String = ""
)

fun BillInfo.toMap(): Map<String, Any?> = BillInfo::class.memberProperties.associate {
    it.name to it.get(this)
}