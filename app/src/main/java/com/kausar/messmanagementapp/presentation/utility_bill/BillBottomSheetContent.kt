package com.kausar.messmanagementapp.presentation.utility_bill

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.data.model.AddUtilityBill
import com.kausar.messmanagementapp.data.model.AddUtilityBillWithUser
import com.kausar.messmanagementapp.data.model.BillInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.shopping_info.shared.ChooseDate
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SharedShoppingInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.getDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillSheetContent(
    mainViewModel: MainViewModel,
    onDone: () -> Unit,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    val memberInfo = mainViewModel.memberInfo.value

    val listInfo by remember {
        mutableStateOf(mutableListOf<AddUtilityBill>())
    }

    var listSize by remember {
        mutableStateOf(0)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var save by remember {
        mutableStateOf(AddUtilityBillWithUser())
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val bills = (1..3).map { "name$it" }
        LazyColumn() {
            item {
                AddUtilityBillHeader(
                    members = memberInfo.listOfMember,
                    billsName = bills,
                    onCancel = {},
                    addUtilityBill = { user, addUtilityBill ->
                        Log.d("TAG", addUtilityBill.toString())
                        onDone()
                    })
            }
        }

    }

}

@Composable
fun AddUtilityBillHeader(
    members: List<User>,
    billsName: List<String>,
    onCancel: () -> Unit,
    addUtilityBill: (User, AddUtilityBill) -> Unit
) {

    val memberNames = SharedShoppingInfo.getNames(members)

    val billStatus = mutableListOf<BillInfo>()

    var memberName by remember {
        mutableStateOf(if (memberNames.isNotEmpty()) memberNames[0] else "")
    }

    var selectedMember by remember {
        mutableStateOf(
            if (members.isNotEmpty()) {
                members[0]
            } else {
                User()
            }
        )
    }

    var selectedDate by remember {
        mutableStateOf(getDate(Calendar.getInstance()))
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Add Utility Bill", fontWeight = FontWeight.ExtraBold)
            CustomDropDownMenu(
                title = "Select Member Name",
                items = memberNames,
                selectedItem = memberName,
                onSelect = {
                    memberName = it
                    selectedMember = SharedShoppingInfo.getUser(members, memberName)
                })
            ChooseDate(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp),
                date = selectedDate
            ) {
                selectedDate = it
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(4.dp), modifier = Modifier.border(
                    1.dp, MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(8.dp)
                )
            ) {
                items(billsName.size) { index ->
                    billStatus.add(index, BillInfo(billName = billsName[index]))
                    ModifyBillStatus(billTitle = billsName[index]) { status ->
                        billStatus[index] = billStatus[index].copy(paymentStatus = status)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel, shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Cancel",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                ElevatedButton(
                    onClick = {
                        val information = AddUtilityBill(
                            userName = selectedMember.userName,
                            date = selectedDate,
                            billInfo = billStatus
                        )
                        addUtilityBill(selectedMember, information)
                    },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = "Add Utility Bill",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

    }

}

@Composable
fun ModifyBillStatus(billTitle: String, onItemClick: (Boolean) -> Unit = {}) {
    var status by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp,MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = billTitle,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Checkbox(checked = status, onCheckedChange = {
                status = it
                onItemClick(it)
            })

        }

    }
}