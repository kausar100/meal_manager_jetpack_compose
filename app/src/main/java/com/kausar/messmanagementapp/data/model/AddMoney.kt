package com.kausar.messmanagementapp.data.model

data class AddMoney(
    val userId: String = "",
    val userName: String = "",
    val date: String = "",
    val amount: String = ""
)

val listAddMoney = (1..5).map {
    AddMoney(
        userId = "${it*5}abdicladljdjsjd$it",
        userName = "user ${it+1}",
        date = "$it/09/2023",
        amount = "$it$it"
    )
}
