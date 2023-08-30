package com.kausar.messmanagementapp.data.model

import com.kausar.messmanagementapp.R


object ShoppingListItem {
    private val itemTitles = listOf("Add Money", "Shop Entry", "Show Money", "Show Shopping List")


    val items = mutableListOf<ShoppingInfo>()

    init {
        repeat(itemTitles.size) {
            val resId = when (it) {
                0 -> R.drawable.ic_money
                1 -> R.drawable.ic_add_shopping
                2 -> R.drawable.ic_account_balance
                3 -> R.drawable.ic_shopping
                else -> 0
            }
            items.add(
                ShoppingInfo(itemTitles[it], resId, itemTitles[it])
            )
        }
    }

}

data class ShoppingInfo(
    val title: String = "",
    val icon: Int = 0,
    val desc: String = ""
)