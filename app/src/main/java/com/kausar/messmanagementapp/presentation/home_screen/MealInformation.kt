package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.data.model.MealInfo

@Composable
fun MealInformation(
    modifier: Modifier = Modifier,
    mealInfo: MealInfo?
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val title = listOf("Breakfast", "Lunch", "Dinner")
        repeat(title.size) {
            val value = when (it) {
                0 -> mealInfo?.breakfast ?: false
                1 -> mealInfo?.lunch ?: false
                else -> mealInfo?.dinner ?: false
            }
            val quantity = when (it) {
                0 -> mealInfo?.cntBreakFast ?: 0
                1 -> mealInfo?.cntLunch ?: 0
                else -> mealInfo?.cntDinner ?: 0
            }
            Card(
                modifier = Modifier
                    .padding(8.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${title[it]} ($quantity)",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Switch(checked = value, onCheckedChange = {})
                }
            }

        }
    }
}

@Composable
fun SingleRowForMealInfo(
    isEnabled: Boolean = false,
    title: String,
    unit: Int = 0,
    value: Boolean,
    onAmountChanged: (Int) -> Unit = {},
    onChange: (Boolean) -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        CustomCheckBox(
            isEnabled = isEnabled,
            isChecked = value,
            onCheckChange = onChange,
            modifier = Modifier.weight(1f)
        )
        QuantityBuilder(
            modifier = Modifier.weight(1f),
            isEnabled = isEnabled,
            checked = value, amount = unit, onDecrease = {
                var curr = unit
                curr--
                onAmountChanged(curr)

            }, onIncrease = {
                var curr = unit
                curr++
                onAmountChanged(curr)
            })
    }

}

@Composable
fun QuantityBuilder(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    checked: Boolean,
    amount: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (checked && isEnabled) {
            IconButton(
                onClick = {
                    if (amount > 1) {
                        onDecrease()
                    }
                },
                modifier = Modifier
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = "minus",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = amount.toString())
        Spacer(modifier = Modifier.width(8.dp))
        if (checked && isEnabled) {
            IconButton(
                onClick = {
                    if (amount < 3) {
                        onIncrease()
                    }
                }, modifier = Modifier
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    )
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "plus",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
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