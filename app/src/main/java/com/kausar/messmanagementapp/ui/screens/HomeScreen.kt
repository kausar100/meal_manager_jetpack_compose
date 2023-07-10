package com.kausar.messmanagementapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kausar.messmanagementapp.ui.utils.CustomTopAppBar
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(user: String?) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = user ?: "Welcome To App!",
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                DateInfo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                MealInfo(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .padding(16.dp)
                )
            }
        }

    }

}

@Composable
fun MealInfo(modifier: Modifier = Modifier) {
    val breakFast by remember { mutableStateOf(true) }
    val lunch by remember { mutableStateOf(true) }
    val dinner by remember { mutableStateOf(true) }
    val roundBorder = Modifier
        .border(
            width = 1.dp,
            color = Color.Gray,
            shape = RoundedCornerShape(4.dp)
        )
        .padding(8.dp)
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = roundBorder
        ) {
            Text(text = "Breakfast")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(
                isChecked = breakFast,
                onCheckChange = {}
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = roundBorder
        ) {
            Text(text = "Lunch")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(
                isChecked = lunch,
                onCheckChange = {}
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = roundBorder
        ) {
            Text(text = "Dinner")
            Spacer(modifier = Modifier.weight(1f))
            CustomCheckBox(
                isChecked = dinner,
                onCheckChange = {}
            )

        }
    }
}

@Composable
fun CustomCheckBox(
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    onCheckChange: (Boolean) -> Unit
) {
    Checkbox(
        enabled = isEnabled,
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = { onCheckChange(it) }
    )
}

@Composable
fun DateInfo(modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }

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

    LaunchedEffect(key1 = Unit) {
        selectedDateText = "$dayName, $dayOfMonth/${month + 1}/$year"
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Meal Status",
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "previous_day")

            }
            Text(text = selectedDateText)
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "next_day"
                )

            }

        }
    }

}

@Preview
@Composable
fun PreviewHome() {
    HomeScreen(user = "")
}