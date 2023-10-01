package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class MealInfo(
    val date: String? = "",
    val dayName: String? = "",
    val breakfast: Boolean? = false,
    val lunch: Boolean? = false,
    val dinner: Boolean? = false,
    val cntBreakFast: Int = 0,
    val cntLunch: Int = 0,
    val cntDinner: Int = 0,
)

fun MealInfo.toMap(): Map<String, Any?> = MealInfo::class.memberProperties.associate {
    it.name to it.get(this)
}