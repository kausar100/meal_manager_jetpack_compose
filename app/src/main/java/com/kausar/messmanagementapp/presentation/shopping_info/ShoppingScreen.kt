package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kausar.messmanagementapp.data.model.ShoppingInfo
import com.kausar.messmanagementapp.data.model.ShoppingListItem
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@Composable
fun ShoppingScreen(mainViewModel: MainViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn() {
                items(ShoppingListItem.items) { item ->
                    ShoppingListItem(item)
                }
            }

        }
    }
}

@Composable
fun ShoppingListItem(item: ShoppingInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = item.icon), contentDescription = item.desc)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = item.title, modifier = Modifier.padding(8.dp))

        }

    }
}