package com.kausar.messmanagementapp.presentation.meal_info_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName


@Composable
fun MealListScreen(
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
) {

    val itemState = viewModel.response.value
    val listTitle = fetchCurrentMonthName()


    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(.1f))
        Text(
            text = "Meal List <> $listTitle",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Cursive
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            Modifier
                .fillMaxHeight(.95f)
        ) {

            if (itemState.item.isNotEmpty()) {
                val mealResponseList = itemState.item
                LazyColumn(
                    content = {
                        items(
                            mealResponseList,
                        ) { item ->
                            MealItem(meal = item)
                        }
                    }
                )
            } else {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.9f),
                    contentAlignment = Alignment.Center
                ) {
                    if (itemState.isLoading) {
                        CustomProgressBar("Fetching data...")
                    } else if (itemState.error.isNotEmpty()) {
                        Text(itemState.error, textAlign = TextAlign.Center)
                    } else {
                        if (itemState.item.isEmpty()) {
                            Text(text = "Not found any meal history!", textAlign = TextAlign.Center)
                        }
                    }

                }

            }
        }
    }

}

@Composable
fun MealItem(meal: MealInfo) {
    val mealListTitle = listOf("Date", "Day", "Breakfast", "Lunch", "Dinner")
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(.9f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.padding(4.dp)) {
                repeat(mealListTitle.size) {
                    Text(
                        text = mealListTitle[it],
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(Modifier.padding(4.dp)) {
                Text(text = meal.date.toString())
                Text(text = meal.dayName.toString())
                MealIcon(status = meal.breakfast!!, desc = "breakfast")
                MealIcon(status = meal.lunch!!, desc = "breakfast")
                MealIcon(status = meal.dinner!!, desc = "breakfast")
            }

        }
    }
}

@Composable
fun MealIcon(status: Boolean, desc: String) {
    if (status)
        Icon(imageVector = Icons.Default.Done, contentDescription = desc, tint = Color.Blue)
    else
        Icon(imageVector = Icons.Default.Clear, contentDescription = desc, tint = Color.Gray)
}

@Preview(showBackground = true)
@Composable
fun PreviewMealListScreen() {
   MealListScreen()
}