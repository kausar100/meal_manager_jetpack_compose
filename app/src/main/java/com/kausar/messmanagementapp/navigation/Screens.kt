package com.kausar.messmanagementapp.navigation

sealed class Screen(val route: String, val argKey: List<String> = emptyList(), val path: String = "", val title: String? = null){
    object Login: Screen(route = "login_screen", title = "Log In")
    object SignUp: Screen(route = "signup_screen", title = "Sign Up")
    object PinVerify: Screen(route = "otp_verification_screen", argKey = listOf("name","phone"), path = "/{name}/{phone}", title = "OTP Verification"){
        fun passNameAndPhoneNumber(name: String, phone: String): String{
            return this.route+"/$name/$phone"
        }
    }
    object Home: Screen(route = "home_screen/", argKey = listOf("user"), path = "/{user}"){
        fun passName(name: String): String{
            return this.route+"/$name"
        }
    }
    object About: Screen(route = "about_screen")
}