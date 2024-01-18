package com.kausar.messmanagementapp.presentation.utility_bill

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kausar.messmanagementapp.data.model.BillInfo
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtilityScreen(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = viewModel(),
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    val userData = mainViewModel.userInfo.value

    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo()
    }

    var showBillStatus by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            //add bill information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp)
                    .clickable {
                        navController.navigate(Screen.AddUtilityBill.route)
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Utility Bill Information",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add_bill")
                }
            }

            AnimatedVisibility(visible = !showBillStatus) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val bills = (1..3).map { BillInfo("bill name $it", (it * 100).toString()) }

                    //show bill information
                    Card {
                        LazyColumn {
                            item {
                                Text(
                                    text = "Utility Bill Information",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        letterSpacing = 2.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                                Divider(thickness = 1.dp)
                            }
                            items(bills) {
                                SingleBillInformation(it)
                            }
                        }
                    }

                    //payment status paid, due
                    Card {
                        Column {
                            Text(
                                text = "Utility Bill Status",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    letterSpacing = 2.sp
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            Divider(thickness = 1.dp)
                            LazyRow(contentPadding = PaddingValues(16.dp)) {
                                items(bills) {
                                    SingleBillStatus(Modifier.fillMaxHeight(.3f), it)
                                }
                            }
                        }
                    }

                    AddBill(modifier = Modifier.fillMaxWidth(1f)) {
                        showBillStatus = true
                    }
                }

            }

            AnimatedVisibility(visible = showBillStatus) {
                BillSheetContent(
                    mainViewModel = mainViewModel,
                    onDone = {
                        showBillStatus = false
                    },
                    firestore = firestore
                )

            }

        }

    }

}

@Composable
fun SingleBillInformation(info: BillInfo) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = info.billName, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = info.billCost + " Tk", style = MaterialTheme.typography.titleMedium)
    }

}

@Composable
fun SingleBillStatus(modifier: Modifier, info: BillInfo) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = modifier
                .wrapContentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = info.billName, style = MaterialTheme.typography.titleMedium)
            if (info.paymentStatus) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "done")

            } else {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "due")
            }

        }
    }


}

@Composable
fun AddBill(modifier: Modifier,onItemClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .clickable {
                onItemClick()
            },
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth(1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add User Bill",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Icon(imageVector = Icons.Default.Add, contentDescription = "utility_bill")
        }

    }
}