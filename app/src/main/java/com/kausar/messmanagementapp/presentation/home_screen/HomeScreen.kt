package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchDateAsString
import java.util.Calendar

enum class Keys {
    Total, Breakfast, Lunch, Dinner
}

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val calendar = Calendar.getInstance()
    val today by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }

    LaunchedEffect(key1 = true) {
        if (mainViewModel.totalMealCount.value.isEmpty()) {
            mainViewModel.getAllMealCount()
        }
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
                text = "Number of total meal until today",
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
                        .fillMaxHeight(.4f)
                        .padding(horizontal = 8.dp),
                    totalMeal = if (data.contains(Keys.Total.name)) data[Keys.Total.name].toString() else "0.0",
                    numberOfBreakfast = if (data.contains(Keys.Breakfast.name)) data[Keys.Breakfast.name].toString() else "0.0",
                    numberOfLunch = if (data.contains(Keys.Lunch.name)) data[Keys.Lunch.name].toString() else "0.0",
                    numberOfDinner = if (data.contains(Keys.Dinner.name)) data[Keys.Dinner.name].toString() else "0.0"
                )
            } else {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = today,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            if (mainViewModel.todayMealCount.value.isNotEmpty()) {
                val data = mainViewModel.todayMealCount.value
                Column(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TodayMealCountInformation(
                        mealTime = "Meal Time",
                        value = 0.0,
                        header = "Number of Meal"
                    )
                    Divider(
                        Modifier
                            .padding(horizontal = 16.dp)
                    )
                    repeat(3) {
                        val title = when (it) {
                            0 -> Keys.Breakfast.name
                            1 -> Keys.Lunch.name
                            2 -> Keys.Dinner.name
                            else -> ""
                        }
                        TodayMealCountInformation(
                            mealTime = title,
                            value = data[title] ?: 0.0
                        )
                    }
                }

            } else {
                CircularProgressIndicator()
            }
        }

    }

}


@Composable
fun TodayMealCountInformation(mealTime: String, value: Double, header: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mealTime,
            fontWeight = if (header != null) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.weight(1f))
        header?.let {
            Text(text = header, fontWeight = FontWeight.Bold)
        } ?: Text(text = value.toString())
    }

}

@Preview
@Composable
fun PreviewHome() {
    HomeScreen()
}