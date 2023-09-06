package com.kausar.messmanagementapp.presentation.shopping_info.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.data.model.AddMoney

@Composable
fun MoneyRow(info: AddMoney) {
    Card(
        Modifier.padding(vertical = 4.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = info.date)
                Text(text = info.userName)
            }
            Text(text = "${info.amount} Tk")
        }
    }

}

@Composable
fun MoneyInfo(modifier: Modifier = Modifier, info: AddMoney) {
    Card(
        modifier.padding(vertical = 8.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = info.date, fontSize = 12.sp)
            Text(text = "${info.amount} Tk", fontSize = 12.sp)
        }
    }

}
