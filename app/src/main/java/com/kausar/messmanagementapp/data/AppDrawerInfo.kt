package com.kausar.messmanagementapp.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.navigation.Screen


data class AppDrawerItemInfo(
    val drawerOption: Screen,
    @StringRes val title: Int,
    val drawableId: ImageVector,
    @StringRes val descriptionId: Int
)

// list of the buttons
object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            Screen.Home,
            R.string.drawer_home,
            Icons.Default.Home,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            Screen.MealList,
            R.string.drawer_meal_list,
            Icons.Default.List,
            R.string.drawer_meal_list_description
        ),
        AppDrawerItemInfo(
            Screen.DefaultMealSetup,
            R.string.drawer_default_meal_setup,
            Icons.Default.Settings,
            R.string.drawer_default_meal_setup_description
        ),
        AppDrawerItemInfo(
            Screen.About,
            R.string.drawer_about,
            Icons.Default.Info,
            R.string.drawer_info_description
        ),
    )
}