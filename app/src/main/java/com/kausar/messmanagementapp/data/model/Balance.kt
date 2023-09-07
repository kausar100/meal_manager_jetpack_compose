package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class Balance(
    val totalMeal: String = "",
    val totalReceivingAmount: String = "",
    val totalShoppingCost: String = "",
    val remainingAmount: String = "",
)

fun Balance.toMap(): Map<String, Any?> = Balance::class.memberProperties.associate {
    it.name to it.get(this)
}
