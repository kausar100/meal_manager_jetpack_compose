package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.SplashScreen
import com.kausar.messmanagementapp.presentation.auth_screen.LoginScreen
import com.kausar.messmanagementapp.presentation.auth_screen.RegistrationScreen
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
            HomeScreen(mainViewModel)
        }
        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(mainViewModel)
        }
        composable(route = BottomBarScreen.MealList.route) {
            MealListScreen(mainViewModel)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(mainViewModel, gotoRegistrationScreen = {
                navController.popBackStack()
                navController.navigate(Screen.SignUp.route)
            }) { info ->
                navController.navigate(Screen.PinVerify.passInfo(info))
            }
        }
        composable(route = Screen.SignUp.route) {
            RegistrationScreen(mainViewModel, gotoLoinScreen = {
                navController.popBackStack()
                navController.navigate(Screen.Login.route)
            }) { info ->
                navController.navigate(Screen.PinVerify.passInfo(info))
            }
        }
        composable(
            route = Screen.PinVerify.route.plus(Screen.PinVerify.path), arguments = listOf(
                navArgument(Screen.PinVerify.argKey) {
                    type = NavType.StringType
                },
            )
        ) {
            val info = it.arguments?.getString(Screen.PinVerify.argKey) ?: ""
            OtpVerifyScreen(info = info, mainViewModel) {
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
        composable(route = Screen.Splash.route) {
            SplashScreen {
                navController.popBackStack()
                if (isLoggedIn) {
                    navController.navigate(BottomBarScreen.Home.route)
                } else
                    navController.navigate(Screen.Login.route)
            }

        }

    }
}
