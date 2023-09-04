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
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.shopping_info.shared.ChooseDate
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SharedShoppingInfo
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SingleMoney
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.getTime
import com.kausar.messmanagementapp.utils.showToast
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddMoney(mainViewModel: MainViewModel, navController: NavHostController) {

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
        mutableStateOf(AddMoney())
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                save = AddMoney(
                    userId = member.userId,
                    userName = member.userName,
                    date = date,
                    amount = amount
                )
                showDialog = true
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                Modifier
                    .fillMaxWidth()
            ) {
                items(listSize) { index ->
                    SingleMoney(info = listInfo[index])
                }
            }
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
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
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DialogInformation(
                            title = "Member",
                            data = save.userName
                        )
                        DialogInformation(
                            title = "Date",
                            data = save.date
                        )

                        DialogInformation(
                            title = "Amount",
                            data = "${save.amount} Tk"
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
                                    listInfo.add(save)
                                    listSize++
                                    Log.d("AddMoney: ", listInfo.toString())
                                    showDialog = false
                                },
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
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