package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class Balance(
    val totalMeal: String = "0.0",
    val totalReceivingAmount: String = "0.0",
    val totalShoppingCost: String = "0.0",
    val remainingAmount: String = "0.0",
    val mealRate: String = "0.0"
)

fun Balance.toMap(): Map<String, Any?> = Balance::class.memberProperties.associate {
    it.name to it.get(this)
}
