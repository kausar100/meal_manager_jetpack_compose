package com.kausar.messmanagementapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

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
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object MealList : BottomBarScreen(
        route = "meal_list",
        title = "Meal List",
        icon = Icons.Default.List
    )

    object Profile : BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}