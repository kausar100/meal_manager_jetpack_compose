package com.kausar.messmanagementapp.navigation

sealed class Screen(val route: String, val argKey: String = "",val path: String = "", val title: String? = null){
    object Login: Screen(route = "login_screen", title = "Log In")
    object SignUp: Screen(route = "signup_screen", title = "Sign Up")
    object PinVerify: Screen(route = "pin_verification_screen", title = "Pin Verification")
    object Home: Screen(route = "home_screen/", argKey = "user", path = "/{user}"){
        fun passName(name: String): String{
            return this.route+"/$name"
        }
    }
    object About: Screen(route = "about_screen")
}