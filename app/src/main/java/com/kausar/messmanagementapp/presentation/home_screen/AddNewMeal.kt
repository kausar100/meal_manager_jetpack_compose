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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel

@Composable
fun AddNewMeal(
    onCancel: () -> Unit,
    selectedDate: String,
    viewModel: FirebaseFirestoreDbViewModel,
    updateMeal: (Boolean, Boolean, Boolean, Double, Double, Double) -> Unit,
    addMeal: (Boolean, Boolean, Boolean, Double, Double, Double) -> Unit
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

    var cntbreakFast by rememberSaveable { mutableStateOf(0.0) }
    var cntlunch by rememberSaveable { mutableStateOf(0.0) }
    var cntdinner by rememberSaveable { mutableStateOf(0.0) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (mealInfoState.isLoading) {
            CustomProgressBar(msg = "Fetching meal info...")
        }
        if (mealInfoState.success.isNotEmpty() || mealInfoState.error.isNotEmpty()) {
            LaunchedEffect(key1 = selectedDate) {
                if (mealInfoState.success.isNotEmpty()) {
                    edit = false
                    breakFast = mealInfoState.meal.breakfast!!
                    lunch = mealInfoState.meal.lunch!!
                    dinner = mealInfoState.meal.dinner!!
                    cntbreakFast = mealInfoState.meal.cntBreakFast
                    cntlunch = mealInfoState.meal.cntLunch
                    cntdinner = mealInfoState.meal.cntDinner
                } else {
                    edit = true
                    breakFast = false
                    lunch = false
                    dinner = false
                    cntbreakFast = 0.0
                    cntlunch = 0.0
                    cntdinner = 0.0
                }
            }
            Column(
                modifier = Modifier
                    .height(screenHeight / 1.3f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(Modifier.height(screenHeight / 3f).fillMaxWidth(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Meal time",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Meal status",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                    }
                    Divider(Modifier.padding(horizontal = 8.dp))
                    Column(
                        Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f)
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
                            val quantity = when (it) {
                                0 -> cntbreakFast
                                1 -> cntlunch
                                else -> cntdinner
                            }
                            SingleRowForMealInfo(
                                isEnabled = edit or toUpdate,
                                title = title[it],
                                unit = quantity,
                                value = value,
                                onAmountChanged = { newCnt ->
                                    when (it) {
                                        0 -> {
                                            cntbreakFast = newCnt
                                        }

                                        1 -> {
                                            cntlunch = newCnt
                                        }

                                        else -> {
                                            cntdinner = newCnt
                                        }
                                    }

                                },
                                onChange = { newValue ->
                                    when (it) {
                                        0 -> {
                                            breakFast = newValue
                                            cntbreakFast = if(newValue){
                                                1.0
                                            }else{
                                                0.0
                                            }
                                        }

                                        1 -> {
                                            lunch = newValue
                                            cntlunch = if(newValue){
                                                1.0
                                            }else{
                                                0.0
                                            }
                                        }

                                        else -> {
                                            dinner = newValue
                                            cntdinner = if(newValue){
                                                1.0
                                            }else{
                                                0.0
                                            }
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
                            addMeal(breakFast, lunch, dinner, cntbreakFast, cntlunch, cntdinner)
                        }, shape = RoundedCornerShape(4.dp),
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
                            updateMeal(breakFast, lunch, dinner, cntbreakFast, cntlunch, cntdinner)
                        }, shape = RoundedCornerShape(4.dp),
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
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(visible = !toUpdate) {
                    OutlinedButton(
                        onClick = { toUpdate = true }, shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Text(
                            text = "Edit Meal",
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }

    }

}