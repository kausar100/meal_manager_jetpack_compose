package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.data.model.ShoppingInfo
import com.kausar.messmanagementapp.data.model.ShoppingListItem
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

enum class ListType {
    List, Grid
}

@Composable
fun ShoppingScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mainViewModel.listType.value == ListType.List) {
                LazyColumn {
                    items(ShoppingListItem.items) { item ->
                        MenuItem(item) {
                            when (item.title) {
                                ShoppingListItem.itemTitles[0] -> {
                                    navController.navigate(Screen.AddMoney.route)
                                }

                                ShoppingListItem.itemTitles[1] -> {
                                    navController.navigate(Screen.ShopEntry.route)
                                }

                                ShoppingListItem.itemTitles[2] -> {
                                    navController.navigate(Screen.Balance.route)
                                }

                                ShoppingListItem.itemTitles[3] -> {
                                    navController.navigate(Screen.ShoppingHistory.route)
                                }
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(ShoppingListItem.items) { item ->
                        MenuItem(item, true) {
                            when (item.title) {
                                ShoppingListItem.itemTitles[0] -> {
                                    navController.navigate(Screen.AddMoney.route)
                                }

                                ShoppingListItem.itemTitles[1] -> {
                                    navController.navigate(Screen.ShopEntry.route)
                                }

                                ShoppingListItem.itemTitles[2] -> {
                                    navController.navigate(Screen.Balance.route)
                                }

                                ShoppingListItem.itemTitles[3] -> {
                                    navController.navigate(Screen.ShoppingHistory.route)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItem(item: ShoppingInfo, gridView: Boolean = false, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable {
                onItemClick()
            },
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        if (gridView) {
            Column(
                Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = item.icon), contentDescription = item.desc)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )

            }

        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = item.icon), contentDescription = item.desc)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = item.title, modifier = Modifier.padding(8.dp))

            }
        }
    }
}