package com.kausar.messmanagementapp.presentation.meal_info_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.getDateList
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState
import java.util.Calendar


@Composable
fun MealListScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
) {
    val itemState = viewModel.response.value
    val userInfo = mainViewModel.userInfo.value

    LaunchedEffect(key1 = true) {
        if (userInfo.userType.isEmpty()) {
            mainViewModel.getUserInfo()
        } else {
            viewModel.getAllMeal(userInfo.userId)
        }
    }

    val listTitle = fetchCurrentMonthName()
    val date = getDate(Calendar.getInstance())
    val searchItems = getDateList(date)

    var search by remember {
        mutableStateOf(false)
    }

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    //when internet available and previously not fetch any data
    if (userInfo.userType.isEmpty()) {
        LaunchedEffect(key1 = isConnected) {
            if (isConnected) {
                mainViewModel.getUserInfo()
            }
        }
    }

    LaunchedEffect(key1 = isConnected) {
        if (itemState.error.isNotEmpty() && isConnected) {
            viewModel.getAllMeal(userInfo.userId)
        }
    }

    val listState = rememberLazyListState()

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(.1f))
            if (itemState.item.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (search) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "search",
                        tint = if (search) Color.Gray else Color.Blue,
                        modifier = Modifier
                            .border(1.dp, Color.LightGray)
                            .clickable {
                                search = !search
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                if (search) {
                    LazyRow(
                        Modifier
                            .padding(horizontal = 16.dp),
                        state = listState
                    ) {
                        itemsIndexed(searchItems) { index, item ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .border(
                                        1.dp, Color.DarkGray,
                                        CircleShape
                                    )
                                    .background(
                                        if (selectedIndex == index) {
                                            Color(0xFF3F51B5)
                                        } else {
                                            Color.Gray
                                        },
                                        CircleShape
                                    )
                                    .clickable {
                                        selectedIndex = index
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item, color = Color.White)
                            }
                        }
                    }
                }
                if (search) {
                    val found = getMealAt(itemState.item, searchItems[selectedIndex])
                    Spacer(modifier = Modifier.height(16.dp))
                    MealItemSearch(meal = found)
                } else {
                    MealListInfo(itemState = itemState)
                }

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
                            Text(
                                text = "No meal history found!",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

        }
    }
}

fun getMealAt(meals: List<MealInfo>, day: String): MealInfo? {
    var foundMeal: MealInfo? = null
    for (meal in meals) {
        if (meal.date!!.substring(0, 2) == day) {
            foundMeal = meal
            break
        }
    }
    return foundMeal
}

@Composable
fun MealItemSearch(meal: MealInfo?) {
    Column(
        Modifier
            .fillMaxHeight(.95f)
    ) {
        meal?.let {
            MealItem(meal = meal)
        } ?: Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(.9f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Not found any meal!", textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun MealListInfo(itemState: FirebaseFirestoreDbViewModel.ItemState) {
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