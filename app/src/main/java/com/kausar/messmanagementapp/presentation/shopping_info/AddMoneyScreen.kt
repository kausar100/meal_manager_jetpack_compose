package com.kausar.messmanagementapp.presentation.shopping_info

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.AddMoneyWithUser
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.shopping_info.shared.ChooseDate
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.shopping_info.shared.MoneyRow
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SharedShoppingInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.getTime
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddMoney(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    val memberInfo = mainViewModel.memberInfo.value

    val listInfo by remember {
        mutableStateOf(mutableListOf<AddMoney>())
    }

    var listSize by remember {
        mutableStateOf(0)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var save by remember {
        mutableStateOf(AddMoneyWithUser())
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            AddMoneyHeader(memberInfo.listOfMember, onCancel = {
                navController.popBackStack()
            }) { member, date, amount ->
                val currentTime = getTime(Calendar.getInstance())
                Log.d("time : ", currentTime)

                val info = AddMoney(
                    userName = member.userName,
                    date = date,
                    amount = amount
                )
                save = AddMoneyWithUser(user = member, info)
                showDialog = true
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                Modifier
                    .fillMaxWidth()
            ) {
                items(listSize) { index ->
                    MoneyRow(info = listInfo[index])
                }
            }
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    elevation = CardDefaults.elevatedCardElevation(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Review entry information",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DialogInformation(
                            title = "Member",
                            data = save.user.userName
                        )
                        DialogInformation(
                            title = "Date",
                            data = save.info.date
                        )

                        DialogInformation(
                            title = "Amount",
                            data = "${save.info.amount} Tk"
                        )

                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showDialog = false
                                }, shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Edit",
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            ElevatedButton(
                                onClick = {
                                    scope.launch {
                                        showDialog = false
                                        firestore.addMemberMoney(data = save).collectLatest {
                                            when (it) {
                                                is ResultState.Success -> {
                                                    showProgress = false
                                                    context.showToast(it.data)
                                                    listInfo.add(save.info)
                                                    listSize++
                                                    Log.d("AddMoney: ", listInfo.toString())
                                                }

                                                is ResultState.Failure -> {
                                                    showProgress = false
                                                    context.showToast(
                                                        it.message.localizedMessage
                                                            ?: "Some error occurred!"
                                                    )
                                                }

                                                is ResultState.Loading -> {
                                                    showProgress = true
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                Text(
                                    text = "Continue",
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }


                    }
                }
            }
        }
        if (showProgress) {
            CustomProgressBar("Adding money...")
        }
    }

}

@Composable
fun AddMoneyHeader(
    members: List<User>,
    onCancel: () -> Unit,
    addMoney: (User, String, String) -> Unit
) {

    val memberNames = SharedShoppingInfo.getNames(members)

    var amount by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

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

    val focusManager = LocalFocusManager.current

    var selectedDate by remember {
        mutableStateOf(getDate(Calendar.getInstance()))
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Add Money", fontWeight = FontWeight.ExtraBold)
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
            CustomOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                input = amount,
                onInputChange = {
                    amount = it
                },
                placeholder = { Text(text = "Enter Amount") },
                prefixIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_money),
                        contentDescription = "amount"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                )
            ) {
                focusManager.clearFocus(true)
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
                        if (amount.isEmpty()) {
                            context.showToast("Please enter receiving amount!")
                        } else {
                            addMoney(selectedMember, selectedDate, amount)
                        }

                    },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = "Add Money",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

    }

}