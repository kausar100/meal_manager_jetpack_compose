package com.kausar.messmanagementapp.presentation.meal_info_list

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.home_screen.MealSummary
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MealInfoScreen(
    mainViewModel: MainViewModel, viewModel: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    val itemState = viewModel.response.value
    val memberState = mainViewModel.memberInfo.value
    val userInfo = mainViewModel.userInfo.value

    Log.d("TAG", "MealInfoScreen: ${mainViewModel.totalMealCntPerMember.values}")


    var selectedMemberTotalMealCount by remember {
        mutableStateOf(mainViewModel.totalMealCntPerMember[userInfo.userId])
    }

    val listTitle = fetchCurrentMonthName()

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    LaunchedEffect(key1 = isConnected) {
        if(userInfo.userType.isEmpty()){
            mainViewModel.getUserInfo()
        }
        if (memberState.listOfMember.isEmpty()) {
            mainViewModel.getMessMembers()
        }
    }

    var showList by remember {
        mutableStateOf(false)
    }

    var selectedMember by remember {
        mutableStateOf(userInfo)
    }

    var showMealInfoScreen by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (showList) {
                ShowUser(userInfo = selectedMember, expand = true, onClickUser = {
                    showList = false
                })
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = listTitle,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                MealInfoTable(itemState = itemState)

            } else {
                if (memberState.listOfMember.isNotEmpty()) {
                    LazyColumn(content = {
                        items(
                            memberState.listOfMember,
                        ) { user ->
                            ShowUser(user, expand = false, onClickUser = {
                                scope.launch {
                                    viewModel.getMealByUserId(user.userId)
                                    delay(500)
                                    selectedMember = user
                                    showList = true
                                }
                            }, onClickInfo = {
                                if (!mainViewModel.totalMealCntPerMember.containsKey(
                                        user.userId
                                    )
                                ) {
                                    mainViewModel.setSingleMemberMealCount(user.userId)
                                }
                                selectedMemberTotalMealCount =
                                    mainViewModel.totalMealCntPerMember[user.userId]
                                showMealInfoScreen = true
                            })
                        }
                    })
                } else {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(.9f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (memberState.isLoading) {
                            CustomProgressBar("Fetching data...")
                        } else if (memberState.error.isNotEmpty()) {
                            Text(memberState.error, textAlign = TextAlign.Center)
                        } else {
                            if (memberState.listOfMember.isEmpty()) {
                                Text(
                                    text = "Mess member not found!", textAlign = TextAlign.Center
                                )
                            }

                        }

                    }

                }


            }

        }
        if (showMealInfoScreen) {
            Box(Modifier.height(screenHeight / 2f), contentAlignment = Alignment.Center) {
                AlertDialog(containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        showMealInfoScreen = false
                    },
                    title = {
                        Text(text = "Number of meal until today", fontFamily = FontFamily.Cursive)
                    },
                    shape = RoundedCornerShape(8.dp),
                    text = {
                        MealSummary(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(screenHeight / 2.5f),
                            totalMeal = selectedMemberTotalMealCount?.total.toString(),
                            numberOfBreakfast = selectedMemberTotalMealCount?.breakfast.toString(),
                            numberOfLunch = selectedMemberTotalMealCount?.lunch.toString(),
                            numberOfDinner = selectedMemberTotalMealCount?.dinner.toString()
                        )


                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showMealInfoScreen = false
                            },
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {

                    })
            }

        }
    }

}

@Composable
fun MealInfoTable(itemState: FirebaseFirestoreDbViewModel.ItemState) {
    val mealListTitle = listOf("Date", "Day", "B", "L", "D")
    Column(
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
    ) {
        if (itemState.item.isNotEmpty()) {
            val mealResponseList = itemState.item
            LazyColumn(content = {
                item {
                    ShowTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 1.dp, clip = true
                            ), mealListTitle
                    )

                }
                items(
                    mealResponseList,
                ) { item ->
                    ShowInfo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 1.dp, clip = true
                            ), item = item
                    )
                }
            })
        } else {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.9f), contentAlignment = Alignment.Center
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
fun ShowTitle(modifier: Modifier, items: List<String>) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(items.size) {
            Text(
                text = items[it],
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                modifier = Modifier.weight(if (it == 0 || it == 1) 2f else 1f)
            )
        }

    }

}

@Composable
fun ShowInfo(modifier: Modifier, item: MealInfo) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.date!!,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = item.dayName!!,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = item.cntBreakFast.toString(),
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.cntLunch.toString(),
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.cntDinner.toString(),
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUser(
    userInfo: User,
    showInfo: Boolean = true,
    expand: Boolean = false,
    expandable: Boolean = true,
    onClickUser: () -> Unit = {},
    onClickInfo: () -> Unit = {}
) {
    Card(
        Modifier
            .padding(vertical = 8.dp)
            .clickable {
                onClickUser()
            }, elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(4.dp)
    ) {
        ListItem(headlineText = {
            Text(text = userInfo.userName)
        }, leadingContent = {
            if (userInfo.profilePhoto.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(userInfo.profilePhoto)
                        .crossfade(true).build(),
                    placeholder = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(id = R.string.mess_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "profile",
                    modifier = Modifier
                        .size(50.dp)
                        .border(
                            1.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape
                        )
                        .background(color = Color.Transparent, shape = CircleShape)
                        .clip(CircleShape)
                )
            }

        }, trailingContent = {
            Row {
                if (expandable) {
                    IconButton(onClick = onClickUser) {
                        Icon(
                            imageVector = if (expand) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "show_hide_list"
                        )
                    }
                }

                if (!expand) {
                    Spacer(modifier = Modifier.width(8.dp))
                    if (showInfo) {
                        IconButton(onClick = onClickInfo) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "meal_info"
                            )
                        }
                    }

                }

            }

        }, overlineText = { Text(text = userInfo.userType) })
    }
}