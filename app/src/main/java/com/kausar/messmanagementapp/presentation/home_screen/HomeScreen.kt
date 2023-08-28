package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

enum class Keys {
    Total, Breakfast, Lunch, Dinner
}

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = true) {
        mainViewModel.getAllMealCount()
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Number of meal until today",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
            if (mainViewModel.totalMealCount.value.isNotEmpty()) {
                val data = mainViewModel.totalMealCount.value
                MealSummary(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(.4f),
                    totalMeal = if (data.contains(Keys.Total.name)) data[Keys.Total.name].toString() else "0.0",
                    numberOfBreakfast = if (data.contains(Keys.Breakfast.name)) data[Keys.Breakfast.name].toString() else "0.0",
                    numberOfLunch = if (data.contains(Keys.Lunch.name)) data[Keys.Lunch.name].toString() else "0.0",
                    numberOfDinner = if (data.contains(Keys.Dinner.name)) data[Keys.Dinner.name].toString() else "0.0"
                )
            }else{
                CircularProgressIndicator()
            }
        }

    }

}


@Preview
@Composable
fun PreviewHome() {
    HomeScreen()
}