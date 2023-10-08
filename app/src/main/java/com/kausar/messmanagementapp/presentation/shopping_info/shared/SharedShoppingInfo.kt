package com.kausar.messmanagementapp.presentation.shopping_info.shared

import com.kausar.messmanagementapp.data.model.User
import java.math.RoundingMode
import java.text.DecimalFormat

object SharedShoppingInfo{
    fun getNames(members: List<User>): List<String> {
        val names = mutableListOf<String>()
        if (members.isNotEmpty()) {
            for (member in members) {
                names.add(member.userName)
            }
        }
        return names

    }

    fun getUser(members: List<User>, name: String): User {
        var selectedMember = User()
        for (member in members) {
            if (member.userName == name) {
                selectedMember = member
                break
            }
        }
        return selectedMember
    }
    fun getCostPerMeal(totalMeal: String = "0.0", totalShoppingCost: String = "0.0"): String {
        var cost: String
        try {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            cost =
                df.format(totalShoppingCost.ifEmpty { "0.0" }.toDouble() / totalMeal.ifEmpty { "0.0" }
                    .toDouble())
        } catch (e: Exception) {
            cost = "0.0"
        }
        return cost
    }

    fun getMealCost(numberOfMeal: String = "0.0", mealRate: String = "0.0", otherCost: String = "0.0"): String {
        var cost: String
        try {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            cost = df.format((numberOfMeal.toDouble()*mealRate.toDouble())+otherCost.toDouble())
        } catch (e: Exception) {
            cost = "0.0"
        }
        return cost
    }
}