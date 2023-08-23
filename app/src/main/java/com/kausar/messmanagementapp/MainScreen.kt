package com.kausar.messmanagementapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.navigation.BottomBarScreen
import com.kausar.messmanagementapp.navigation.BottomNavGraph
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.navigation.logoutAndNavigateToLoginPage
import com.kausar.messmanagementapp.presentation.auth_screen.AuthViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val isLogin = viewModel.isLoggedIn.value

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    Scaffold(
        topBar = {
            when (currentRoute(navController)) {
                BottomBarScreen.Home.route,
                BottomBarScreen.MealList.route,
                BottomBarScreen.Profile.route -> {
                    CustomTopAppBar(
                        title = when (currentRoute(navController)) {
                            BottomBarScreen.Home.route -> {
                                BottomBarScreen.Home.title
                            }

                            BottomBarScreen.MealList.route -> {
                                BottomBarScreen.MealList.title
                            }

                            BottomBarScreen.Profile.route -> {
                                BottomBarScreen.Profile.title
                            }

                            else -> {
                                null
                            }
                        },
                        canLogout = when (currentRoute(navController)) {
                            BottomBarScreen.Home.route -> {
                                true
                            }
                            else -> {
                                false
                            }
                        },
                        canNavigateBack = false, logoutAction = {
                            authViewModel.logout()
                            viewModel.saveLoginStatus(false)
                            logoutAndNavigateToLoginPage(navController)
                        }, scrollBehavior = scrollBehavior
                    )
                }
            }
        },
        bottomBar = {
            when (currentRoute(navController)) {
                BottomBarScreen.Home.route, BottomBarScreen.MealList.route, BottomBarScreen.Profile.route -> {
                    BottomBar(navController = navController)
                }
            }
        },
        snackbarHost = {
            if (isConnected.not()) {
                Snackbar(action = {}, modifier = Modifier.padding(8.dp)) {
                    Text(text = "There is no internet")
                }
            }
        }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(
                bottom = it.calculateBottomPadding()
            )
        ) {
            BottomNavGraph(
                navController = navController,
                mainViewModel = viewModel,
                isLoggedIn = isLogin,
                startDestination = Screen.Splash.route
            )
        }

    }


}


@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route?.substringBeforeLast("/")
}

@Composable
fun BottomBar(navController: NavHostController) {

    val screens = mutableListOf(
        BottomBarScreen.Home, BottomBarScreen.MealList, BottomBarScreen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar() {
        screens.forEach { screen ->
            AddDestination(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }

}

@Composable
fun RowScope.AddDestination(
    screen: BottomBarScreen, currentDestination: NavDestination?, navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(BottomBarScreen.Home.route)
                launchSingleTop = true
            }
        },
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon, contentDescription = "Navigation Icon"
            )
        },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = LocalContentColor.current.copy(alpha = .3f),
        ),

        )
}