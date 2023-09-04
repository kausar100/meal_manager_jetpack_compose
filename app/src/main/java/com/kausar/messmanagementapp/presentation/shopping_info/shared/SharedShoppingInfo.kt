package com.kausar.messmanagementapp.presentation.shopping_info.shared

import com.kausar.messmanagementapp.data.model.User

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
}