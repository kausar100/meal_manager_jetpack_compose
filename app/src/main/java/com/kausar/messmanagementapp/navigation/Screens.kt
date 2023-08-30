package com.kausar.messmanagementapp.navigation

import com.kausar.messmanagementapp.R

sealed class Screen(
    val route: String,
    val argKey: String = "",
    val path: String = "",
    val title: String? = null
) {
    object Splash : Screen(route = "splash_screen")
    object Login : Screen(route = "login_screen", title = "Log In")
    object SignUp : Screen(route = "signup_screen", title = "Sign Up")
    object PinVerify : Screen(
        route = "otp_verification_screen",
        argKey = "info",
        path = "/{info}",
        title = "OTP Verification"
    ) {
        fun passInfo(data: String): String {
            return this.route + "/$data"
        }
    }
}


sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_home
    )

    object MealInfo : BottomBarScreen(
        route = "meal_info",
        title = "Meal",
        icon = R.drawable.ic_meal
    )

    object Shopping : BottomBarScreen(
        route = "shop_info",
        title = "Shopping",
        icon = R.drawable.ic_shopping
    )

    object Profile : BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = R.drawable.ic_person
    )

}