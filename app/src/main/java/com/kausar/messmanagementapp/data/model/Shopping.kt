package com.kausar.messmanagementapp.data.model

import com.kausar.messmanagementapp.R


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

val listOfShoppingItem = (1..10).map {
    ShoppingItem(
        name = "item $it",
        unit = "$it$it",
        price = "${it*2}"
    )
}

data class MemberShoppingList(val info: List<Shopping>)

data class Shopping(
    val member: User = User(),
    val date: String = "",
    val itemDetails: List<ShoppingItem> = emptyList(),
    val totalCost: String = ""
)

val listOfShopping = (1..5).map {
    Shopping(
        member = User(userName = "user $it"),
        date = "$it/09/2023",
        itemDetails = listOfShoppingItem,
        totalCost = "2322"
    )
}