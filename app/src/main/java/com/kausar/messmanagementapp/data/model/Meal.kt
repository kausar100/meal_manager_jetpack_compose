package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class MealInfo(
    val date: String? = "",
    val dayName: String? = "",
    val breakfast: Boolean? = false,
    val lunch: Boolean? = false,
    val dinner: Boolean? = false,
    val cntBreakFast: Double = 0.0,
    val cntLunch:  Double = 0.0,
    val cntDinner:  Double = 0.0,
)

fun MealInfo.toMealCount() : MealCount{
    val total = (this.cntBreakFast * 0.5) + this.cntLunch + this.cntDinner
    return MealCount(breakfast = this.cntBreakFast, lunch = this.cntLunch, dinner = this.cntDinner, total = total)
}

fun MealInfo.toMap(): Map<String, Any?> = MealInfo::class.memberProperties.associate {
    it.name to it.get(this)
}