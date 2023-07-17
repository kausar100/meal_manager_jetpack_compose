package com.kausar.messmanagementapp.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen
import com.kausar.messmanagementapp.presentation.default_meal_setup_screen.DefaultMealInfo
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.meal_info_list.MealListScreen
import com.kausar.messmanagementapp.presentation.profile_screen.ProfileScreen
import com.kausar.messmanagementapp.components.NavigationDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    val coroutineScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var toggle by rememberSaveable {
        mutableStateOf(false)
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.Login.route) {
            AuthScreen { phone, _ ->
                navController.navigate(Screen.PinVerify.phoneNumber(phone))
            }
        }
        composable(route = Screen.Home.route) {
            NavigationDrawer(navController = navController,
                drawerState = drawerState,
                currentScreen = Screen.Home.title!!,
                needToggle = toggle,
                toggleDrawerState = {
                    toggle = !toggle
                    changeDrawerState(drawerState = drawerState, scope = coroutineScope)
                }) {
                HomeScreen(navigateToProfileScreen = {
                    navController.navigate(Screen.Profile.route)
                }) {
                    toggle = true
                }

            }
        }


        composable(route = Screen.PinVerify.route.plus(Screen.PinVerify.path), arguments = listOf(
            navArgument(Screen.PinVerify.argKey[0]) {
                type = NavType.StringType
            },
        )) {
            val phone = it.arguments?.getString(Screen.PinVerify.argKey[0]) ?: ""
            OtpVerifyScreen(phoneNumber = phone) {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        }

        composable(route = Screen.About.route) {
            NavigationDrawer(navController = navController,
                drawerState = drawerState,
                currentScreen = Screen.About.title!!,
                needToggle = toggle,
                toggleDrawerState = {
                    toggle = !toggle
                    changeDrawerState(drawerState = drawerState, scope = coroutineScope)
                }) {
                AboutScreen {
                    toggle = true
                }

            }

        }

        composable(route = Screen.DefaultMealSetup.route) {
            NavigationDrawer(navController = navController,
                drawerState = drawerState,
                currentScreen = Screen.DefaultMealSetup.title!!,
                needToggle = toggle,
                toggleDrawerState = {
                    toggle = !toggle
                    changeDrawerState(drawerState = drawerState, scope = coroutineScope)
                }) {
                DefaultMealInfo {
                    toggle = true
                }

            }

        }

        composable(route = Screen.MealList.route) {
            NavigationDrawer(navController = navController,
                drawerState = drawerState,
                currentScreen = Screen.MealList.title!!,
                needToggle = toggle,
                toggleDrawerState = {
                    toggle = !toggle
                    changeDrawerState(drawerState = drawerState, scope = coroutineScope)
                }) {
                MealListScreen(toggleDrawerState = { toggle = true })
            }

        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(onSubmit = {}) {
                navController.navigateUp()
            }
        }
    }
}

@Composable
fun MealManagementApp(
    navController: NavHostController = rememberNavController(),
) {
    SetupNavGraph(navController = navController, startDestination = Screen.Login.route)
}

@OptIn(ExperimentalMaterial3Api::class)
fun changeDrawerState(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        if (drawerState.isOpen) {
            drawerState.close()
        } else if (drawerState.isClosed) {
            drawerState.open()
        }
    }

}
