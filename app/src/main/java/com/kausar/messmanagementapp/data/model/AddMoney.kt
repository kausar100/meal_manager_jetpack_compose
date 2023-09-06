package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class AddMoney(
    val userName: String = "",
    val date: String = "",
    val amount: String = ""
)

data class AddMoneyWithUser(
    val user : User = User(),
    val info : AddMoney = AddMoney()
)
data class TotalMoneyPerMember(
    val userId: String = "",
    val total: String = ""
)

fun AddMoney.toMap(): Map<String, Any?> = AddMoney::class.memberProperties.associate {
    it.name to it.get(this)
}
