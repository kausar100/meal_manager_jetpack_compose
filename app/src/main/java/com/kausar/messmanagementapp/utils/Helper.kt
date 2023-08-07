package com.kausar.messmanagementapp.utils

import java.util.Calendar

fun fetchDateAsString(calendar: Calendar): String {
    // Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val dayName = getDayName(calendar)
    return "$dayName, $dayOfMonth/${month + 1}/$year"
}

fun getDate(calendar: Calendar): String {
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val prefixDayOfMonth = if (dayOfMonth < 10) "0" else ""
    val prefixMonth = if (month < 9) "0" else ""
    return "$prefixDayOfMonth$dayOfMonth/$prefixMonth${month + 1}/$year"
}

fun getDayName(calendar: Calendar): String {
    val day = when (calendar[Calendar.DAY_OF_WEEK]) {
        1 -> "SUNDAY"
        2 -> "MONDAY"
        3 -> "TUESDAY"
        4 -> "WEDNESDAY"
        5 -> "THURSDAY"
        6 -> "FRIDAY"
        7 -> "SATURDAY"
        else -> ""
    }
    return day
}

fun fetchCurrentMonthName():String{
    val calendar = Calendar.getInstance()
    val month =  when(calendar[Calendar.MONTH]){
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> ""
    }
    val year = calendar[Calendar.YEAR]
    return "$month, $year"
}