package com.kausar.messmanagementapp.presentation.meal_info_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.data.model.Meal
import com.kausar.messmanagementapp.data.model.MealStatus
import com.kausar.messmanagementapp.data.model.mealListTitle
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.RealtimeDbViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    toggleDrawerState: () -> Unit,
    viewModel: RealtimeDbViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val itemState = viewModel.response.value

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
                style = TextStyle(
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

                if (itemState.item.isNotEmpty()) {
                    val mealResponseList = itemState.item

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
                            items(
                                mealResponseList,
                                key = { firebase_db ->
                                    firebase_db.key!!
                                }

                            ) { item ->
                                ShowInfo(
                                    modifier = Modifier
                                        .fillMaxWidth(.9f)
                                        .shadow(
                                            elevation = 1.dp,
                                            clip = true
                                        ),
                                    item = item.meal!!
                                )
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
                            Text(itemState.error)
                        } else {
                            if (itemState.item.isEmpty()) {
                                Text(text = "Not found any meal history!")
                            }
                        }

                    }

                }
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
            text = item.date!!,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = item.dayName!!,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Checkbox(checked = item.breakfast!!, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Checkbox(checked = item.lunch!!, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Checkbox(checked = item.dinner!!, onCheckedChange = {}, modifier = Modifier.weight(1f))
        Text(
            text = item.status!!.name,
            fontSize = 6.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .background(
                    when (item.status!!) {
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
    MealListScreen(toggleDrawerState = { /*TODO*/ })
}