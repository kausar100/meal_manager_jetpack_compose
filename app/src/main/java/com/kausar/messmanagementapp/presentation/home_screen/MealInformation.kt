package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.data.model.MealInfo

@Composable
fun MealInformation(
    mealInfo: MealInfo?
) {
    var breakFast by rememberSaveable { mutableStateOf(mealInfo?.breakfast ?: false) }
    var lunch by rememberSaveable { mutableStateOf(mealInfo?.lunch ?: false) }
    var dinner by rememberSaveable { mutableStateOf(mealInfo?.dinner ?: false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
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
                SingleRowForMealInfo(title = title[it], value = value, onChange = { newValue ->
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
}

@Composable
fun SingleRowForMealInfo(
    isEnabled: Boolean = false,
    title: String,
    value: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        CustomCheckBox(isEnabled = isEnabled, isChecked = value, onCheckChange = onChange)
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