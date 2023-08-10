package com.kausar.messmanagementapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val argKey: List<String> = emptyList(),
    val path: String = "",
    val title: String? = null
) {
    object Splash : Screen(route = "splash_screen")
    object Login : Screen(route = "login_screen", title = "Log In")
    object SignUp : Screen(route = "signup_screen", title = "Sign Up")
    object PinVerify : Screen(
        route = "otp_verification_screen",
        argKey = listOf("phone"),
        path = "/{phone}",
        title = "OTP Verification"
    ) {
        fun phoneNumber(phone: String): String {
            return this.route + "/$phone"
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