package com.kausar.messmanagementapp.data

data class Meal(
    val id: Int = 0,
    val date: String = "",
    val dayName: String = "",
    val breakfast: Boolean = false,
    val lunch: Boolean = false,
    val dinner: Boolean = false,
    val status: MealStatus = MealStatus.Pending,
)

val  mealListTitle: List<String>  = listOf("Date","Day", "B","L","D","Status")

enum class MealStatus{
    Pending,Running,Completed
}
