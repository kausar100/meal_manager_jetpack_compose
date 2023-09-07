package com.kausar.messmanagementapp.data.model

import com.kausar.messmanagementapp.R
import kotlin.reflect.full.memberProperties


object ShoppingListItem {
    val itemTitles = listOf("Add Member Money", "New Shop Entry", "Account Balance", "Shopping History")

    val items = mutableListOf<ShoppingScreenListInfo>()

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
                ShoppingScreenListInfo(itemTitles[it], resId, itemTitles[it])
            )
        }
    }

}

data class ShoppingScreenListInfo(
    val title: String = "",
    val icon: Int = 0,
    val desc: String = ""
)

data class ShoppingItem(
    val name: String = "",
    val unit: String = "",
    val price: String = ""
)

data class MemberShoppingList(val info: List<Shopping>, val totalCost: String = "")

data class Shopping(
    val userName: String = "",
    val date: String = "",
    val itemDetails: List<ShoppingItem> = emptyList(),
    val totalCost: String = ""
)

fun Shopping.toMap(): Map<String, Any?> = Shopping::class.memberProperties.associate {
    it.name to it.get(this)
}