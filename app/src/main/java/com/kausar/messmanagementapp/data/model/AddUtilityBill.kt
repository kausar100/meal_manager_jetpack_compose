package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties
data class AddUtilityBill(
    val userName: String = "",
    val date: String = "",
    val billInfo: List<BillInfo> = emptyList()
)

data class AddUtilityBillWithUser(
    val user : User = User(),
    val info : AddUtilityBill = AddUtilityBill()
)

fun AddUtilityBill.toMap(): Map<String, Any?> = AddUtilityBill::class.memberProperties.associate {
    it.name to it.get(this)
}