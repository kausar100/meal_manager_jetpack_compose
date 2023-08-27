package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class MealCount(
    val breakfast: Double = 0.0,
    val lunch: Double = 0.0,
    val dinner: Double = 0.0,
    val total: Double = 0.0
)

fun MealCount.toMap(): Map<String, Any?> = MealCount::class.memberProperties.associate {
    it.name to it.get(this)
}