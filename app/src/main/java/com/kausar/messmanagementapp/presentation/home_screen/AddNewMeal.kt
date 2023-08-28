package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel

@Composable
fun AddNewMeal(
    onCancel: () -> Unit,
    selectedDate: String,
    viewModel: FirebaseFirestoreDbViewModel,
    updateMeal: (Boolean, Boolean, Boolean) -> Unit,
    addMeal: (Boolean, Boolean, Boolean) -> Unit
) {
    var edit by rememberSaveable { mutableStateOf(false) }
    var toUpdate by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedDate) {
        toUpdate = false
        edit = false
        viewModel.getMealAtDate(selectedDate)
    }

    val mealInfoState = viewModel.newMealInfo.value

    var breakFast by rememberSaveable { mutableStateOf(false) }
    var lunch by rememberSaveable { mutableStateOf(false) }
    var dinner by rememberSaveable { mutableStateOf(false) }



    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (mealInfoState.success.isNotEmpty() || mealInfoState.error.isNotEmpty()) {
            LaunchedEffect(key1 = selectedDate) {
                if (mealInfoState.success.isNotEmpty()) {
                    edit = false
                    breakFast = mealInfoState.meal.breakfast!!
                    lunch = mealInfoState.meal.lunch!!
                    dinner = mealInfoState.meal.dinner!!
                } else {
                    edit = true
                    breakFast = false
                    lunch = false
                    dinner = false
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(Modifier.fillMaxHeight(.6f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Meal time",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Meal status",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )

                    }
                    Divider(Modifier.padding(horizontal = 8.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 0.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        val title = listOf("Breakfast", "Lunch", "Dinner")
                        repeat(title.size) {
                            val value = when (it) {
                                0 -> breakFast
                                1 -> lunch
                                else -> dinner
                            }
                            SingleRowForMealInfo(
                                isEnabled = edit or toUpdate,
                                title = title[it], value = value, onChange = { newValue ->
                                    when (it) {
                                        0 -> {
                                            breakFast = newValue
                                        }

                                        1 -> {
                                            lunch = newValue
                                        }

                                        else -> {
                                            dinner = newValue
                                        }
                                    }
                                })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(visible = !toUpdate) {
                    ElevatedButton(
                        onClick = {
                            addMeal(breakFast, lunch, dinner)
                        }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                        ),
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Text(
                            text = "Add Meal",
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                AnimatedVisibility(visible = toUpdate) {
                    ElevatedButton(
                        onClick = {
                            updateMeal(breakFast, lunch, dinner)
                        }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(

                        ),
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Text(
                            text = "Update Meal",
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCancel, shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(
                        text = "Cancel",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (!toUpdate) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    FloatingActionButton(
                        onClick = {
                            toUpdate = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "update meal",
                        )
                    }
                }

            }
        }
        if (mealInfoState.isLoading) {
            CustomProgressBar(msg = "Fetching meal info...")
        }
    }

}