package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.SplashScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.meal_info_list.MealListScreen
import com.kausar.messmanagementapp.presentation.profile_screen.ProfileScreen
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

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
fun BottomNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    isLoggedIn: Boolean,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = BottomBarScreen.Home.route) {

            HomeScreen {
                mainViewModel.saveLoginStatus(false)
                logoutAndNavigateToLoginPage(navController)
            }


        }
        composable(route = BottomBarScreen.Profile.route) {

            ProfileScreen {
                mainViewModel.saveLoginStatus(false)
                logoutAndNavigateToLoginPage(navController)
            }

        }
        composable(route = BottomBarScreen.MealList.route) {

            MealListScreen {
                mainViewModel.saveLoginStatus(false)
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
                mainViewModel.saveLoginStatus(true)
                navController.popBackStack()
                navController.navigate(BottomBarScreen.Home.route) {
                    popUpTo(navController.graph.id) {
                        if (navController.previousBackStackEntry != null) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        composable(route = Screen.About.route) {
            AboutScreen(onLogout = {
                mainViewModel.saveLoginStatus(false)
                logoutAndNavigateToLoginPage(navController)
            })

        }

        composable(route = Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.popBackStack()
                if (isLoggedIn) {
                    navController.navigate(BottomBarScreen.Home.route)
                } else
                    navController.navigate(Screen.Login.route)
            })

        }

    }
}
