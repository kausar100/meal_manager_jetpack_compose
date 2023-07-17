package com.kausar.messmanagementapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun CustomProgressBar(msg: String) {

    Card(
        elevation = CardDefaults.cardElevation(40.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(),
        modifier = Modifier
            .fillMaxWidth(.95f)
            .padding(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(msg, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(16.dp))
            CircularProgressIndicator(
                color = Color.Blue,
                strokeWidth = 3.dp
            )
        }
    }

}