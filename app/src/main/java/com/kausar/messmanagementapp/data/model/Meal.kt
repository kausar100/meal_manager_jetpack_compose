package com.kausar.messmanagementapp.data.model

data class RealtimeMealResponse(
    val meal: Meal? = null,
    val key: String? = ""
)

data class Meal(
    val date: String? = "",
    val dayName: String? = "",
    val breakfast: Boolean? = false,
    val lunch: Boolean? = false,
    val dinner: Boolean? = false,
    val status: MealStatus? = MealStatus.Pending
)

val mealListTitle: List<String> = listOf("Date", "Day", "B", "L", "D", "Status")

enum class MealStatus {
    Pending, Running, Completed
}
