package com.kausar.messmanagementapp.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.data.DrawerParams
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.utils.AppDrawerContent
import kotlinx.coroutines.launch

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.Login.route) {
            AuthScreen { name, phone ->
                navController.navigate(Screen.PinVerify.passNameAndPhoneNumber(name, phone))
            }
        }
        composable(route = Screen.Home.route.plus(Screen.Home.path), arguments = listOf(
            navArgument(Screen.Home.argKey[0]) {
                type = NavType.StringType
            }
        )) {
            val username = it.arguments?.getString(Screen.Home.argKey[0]) ?: ""
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

@Composable
fun MealManagementApp(
    navController: NavHostController = rememberNavController(),
) {
    SetupNavGraph(navController = navController, startDestination = Screen.Login.route)
}