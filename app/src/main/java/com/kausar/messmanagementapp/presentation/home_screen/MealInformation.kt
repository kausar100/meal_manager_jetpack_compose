package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.data.model.MealInfo

@Composable
fun MealInformation(
    mealInfo: MealInfo?
) {
    var breakFast by rememberSaveable { mutableStateOf(mealInfo?.breakfast ?: false) }
    var lunch by rememberSaveable { mutableStateOf(mealInfo?.lunch ?: false) }
    var dinner by rememberSaveable { mutableStateOf(mealInfo?.dinner ?: false) }

    var cntbreakFast by rememberSaveable { mutableStateOf(mealInfo?.cntBreakFast ?: 0) }
    var cntlunch by rememberSaveable { mutableStateOf(mealInfo?.cntLunch ?: 0) }
    var cntdinner by rememberSaveable { mutableStateOf(mealInfo?.cntDinner ?: 0) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .height(screenHeight / 3f)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
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
                ),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Meal status",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
        }
        Divider(Modifier.padding(horizontal = 8.dp))
        Column(
            Modifier
                .weight(3f)
                .fillMaxWidth()
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
                SingleRowForMealInfo(isEnabled = false, title = title[it], unit = quantity, value = value,
                    onAmountChanged = {
                    },
                    onChange = { newValue ->
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
    unit: Int = 0,
    value: Boolean,
    onAmountChanged: (Int) -> Unit = {},
    onChange: (Boolean) -> Unit,
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
            modifier = Modifier.weight(1.5f),
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
            IconButton(onClick = {
                if (amount < 3) {
                    onIncrease()
                }
            },  modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                )
                .size(24.dp)) {
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