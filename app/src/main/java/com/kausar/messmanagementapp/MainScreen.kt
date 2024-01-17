package com.kausar.messmanagementapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.data.model.MemberType
import com.kausar.messmanagementapp.navigation.BottomBarScreen
import com.kausar.messmanagementapp.navigation.BottomNavGraph
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.navigation.logoutAndNavigateToLoginPage
import com.kausar.messmanagementapp.presentation.auth_screen.AuthViewModel
import com.kausar.messmanagementapp.presentation.shopping_info.ListType
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val firestoreVM: FirebaseFirestoreDbViewModel = hiltViewModel()

    viewModel.getUserInfo()

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    Scaffold(topBar = {
        when (currentRoute(navController)) {
            BottomBarScreen.Home.route, BottomBarScreen.MealInfo.route,
            BottomBarScreen.Shopping.route, Screen.AddMoney.route,
            Screen.ShopEntry.route, Screen.Balance.route, Screen.AddUtilityBill.route,
            Screen.Profile.route, Screen.ShoppingHistory.route,
            BottomBarScreen.Utility.route, Screen.ShoppingList.route -> {
                CustomTopAppBar(title = when (currentRoute(navController)) {

                    Screen.AddMoney.route -> {
                        Screen.AddMoney.title
                    }

                    Screen.AddUtilityBill.route-> {
                        Screen.AddUtilityBill.title
                    }

                    Screen.Profile.route -> {
                        Screen.Profile.title
                    }

                    Screen.ShopEntry.route -> {
                        Screen.ShopEntry.title
                    }

                    Screen.Balance.route -> {
                        Screen.Balance.title
                    }

                    Screen.ShoppingHistory.route -> {
                        Screen.ShoppingHistory.title
                    }

                    Screen.ShoppingList.route -> {
                        Screen.ShoppingList.title
                    }

                    else -> {
                        null
                    }
                },
                    canGoProfileScreen = when (currentRoute(navController)) {
                        BottomBarScreen.Home.route -> {
                            true
                        }

                        BottomBarScreen.MealInfo.route -> {
                            true
                        }

                        BottomBarScreen.Shopping.route -> {
                            true
                        }

                        BottomBarScreen.Utility.route -> {
                            true
                        }

                        else -> {
                            false
                        }

                    },
                    gotoProfileScreen = {
                        navController.navigate(Screen.Profile.route)
                    },
                    showAction = when (currentRoute(navController)) {
                        BottomBarScreen.Shopping.route -> {
                            viewModel.userInfo.value.userType == MemberType.Manager.name
                        }

                        else -> {
                            false
                        }
                    },
                    actionIcon = if (viewModel.listType.value == ListType.List) R.drawable.ic_grid else R.drawable.ic_list,
                    onClickAction = {
                        viewModel.toggleList()
                    },
                    canLogout = when (currentRoute(navController)) {
                        Screen.Profile.route -> {
                            true
                        }

                        else -> {
                            false
                        }
                    },
                    canNavigateBack = when (currentRoute(navController)) {
                        Screen.AddMoney.route, Screen.ShopEntry.route, Screen.AddUtilityBill.route, Screen.Balance.route, Screen.ShoppingHistory.route, Screen.ShoppingList.route, Screen.Profile.route -> {
                            true
                        }

                        else -> {
                            false
                        }
                    },
                    logoutAction = {
                        authViewModel.logout()
                        viewModel.saveLoginStatus(false)
                        logoutAndNavigateToLoginPage(navController)
                    },
                    scrollBehavior = scrollBehavior,
                    navigateUp = {
                        navController.popBackStack()
                    })
            }
        }
    }, bottomBar = {
        when (currentRoute(navController)) {
            BottomBarScreen.Home.route, BottomBarScreen.MealInfo.route, BottomBarScreen.Shopping.route, BottomBarScreen.Utility.route  -> {
                BottomBar(navController = navController)
            }
        }
    }, snackbarHost = {
        if (isConnected.not()) {
            Snackbar(action = {}, modifier = Modifier.padding(8.dp)) {
                Text(text = "There is no internet")
            }
        }
    }) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.padding(
                top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()
            )
        ) {
            BottomNavGraph(
                navController = navController,
                mainViewModel = viewModel,
                firestoreViewModel = firestoreVM,
                authVM = authViewModel,
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

    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.MealInfo,
        BottomBarScreen.Utility,
        BottomBarScreen.Shopping,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
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
    NavigationBarItem(selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true, onClick = {
        currentDestination?.let {
            if (currentDestination.route != screen.route) {
                navController.navigate(screen.route) {
                    popUpTo(BottomBarScreen.Home.route)
                    launchSingleTop = true
                }
            }
        } ?: navController.navigate(screen.route) {
            popUpTo(BottomBarScreen.Home.route)
            launchSingleTop = true
        }
    }, label = {
        Text(text = screen.title)
    }, icon = {
        Icon(
            painter = painterResource(id = screen.icon), contentDescription = "Navigation Icon"
        )
    })
}