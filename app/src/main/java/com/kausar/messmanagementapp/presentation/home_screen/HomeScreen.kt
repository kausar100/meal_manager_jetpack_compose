package com.kausar.messmanagementapp.presentation.home_screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.RealtimeDbViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    viewModel: RealtimeDbViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    toggleDrawerState: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val calendar = Calendar.getInstance()
    var currentDate by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }
    var showProgress by rememberSaveable {
        mutableStateOf(false)
    }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(topBar = {
        CustomTopAppBar(
            title = Screen.Home.title,
            canNavigateBack = false,
            canShowDrawer = true,
            showAction = true,
            actionIcon = Icons.Default.Person,
            onClickAction = navigateToProfileScreen,
            scrollBehavior = scrollBehavior,
            onClickDrawerMenu = toggleDrawerState
        )
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(.1f))
            DateInfo(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                date = currentDate,
                onNextDay = {
                    calendar.add(Calendar.DATE, 1)
                    currentDate = fetchDateAsString(calendar)
                    println(currentDate)
                },
                onPreviousDay = {
                    calendar.add(Calendar.DATE, -1)
                    currentDate = fetchDateAsString(calendar)
                    println(currentDate)
                })

            MealInfo(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(16.dp),
                updateMeal = { breakfast, lunch, dinner ->
                    scope.launch {
                        viewModel.insert(
                            Meal(
                                getDate(calendar),
                                getDayName(calendar),
                                breakfast,
                                lunch,
                                dinner,
                                MealStatus.Pending
                            )
                        ).collectLatest { result ->
                            showProgress = when (result) {
                                is ResultState.Success -> {
                                    context.showToast(result.data)
                                    false
                                }

                                is ResultState.Failure -> {
                                    context.showToast(result.message.toString())
                                    false
                                }

                                is ResultState.Loading -> {
                                    true
                                }
                            }
                        }
                    }
                }
            )

            if (showProgress) {
                CustomProgressBar(msg = "Loading...")
            }

        }

    }
}

@Composable
fun MealInfo(
    modifier: Modifier = Modifier,
    updateMeal: (Boolean, Boolean, Boolean) -> Unit
) {
    var breakFast by rememberSaveable { mutableStateOf(true) }
    var lunch by rememberSaveable { mutableStateOf(true) }
    var dinner by rememberSaveable { mutableStateOf(true) }
    var updateMealInfo by rememberSaveable { mutableStateOf(false) }
    val roundBorder = Modifier
        .border(
            width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)
        )
        .padding(8.dp)

    Column(
        modifier = modifier
            .border(
                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp, bottom = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Meal Time",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Meal Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = roundBorder
        ) {
            Text(text = "Breakfast")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(isEnabled = updateMealInfo, isChecked = breakFast, onCheckChange = {
                breakFast = it
            })

        }
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = roundBorder
        ) {
            Text(text = "Lunch")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(isEnabled = updateMealInfo, isChecked = lunch, onCheckChange = {
                lunch = it
            })

        }
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = roundBorder
        ) {
            Text(text = "Dinner")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(isEnabled = updateMealInfo, isChecked = dinner, onCheckChange = {
                dinner = it
            })

        }

        Spacer(modifier = Modifier.fillMaxHeight(.1f))

        OutlinedButton(
            onClick = {
                updateMealInfo = !updateMealInfo
                if (!updateMealInfo) {
                    updateMeal(breakFast, lunch, dinner)
                }
            }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                containerColor = if (updateMealInfo) Color(0xFF222B83) else Color.DarkGray,
                contentColor = Color.White,
            )
        ) {
            Text(
                text = if (updateMealInfo) "Done" else "Update Meal",
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isChecked: Boolean = false,
    onCheckChange: (Boolean) -> Unit
) {
    Checkbox(enabled = isEnabled,
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = { onCheckChange(it) })
}

private fun fetchDateAsString(calendar: Calendar): String {
    // Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val dayName = getDayName(calendar)
    return "$dayName, $dayOfMonth/${month + 1}/$year"
}

private fun getDate(calendar: Calendar): String {
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val prefixDayOfMonth = if (dayOfMonth < 10) "0" else ""
    val prefixMonth = if (month < 9) "0" else ""
    return "$prefixDayOfMonth$dayOfMonth/$prefixMonth${month + 1}/$year"
}

private fun getDayName(calendar: Calendar): String {
    val day = when (calendar[Calendar.DAY_OF_WEEK]) {
        1 -> "SUNDAY"
        2 -> "MONDAY"
        3 -> "TUESDAY"
        4 -> "WEDNESDAY"
        5 -> "THURSDAY"
        6 -> "FRIDAY"
        7 -> "SATURDAY"
        else -> ""
    }
    return day
}

@Composable
fun DateInfo(
    modifier: Modifier = Modifier,
    date: String,
    onNextDay: () -> Unit,
    onPreviousDay: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Meal Information",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Cursive
            )
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "previous_day")

            }
            Text(text = date, modifier = Modifier.weight(5f), textAlign = TextAlign.Center)
            IconButton(onClick = onNextDay, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, contentDescription = "next_day"
                )

            }

        }
    }

}

@Preview
@Composable
fun PreviewHome() {
    HomeScreen(userName = "", navigateToProfileScreen = { /*TODO*/ }) {

    }
}