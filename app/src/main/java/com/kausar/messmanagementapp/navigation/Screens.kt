package com.kausar.messmanagementapp.navigation

import com.kausar.messmanagementapp.R

sealed class Screen(
    val route: String, val argKey: String = "", val path: String = "", val title: String? = null
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

    object AddMoney : Screen(route = "add_money_screen", title = "Add Money")
    object ShopEntry : Screen(route = "shop_entry_screen", title = "Shop Entry")
    object Balance : Screen(route = "balance_screen", title = "Account Balance")
    object ShoppingHistory : Screen(route = "shopping_history_screen", title = "Shopping History")
    object ShoppingList : Screen(route = "shopping_info_screen", title = "Shopping Information")

    object Profile : Screen(route = "profile", title = "Profile")
}


sealed class BottomBarScreen(
    val route: String, val title: String, val icon: Int
) {
    object Home : BottomBarScreen(
        route = "home", title = "Home", icon = R.drawable.ic_home
    )

    object MealInfo : BottomBarScreen(
        route = "meal_info", title = "Meal", icon = R.drawable.ic_meal
    )

    object Shopping : BottomBarScreen(
        route = "shop_info", title = "Shopping", icon = R.drawable.ic_shopping
    )

}