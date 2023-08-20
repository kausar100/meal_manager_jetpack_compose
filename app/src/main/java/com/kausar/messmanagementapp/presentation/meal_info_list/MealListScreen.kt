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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.MemberType
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MealListScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
) {

    val itemState = viewModel.response.value
    val memberState = viewModel.memberInfo.value
    val userInfo = mainViewModel.userInfo.value

    if (userInfo.userType == MemberType.Member.name) {
        LaunchedEffect(key1 = true) {
            viewModel.getMealByUserId(userInfo.userId)
        }
    }

    val listTitle = fetchCurrentMonthName()

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    if (isConnected && itemState.error.isNotEmpty()) {
        LaunchedEffect(key1 = true) {
            viewModel.getMembers()
        }
    }

    var showList by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(.1f))
        if (userInfo.userType == MemberType.Member.name) {
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
            MealListInfo(itemState = itemState)
        } else {
            if (showList) {
                ShowUser(userInfo = userInfo, true) {
                    showList = false
                }
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
                MealListInfo(itemState = itemState)

            } else {
                if (memberState.listOfMember.isNotEmpty()) {
                    LazyColumn(
                        content = {
                            items(
                                memberState.listOfMember,
                            ) { user ->
                                ShowUser(user, showList) {
                                    scope.launch {
                                        viewModel.getMealByUserId(user.userId)
                                        delay(500)
                                        showList = true
                                    }
                                }
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
                        if (memberState.isLoading) {
                            CustomProgressBar("Fetching data...")
                        } else if (itemState.error.isNotEmpty()) {
                            Text(itemState.error, textAlign = TextAlign.Center)
                        } else {
                            if (itemState.item.isEmpty()) {
                                Text(
                                    text = "Not found any member!",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    }

                }


            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUser(userInfo: User, expand: Boolean = false, onClickUser: () -> Unit) {
    ListItem(headlineText = {
        Text(text = userInfo.userName)
    },
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable {
                onClickUser()
            },
        leadingContent = {
            if (userInfo.profilePhoto.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userInfo.profilePhoto)
                        .crossfade(true).build(),
                    placeholder = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(id = R.string.profile_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "profile",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, color = Color.Gray, shape = CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .clip(CircleShape)
                )
            }

        },
        trailingContent = {
            IconButton(onClick = onClickUser) {
                Icon(
                    imageVector = if (expand) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "show_hide_list"
                )
            }
        },
        overlineText = { Text(text = userInfo.userType) }
    )

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