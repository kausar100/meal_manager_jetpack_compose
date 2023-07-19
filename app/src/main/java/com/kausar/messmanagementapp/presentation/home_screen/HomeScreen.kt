package com.kausar.messmanagementapp.presentation.home_screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.presentation.auth_screen.AuthViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val calendar = Calendar.getInstance()
    var currentDate by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }
    var showProgress by rememberSaveable {
        mutableStateOf(false)
    }

    var progressMsg by rememberSaveable {
        mutableStateOf("")
    }

    var newMeal by rememberSaveable {
        mutableStateOf(false)
    }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(topBar = {
        CustomTopAppBar(
            title = "Meal Information",
            canNavigateBack = false,
            logoutAction = {
                scope.launch {
                    authViewModel.logout().collectLatest {
                        when (it) {
                            is ResultState.Loading -> {
                                progressMsg = "Signing out..."
                                showProgress = true
                            }

                            is ResultState.Failure -> {
                                showProgress = false
                                it.message.localizedMessage?.let { msg -> context.showToast(msg) }
                            }

                            is ResultState.Success -> {
                                showProgress = false
                                context.showToast(it.data)
                                onLogout()
                            }
                        }
                    }
                }
            },
            scrollBehavior = scrollBehavior)
    },
        floatingActionButton = {
            if (!newMeal) {
                FloatingActionButton(
                    onClick = {
                        calendar.add(Calendar.DATE, 1)
                        currentDate = fetchDateAsString(calendar)
                        newMeal = true
                    },
                    modifier = Modifier.background(Color.Transparent, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add meal",
                        tint = Color.Black
                    )
                }
            }

        }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(.1f))
                Text(text = currentDate, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (!newMeal) "Running Meal information" else "Change Meal status and Click Add meal",
                    textAlign = TextAlign.Center
                )

                MealInfo(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .padding(16.dp),
                    newMeal = newMeal,
                    onCancel = {
                        newMeal = false
                    },
                    updateMeal = { breakfast, lunch, dinner ->
                        newMeal = false
                        scope.launch {
                            viewModel.insert(
                                MealInfo(
                                    getDate(calendar),
                                    getDayName(calendar),
                                    breakfast,
                                    lunch,
                                    dinner)
                            ).collectLatest { result ->
                                showProgress = when (result) {
                                    is ResultState.Success -> {
                                        println("id from firestore : "+result.data.key.toString())
                                        context.showToast(result.data.message.toString())
                                        false
                                    }

                                    is ResultState.Failure -> {
                                        result.message.localizedMessage?.let { msg ->
                                            context.showToast(
                                                msg)
                                        }
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
            }
            if (showProgress) {
                CustomProgressBar(msg = "Data updating...")
            }
        }

    }
}

@Composable
fun MealInfo(
    modifier: Modifier = Modifier,
    newMeal: Boolean = false,
    onCancel: () -> Unit,
    updateMeal: (Boolean, Boolean, Boolean) -> Unit
) {
    var breakFast by rememberSaveable { mutableStateOf(true) }
    var lunch by rememberSaveable { mutableStateOf(true) }
    var dinner by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxHeight(.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Meal time",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Meal status",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

        }
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .weight(2f)
        ) {
            val title = listOf("Breakfast", "Lunch", "Dinner")
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                repeat(title.size) {
                    Text(text = title[it], textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                CustomCheckBox(isEnabled = newMeal, isChecked = breakFast, onCheckChange = {
                    breakFast = it
                })
                CustomCheckBox(isEnabled = newMeal, isChecked = lunch, onCheckChange = {
                    lunch = it
                })
                CustomCheckBox(isEnabled = newMeal, isChecked = dinner, onCheckChange = {
                    dinner = it
                })
            }
        }
        if (newMeal) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                OutlinedButton(
                    onClick = onCancel, shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Cancel",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                ElevatedButton(
                    onClick = {
                        updateMeal(breakFast, lunch, dinner)
                    }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF222B83),
                        contentColor = Color.White,
                    )
                ) {
                    Text(
                        text = "Add Meal",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
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

@Preview
@Composable
fun PreviewHome() {
   HomeScreen() {

   }
}