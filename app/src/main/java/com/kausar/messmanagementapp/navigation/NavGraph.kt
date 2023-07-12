package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            AuthScreen { name,phone->
                navController.navigate(Screen.PinVerify.passNameAndPhoneNumber(name,phone))
            }
        }
        composable(route = Screen.Home.route.plus(Screen.Home.path), arguments = listOf(
            navArgument(Screen.Home.argKey[0]) {
                type = NavType.StringType
            }
        )) {
            val username = it.arguments?.getString(Screen.Home.argKey[0]) ?:""
            HomeScreen(userName = username)
        }

        composable(route = Screen.PinVerify.route.plus(Screen.PinVerify.path), arguments = listOf(
            navArgument(Screen.PinVerify.argKey[0]) {
                type = NavType.StringType
            },
            navArgument(Screen.PinVerify.argKey[1]) {
                type = NavType.StringType
            }
        )) {
            val username = it.arguments?.getString(Screen.PinVerify.argKey[0]) ?: ""
            val phone = it.arguments?.getString(Screen.PinVerify.argKey[1]) ?: ""
            OtpVerifyScreen(userName = username, phoneNumber = phone) {
                navController.popBackStack()
                navController.navigate(Screen.Home.passName(username))
            }
        }

        composable(route = Screen.About.route) {
            AboutScreen()
        }
    }
}