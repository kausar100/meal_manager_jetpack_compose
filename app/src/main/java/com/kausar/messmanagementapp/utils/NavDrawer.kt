package com.kausar.messmanagementapp.utils

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kausar.messmanagementapp.data.DrawerParams
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
                        navController.navigate(Screen.Home.passName("Kausar")) {
                            popUpTo(navController.graph.id) {
                                if (navController.previousBackStackEntry != null) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    Screen.About -> {
                        navController.navigate(Screen.About.route) {
                            popUpTo(navController.graph.id) {
                                if (navController.previousBackStackEntry != null) {
                                    inclusive = true
                                }

                            }

                        }
                    }

                    else -> {
                    }
                }
            }
        }) {
        content()
    }
}