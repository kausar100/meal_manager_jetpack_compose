package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.utils.CustomTopAppBar
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userName: String, drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val calendar = Calendar.getInstance()
    var currentDate by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }

    Scaffold(topBar = {
        CustomTopAppBar(
            canNavigateBack = false,
            showAction = true,
            actionIcon = Icons.Default.Person,
            onClickAction = {
            },
            scrollBehavior = scrollBehavior
        )
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    .padding(16.dp)
            )

        }

    }

}

@Composable
fun MealInfo(modifier: Modifier = Modifier) {
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
            .padding(8.dp),
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

        OutlinedButton(
            onClick = {
                updateMealInfo = !updateMealInfo
                if (updateMealInfo) {
                    //update info
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
    val dayName = when (calendar[Calendar.DAY_OF_WEEK]) {
        1 -> "SUNDAY"
        2 -> "MONDAY"
        3 -> "TUESDAY"
        4 -> "WEDNESDAY"
        5 -> "THURSDAY"
        6 -> "FRIDAY"
        7 -> "SATURDAY"
        else -> ""
    }
    return "$dayName, $dayOfMonth/${month + 1}/$year"
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
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "previous_day")

            }
            Text(text = date, modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
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
    HomeScreen(userName = "")
}