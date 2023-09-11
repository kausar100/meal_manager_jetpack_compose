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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.data.model.ShoppingScreenListInfo
import com.kausar.messmanagementapp.data.model.ShoppingListItem
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat

enum class ListType {
    List, Grid
}

@Composable
fun ShoppingScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    val memberInfo = mainViewModel.memberInfo.value
    val balance = mainViewModel.balanceInfo.value

    LaunchedEffect(key1 = Unit) {
        for (member in memberInfo.listOfMember) {
            firestore.getMoneyInfo(member)
        }
        mainViewModel.getBalanceInformation()
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = CardDefaults.elevatedCardElevation(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    DialogInformation(title = "Date", data = fetchCurrentMonthName())
                    DialogInformation(title = "Total meal", data = balance.totalMeal)
                    DialogInformation(
                        title = "Total Shopping cost", data = balance.totalShoppingCost
                    )
                    DialogInformation(
                        title = "Total Receiving Amount", data = balance.totalReceivingAmount
                    )
                    DialogInformation(
                        title = "Cost per meal",
                        data = getCostPerMeal(balance.totalMeal, balance.totalShoppingCost)
                    )
                    DialogInformation(title = "Remaining Money", data = balance.remainingAmount)
                }

            }
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

fun getCostPerMeal(totalMeal: String, totalShoppingCost: String): String {
    var cost: String
    try {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        cost =
            df.format(totalShoppingCost.ifEmpty { "0.0" }.toDouble() / totalMeal.ifEmpty { "0.0" }
                .toDouble())
    } catch (e: Exception) {
        cost = "0.0"
    }
    return cost
}

@Composable
fun MenuItem(item: ShoppingScreenListInfo, gridView: Boolean = false, onItemClick: () -> Unit) {
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
                    .fillMaxSize()
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
                    .fillMaxSize()
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