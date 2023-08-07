package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddNewMeal(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    updateMeal: (Boolean, Boolean, Boolean) -> Unit,
    addMeal: (Boolean, Boolean, Boolean) -> Unit
) {
    var breakFast by rememberSaveable { mutableStateOf(false) }
    var lunch by rememberSaveable { mutableStateOf(false) }
    var dinner by rememberSaveable { mutableStateOf(false) }

    var edit by rememberSaveable { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight(.8f)
                .fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    CustomCheckBox(isChecked = breakFast, onCheckChange = {
                        breakFast = it
                    })
                    CustomCheckBox(isChecked = lunch, onCheckChange = {
                        lunch = it
                    })
                    CustomCheckBox(isChecked = dinner, onCheckChange = {
                        dinner = it
                    })
                }
            }
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(visible = !edit) {
                ElevatedButton(
                    onClick = {
                        addMeal(breakFast, lunch, dinner)
                    }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF222B83),
                        contentColor = Color.White,
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
            AnimatedVisibility(visible = edit) {
                ElevatedButton(
                    onClick = {
                        updateMeal(breakFast, lunch, dinner)
                    }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF222B83),
                        contentColor = Color.White,
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
        if (!edit) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                FloatingActionButton(
                    onClick = {
                        edit = true

                    },
                    modifier = Modifier.background(Color.Transparent, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "update meal",
                        tint = Color.Black
                    )
                }
            }

        }
    }
}