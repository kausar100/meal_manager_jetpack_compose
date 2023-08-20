package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel

@Composable
fun MealInfoScreen(firestore: FirebaseFirestoreDbViewModel) {

    val mealCnt by firestore.mealCnt.collectAsState()

    MealSummary(
        modifier = Modifier
            .fillMaxWidth(1f),
        totalMeal = mealCnt.totalMeal.toString(),
        numberOfBreakfast = mealCnt.breakfast.toString(),
        numberOfLunch = mealCnt.lunch.toString(),
        numberOfDinner = mealCnt.dinner.toString()
    )
}

@Composable
fun MealSummary(
    modifier: Modifier,
    totalMeal: String,
    numberOfBreakfast: String,
    numberOfLunch: String,
    numberOfDinner: String
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = "Total Meal", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = totalMeal, fontWeight = FontWeight.Bold)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = "Number of BreakFast")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = numberOfBreakfast)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = "Number of Lunch")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = numberOfLunch)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = "Number of Dinner")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = numberOfDinner)
        }
        Divider(Modifier.height(1.dp), color = Color.Gray)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Breakfast(0.5)", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = " Lunch(1.0)", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = " Dinner(1.0)", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }


    }

}