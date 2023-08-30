package com.kausar.messmanagementapp.data.model

data class AddMoneyModel(
    val userId: String = "",
    val userName: String = "",
    val date: String = "",
    val amount: String = ""
)

object Demo {
    val listOfTestAddMoneyModel = mutableListOf<AddMoneyModel>()

    init {
        repeat(20) {
            listOfTestAddMoneyModel.add(
                AddMoneyModel("user id $it", "user$it", "$it/08/2023", "$it,00")
            )
        }
    }
}

