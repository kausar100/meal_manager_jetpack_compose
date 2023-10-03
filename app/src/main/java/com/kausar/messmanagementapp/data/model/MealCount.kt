package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class MealCount(
    val breakfast: Double = 0.0,
    val lunch: Double = 0.0,
    val dinner: Double = 0.0,
    val total: Double = 0.0
)

fun MealCount.toMealInfo(): MealInfo {
    val breakfast = this.breakfast > 0.0
    val lunch = this.lunch > 0.0
    val dinner = this.dinner > 0.0
    return MealInfo(breakfast = breakfast, lunch = lunch, dinner = dinner, cntBreakFast = this.breakfast, cntLunch = this.lunch, cntDinner = this.dinner )
}

fun MealCount.toMap(): Map<String, Any?> = MealCount::class.memberProperties.associate {
    it.name to it.get(this)
}