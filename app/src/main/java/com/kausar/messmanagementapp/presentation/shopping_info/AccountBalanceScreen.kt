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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.meal_info_list.ShowUser
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.shopping_info.shared.MoneyInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@Composable
fun AccountBalance(
    mainViewModel: MainViewModel,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {
    val memberInfo = mainViewModel.memberInfo.value

    val moneyInfo = firestore.addMoneyInfo.value

    val totalMoneyPerMember = firestore.moneyPerMember.value

    val moneyListInfoPerMember = firestore.addMoneyListPerMember.value

    LaunchedEffect(key1 = Unit) {
        for (member in memberInfo.listOfMember) {
            firestore.getMoneyInfo(member)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(1f),
            elevation = CardDefaults.elevatedCardElevation(),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val members = memberInfo.listOfMember
                repeat(members.size) {
                    val amount =
                        if (totalMoneyPerMember.containsKey(members[it].userId)) totalMoneyPerMember[members[it].userId]!!.total else "0.0"
                    DialogInformation(title = members[it].userName, data = "$amount Tk")
                }
                DialogInformation(title = "Total", data = "${firestore.moneySum.value} Tk")
            }
        }

        LazyColumn(Modifier.fillMaxHeight()) {
            items(memberInfo.listOfMember) { member ->
                val addMoneyInfo =
                    if (moneyListInfoPerMember.containsKey(member.userId)) moneyListInfoPerMember[member.userId]!! else emptyList()

                ShowUserInformation(user = member, addMoneyInfo, isLoading = moneyInfo.isLoading) {
                    if (!moneyListInfoPerMember.containsKey(member.userId)) {
                        firestore.clearMoneyInfo()
                        firestore.getMoneyInfo(member)
                    }
                }
            }
        }
    }

}

@Composable
fun ShowUserInformation(
    user: User,
    moneyInfo: List<AddMoney>,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val widthInDp = configuration.screenWidthDp.dp

    var expand by remember { mutableStateOf(false) }
    Column(Modifier.animateContentSize(tween(500, easing = EaseInOut))) {
        ShowUser(
            userInfo = user,
            showInfo = false,
            expand = expand,
            onClickUser = {
                onClick()
                expand = !expand
            })
        if (expand) {
            Spacer(modifier = Modifier.height(4.dp))
            if (moneyInfo.isNotEmpty()) {
                LazyRow {
                    itemsIndexed(moneyInfo) { index, info ->
                        MoneyInfo(
                            modifier = Modifier
                                .width(widthInDp / 2.15f)
                                .padding(
                                    start = if (index == 0) 0.dp else 4.dp,
                                    end = if (index == moneyInfo.lastIndex) 0.dp else 4.dp
                                ), info = info
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    }else{
                        Text(text = "No information found!")
                    }
                }
            }
        }
    }

}