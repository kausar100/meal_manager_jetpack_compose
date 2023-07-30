package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
    updateMeal: (Boolean, Boolean, Boolean) -> Unit
) {
    var breakFast by rememberSaveable { mutableStateOf(false) }
    var lunch by rememberSaveable { mutableStateOf(false) }
    var dinner by rememberSaveable { mutableStateOf(false) }

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