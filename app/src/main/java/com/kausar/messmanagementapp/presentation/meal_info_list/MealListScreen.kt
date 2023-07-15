package com.kausar.messmanagementapp.presentation.meal_info_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.data.Meal
import com.kausar.messmanagementapp.data.MealStatus
import com.kausar.messmanagementapp.data.mealListTitle
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.utils.CustomTopAppBar

val mealList = (1..30).map {
    Meal(
        date = "$it/7/23",
        dayName = "Wednesday",
        breakfast = true,
        lunch = true,
        dinner = true,
        status = when {
            it % 2 == 0 -> MealStatus.Pending
            it % 3 == 0 -> MealStatus.Completed
            else -> MealStatus.Running
        }
    )
}.toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(toggleDrawerState: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(topBar = {
        CustomTopAppBar(
            title = Screen.MealList.title,
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
            Spacer(modifier = Modifier.fillMaxHeight(.1f))
            Text(
                text = "Your Meal Information",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style= TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Cursive
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxHeight(.8f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            ) {
                ShowTitle(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .shadow(
                            elevation = 1.dp,
                            clip = true
                        ),
                    mealListTitle
                )
                LazyColumn(
                    content = {
                        itemsIndexed(mealList) { _, meal ->
                            ShowInfo(
                                modifier = Modifier
                                    .fillMaxWidth(.9f)
                                    .shadow(
                                        elevation = 1.dp,
                                        clip = true
                                    ),
                                meal
                            )
                        }
                    }
                )
            }
        }
    }

}

@Composable
fun ShowInfo(modifier: Modifier, item: Meal) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.date,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = item.dayName,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Checkbox(checked = item.breakfast, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Checkbox(checked = item.lunch, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Checkbox(checked = item.dinner, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Text(
            text = item.status.name,
            fontSize = 6.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .background(
                    when (item.status) {
                        MealStatus.Pending -> Color.Gray
                        MealStatus.Running -> Color.Blue
                        MealStatus.Completed -> Color.Green
                    }, shape = RoundedCornerShape(2.dp)
                )
                .padding(2.dp)
                .weight(1.3f)
        )
    }

}

@Composable
fun ShowTitle(modifier: Modifier, items: List<String>) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(items.size) {
            Text(
                text = items[it],
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier.weight(if (it == 0 || it == 1) 2f else if (it == items.lastIndex) 1.5f else 1f)
            )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun PreviewMealListScreen() {
    MealListScreen {

    }
}