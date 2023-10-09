package com.kausar.messmanagementapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.data.model.MemberType
import com.kausar.messmanagementapp.presentation.SplashScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthViewModel
import com.kausar.messmanagementapp.presentation.auth_screen.LoginScreen
import com.kausar.messmanagementapp.presentation.auth_screen.OtpVerifyScreen
import com.kausar.messmanagementapp.presentation.auth_screen.RegistrationScreen
import com.kausar.messmanagementapp.presentation.home_screen.SharedHomeScreen
import com.kausar.messmanagementapp.presentation.home_screen.TabScreen
import com.kausar.messmanagementapp.presentation.meal_info_list.MealInfoScreen
import com.kausar.messmanagementapp.presentation.meal_info_list.MealListScreen
import com.kausar.messmanagementapp.presentation.profile_screen.ProfileScreen
import com.kausar.messmanagementapp.presentation.shopping_info.AccountBalance
import com.kausar.messmanagementapp.presentation.shopping_info.AddMoney
import com.kausar.messmanagementapp.presentation.shopping_info.NewShopEntry
import com.kausar.messmanagementapp.presentation.shopping_info.ShoppingHistory
import com.kausar.messmanagementapp.presentation.shopping_info.ShoppingListInformation
import com.kausar.messmanagementapp.presentation.shopping_info.ShoppingScreen
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    firestoreViewModel: FirebaseFirestoreDbViewModel,
    authVM: AuthViewModel,
) {
    var shoppingData = ""
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            if (mainViewModel.userInfo.value.userType == MemberType.Member.name) {
                SharedHomeScreen(mainViewModel)
            } else {
                TabScreen(navController = navController, mainViewModel = mainViewModel)
            }
        }
        composable(route = BottomBarScreen.Shopping.route) {
            ShoppingScreen(mainViewModel, navController)
        }
        composable(route = Screen.AddMoney.route) {
            AddMoney(mainViewModel, navController, firestoreViewModel)
        }
        composable(route = Screen.Splash.route) {
            SplashScreen {
                navController.popBackStack()
                if (mainViewModel.isLoggedIn.value) {
                    navController.navigate(BottomBarScreen.Home.route)
                } else navController.navigate(Screen.Login.route)
            }
        }
        composable(route = Screen.ShoppingList.route) {
            ShoppingListInformation(shoppingData)
        }
        composable(route = Screen.ShopEntry.route) {
            NewShopEntry(mainViewModel, navController, firestoreViewModel)
        }
        composable(route = Screen.Balance.route) {
            AccountBalance(mainViewModel, firestoreViewModel)
        }
        composable(route = Screen.ShoppingHistory.route) {
            ShoppingHistory(mainViewModel, firestoreViewModel) {
                shoppingData = it
                navController.navigate(Screen.ShoppingList.route)
            }
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(mainViewModel)
        }
        composable(route = BottomBarScreen.MealInfo.route) {
            if (mainViewModel.userInfo.value.userType == MemberType.Member.name) {
                MealListScreen(mainViewModel)
            } else {
                MealInfoScreen(mainViewModel = mainViewModel) {
                    //new manager assigned
                    authVM.logout()
                    mainViewModel.saveLoginStatus(false)
                    logoutAndNavigateToLoginPage(navController)
                }
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        }
    }
}
