package com.kausar.messmanagementapp.data.model

import com.kausar.messmanagementapp.R


object ShoppingListItem {
    val itemTitles = listOf("Add Member Money", "New Shop Entry", "Account Balance", "Shopping History")

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

data class ShoppingItem(
    val name: String = "",
    val weight: String = "",
    val price: String = ""
)