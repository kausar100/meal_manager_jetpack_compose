package com.kausar.messmanagementapp.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kausar.messmanagementapp.data.shared.DrawerParams
import com.kausar.messmanagementapp.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    navController: NavController,
    drawerState: DrawerState,
    currentScreen: String,
    needToggle: Boolean = false,
    toggleDrawerState: () -> Unit,
    content: @Composable () -> Unit
) {
    if (needToggle) {
        toggleDrawerState()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                menuItems = DrawerParams.drawerButtons,
                currentScreenName = currentScreen,
                toggleDrawer = toggleDrawerState
            ) { onUserPickedOption ->
                when (onUserPickedOption) {
                    Screen.Home -> {
                        navigateToDrawerItem(Screen.Home.passName("kausar"), navController)
                    }
                    Screen.About -> {
                        navigateToDrawerItem(Screen.About.route, navController)

                    }

                    Screen.DefaultMealSetup -> {
                        navigateToDrawerItem(Screen.DefaultMealSetup.route, navController)

                    }

                    Screen.MealList -> {
                        navigateToDrawerItem(Screen.MealList.route, navController)
                    }

                    else -> {
                    }
                }
            }
        }) {
        content()
    }
}

fun navigateToDrawerItem(screen: String, navController: NavController){
    navController.navigate(screen) {
        popUpTo(navController.graph.id) {
            if (navController.previousBackStackEntry != null) {
                inclusive = true
            }
        }
    }
}