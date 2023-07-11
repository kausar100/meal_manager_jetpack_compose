package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.login_screen.LoginScreen
import com.kausar.messmanagementapp.presentation.signup_screen.SignUpScreen

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    NavHost(navController = navController, startDestination = Screen.About.route) {
        composable(route = Screen.SignUp.route) {
            SignUpScreen(gotoLoginScreen = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.SignUp.route) {
                        inclusive = true
                    }
                }
            })
        }

        composable(route = Screen.Login.route) {
            LoginScreen(gotoSignupScreen = {
                navController.navigate(Screen.SignUp.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }, onLogin = {
                navController.popBackStack()
                navController.navigate(Screen.Home.passName(it))
            })
        }

        composable(route = Screen.Home.route.plus(Screen.Home.path), arguments = listOf(
            navArgument(Screen.Home.argKey){
                type = NavType.StringType
            }
        )) {
            val username = it.arguments?.getString(Screen.Home.argKey)
            HomeScreen(username)
        }

        composable(route = Screen.About.route) {
            AboutScreen()
        }
    }
}