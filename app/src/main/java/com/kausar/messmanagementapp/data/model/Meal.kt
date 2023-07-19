package com.kausar.messmanagementapp.data.model

import kotlin.reflect.full.memberProperties

data class RealtimeMealResponse(
    val meal: MealInfo? = null,
    val key: String? = ""
)

data class MealInfo(
    val date: String? = "",
    val dayName: String? = "",
    val breakfast: Boolean? = false,
    val lunch: Boolean? = false,
    val dinner: Boolean? = false,
)

fun MealInfo.toMap(): Map<String, Any?> = MealInfo::class.memberProperties.associate {
    it.name to it.get(this)
}