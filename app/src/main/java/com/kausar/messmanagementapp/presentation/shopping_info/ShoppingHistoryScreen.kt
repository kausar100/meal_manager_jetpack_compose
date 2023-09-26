package com.kausar.messmanagementapp.presentation.shopping_info

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.kausar.messmanagementapp.data.model.MemberShoppingList
import com.kausar.messmanagementapp.data.model.Shopping
import com.kausar.messmanagementapp.data.model.ShoppingItem
import com.kausar.messmanagementapp.presentation.meal_info_list.ShowUser
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShoppingHistory(
    mainViewModel: MainViewModel,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel(),
    onSubmit: (String) -> Unit
) {
    val memberInfo = mainViewModel.memberInfo.value

    val configuration = LocalConfiguration.current
    val widthInDp = configuration.screenWidthDp.dp

    val pagerState = rememberPagerState()

    val shoppingInfoPerMember = firestore.shoppingPerMember.value
    Log.d("shopping cost", firestore.shoppingCostSum.value)

    LaunchedEffect(key1 = Unit) {
        for (member in memberInfo.listOfMember) {
            firestore.getShoppingInfo(member)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                HorizontalPager(
                    count = firestore.shoppingList.size, state = pagerState
                ) {
                    SingleShoppingInformation(
                        modifier = Modifier.width(widthInDp - 32.dp), firestore.shoppingList[it]
                    )
                }
            }

            LazyColumn(Modifier.weight(2f)) {
                items(memberInfo.listOfMember) { member ->
                    ShowUser(userInfo = member,
                        showInfo = false,
                        expand = false,
                        expandable = false,
                        onClickUser = {
                            val shoppingList =
                                if (shoppingInfoPerMember.containsKey(member.userId)) shoppingInfoPerMember[member.userId]!!.info else emptyList()

                            val cost =
                                if (shoppingList.isNotEmpty()) shoppingInfoPerMember[member.userId]!!.totalCost else "0.0"

                            val data = Gson().toJson(
                                MemberShoppingList(
                                    info = shoppingList, totalCost = cost
                                )
                            )
                            onSubmit(data)
                        })
                }
            }

        }
    }
}

@Composable
fun SingleShoppingInformation(modifier: Modifier = Modifier, shopping: Shopping) {
    val configuration = LocalConfiguration.current
    val heightInDp = configuration.screenHeightDp.dp
    val widthInDp = configuration.screenWidthDp.dp - 32.dp

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        DialogInformation(title = "Date", data = shopping.date)
        DialogInformation(title = "Member", data = shopping.userName)
        DialogInformation(title = "Total Cost", data = shopping.totalCost)
        DialogInformation(title = "Item Details", data = "")
        LazyColumn(
            Modifier
                .height(heightInDp / 3f)
                .weight(1f)
        ) {
            items(shopping.itemDetails) { info ->
                ShoppingItemInfo(
                    modifier = modifier
                        .width(widthInDp)
                        .padding(horizontal = 4.dp), info = info
                )
            }
        }
    }
}

@Composable
fun ShoppingItemInfo(modifier: Modifier = Modifier, info: ShoppingItem) {
    Card(
        modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Item name: ${info.name}")
            Text(text = "Unit: ${info.unit}")
            Text(text = "Price: ${info.price} Tk")
        }

    }

}
