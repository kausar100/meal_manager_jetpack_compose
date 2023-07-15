package com.kausar.messmanagementapp.presentation.default_meal_setup_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.presentation.home_screen.MealInfo
import com.kausar.messmanagementapp.utils.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultMealInfo( toggleDrawerState: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(topBar = {
        CustomTopAppBar(
            canNavigateBack = false,
            canShowDrawer = true,
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
            Text(
                text = "Setup your default meal",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealInfo(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(16.dp)
            )
        }
    }

}



@Preview(showBackground = true)
@Composable
fun PreviewDefaultMealInfo(){
    DefaultMealInfo {

    }
}