package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.data.model.listAddMoney
import com.kausar.messmanagementapp.presentation.meal_info_list.ShowUser
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.shopping_info.shared.MoneyInfo
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchCurrentMonthName

@Composable
fun AccountBalance(mainViewModel: MainViewModel, navController: NavHostController) {
    val memberInfo = mainViewModel.memberInfo.value

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(1f),
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
                    DialogInformation(title = "Total meal", data = "244")
                    DialogInformation(title = "Total Shopping cost", data = "17000")
                    DialogInformation(title = "Cost per meal", data = "60")
                    DialogInformation(title = "Remaining Money", data = "100")
                }

            }
            LazyColumn(Modifier.fillMaxHeight()) {
                items(memberInfo.listOfMember) { member ->
                    ShowUserInformation(user = member)
                }
            }
        }
    }
}

@Composable
fun ShowUserInformation(user: User) {
    val configuration = LocalConfiguration.current
    val widthInDp = configuration.screenWidthDp.dp

    var expand by remember { mutableStateOf(false) }
    Column(Modifier.animateContentSize(tween(500, easing = EaseInOut))) {
        ShowUser(userInfo = user, onClickUser = { expand = !expand }, onClickInfo = {})
        if (expand) {
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow {
                itemsIndexed(listAddMoney) { index, info ->
                    MoneyInfo(
                        modifier = Modifier
                            .width(widthInDp/2.15f)
                            .padding(
                                start = if (index == 0) 0.dp else 4.dp,
                                end = if (index == listAddMoney.lastIndex) 0.dp else 4.dp
                            ), info = info
                    )
                }
            }
        }
    }

}