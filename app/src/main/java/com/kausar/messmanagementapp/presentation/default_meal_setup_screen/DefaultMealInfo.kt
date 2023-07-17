package com.kausar.messmanagementapp.presentation.default_meal_setup_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.home_screen.MealInfo
import com.kausar.messmanagementapp.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultMealInfo(onLogout: () -> Unit, toggleDrawerState: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(topBar = {
        CustomTopAppBar(
            title = Screen.DefaultMealSetup.title,
            canNavigateBack = false,
            canShowDrawer = true,
            logoutAction = onLogout,
            scrollBehavior = scrollBehavior,
            onClickDrawerMenu = toggleDrawerState
        )
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(.1f))
            Text(
                text = "Setup your default meal info",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Cursive
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealInfo(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(16.dp),
                updateMeal = { breakfast, lunch, dinner ->


                }
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewDefaultMealInfo() {
   DefaultMealInfo(onLogout = { /*TODO*/ }) {

   }
}