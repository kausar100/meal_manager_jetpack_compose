package com.kausar.messmanagementapp.navigation

sealed class Screen(val route: String, val argKey: String = "",val path: String = ""){
    object SignUp: Screen(route = "signup_screen")
    object Login: Screen(route = "login_screen")
    object Home: Screen(route = "home_screen/", argKey = "user", path = "/{user}"){
        fun passName(name: String): String{
            return this.route+"/$name"
        }
    }
    object About: Screen(route = "about_screen")
}