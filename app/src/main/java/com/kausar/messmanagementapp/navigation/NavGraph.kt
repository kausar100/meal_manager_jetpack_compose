package com.kausar.messmanagementapp.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.meal_info_list.MealListScreen
import com.kausar.messmanagementapp.presentation.profile_screen.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun logoutAndNavigateToLoginPage(navController: NavController) {
    navController.popBackStack()
    navController.navigate(Screen.Login.route) {
        popUpTo(navController.graph.id) {
            if (navController.previousBackStackEntry != null) {
                inclusive = true
            }
        }
    }

}

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen{
                logoutAndNavigateToLoginPage(navController)
            }
        }
        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen {
                logoutAndNavigateToLoginPage(navController)
            }
        }
        composable(route = BottomBarScreen.MealList.route) {
            MealListScreen {
                logoutAndNavigateToLoginPage(navController)
            }
        }
        composable(route = Screen.Login.route) {
            AuthScreen { phone, _ ->
                navController.navigate(Screen.PinVerify.phoneNumber(phone))
            }
        }
        composable(
            route = Screen.PinVerify.route.plus(Screen.PinVerify.path), arguments = listOf(
                navArgument(Screen.PinVerify.argKey[0]) {
                    type = NavType.StringType
                },
            )
        ) {
            val phone = it.arguments?.getString(Screen.PinVerify.argKey[0]) ?: ""
            OtpVerifyScreen(phoneNumber = phone) {
                navController.popBackStack()
                navController.navigate(BottomBarScreen.Home.route)
            }
        }

        composable(route = Screen.About.route) {
            AboutScreen(onLogout = {
                logoutAndNavigateToLoginPage(navController)
            })

        }

    }
}
