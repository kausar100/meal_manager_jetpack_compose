package com.kausar.messmanagementapp.presentation.shopping_info

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.FieldWithIncDec
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@Composable
fun NewShopEntry(mainViewModel: MainViewModel, navController: NavHostController) {
    val memberInfo = mainViewModel.memberInfo.value

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            NewShopEntryHeader(memberInfo.listOfMember, onCancel = {
                navController.popBackStack()
            }) { member, date, amount ->
                //need to add this money

            }
            Spacer(modifier = Modifier.height(8.dp))
//            NewShopEntryContent()
        }
    }

}

@Composable
fun NewShopEntryHeader(
    members: List<User>,
    onCancel: () -> Unit,
    addMoney: (User, String, String) -> Unit
) {

    val memberNames = getNames(members)

    var amount by remember {
        mutableStateOf("")
    }

    var memberName by remember {
        mutableStateOf(if (memberNames.isNotEmpty()) memberNames[0] else "")
    }

    var selectedMember by remember {
        mutableStateOf(User())
    }

    val focusManager = LocalFocusManager.current

    var selectedDate by remember {
        mutableStateOf("")
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
            Text(text = "New Shop Entry", fontWeight = FontWeight.ExtraBold)
            CustomDropDownMenu(
                title = "Select Member Name",
                items = memberNames,
                selectedItem = memberName,
                onSelect = {
                    memberName = it
                    selectedMember = getUser(members, memberName)
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
            AddBajarNameAndPrice()
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
                        addMoney(selectedMember, selectedDate, amount)
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                ) {
                    Text(
                        text = "Add Shop Entry",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

    }

}

data class Item(
    val name: String = "",
    val weight: String = "",
    val price: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBajarNameAndPrice() {
    val context = LocalContext.current
    var expand by remember {
        mutableStateOf(false)
    }

    val listOfItems = mutableListOf<Item>()

    var item by remember {
        mutableStateOf(Item())
    }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.animateContentSize(tween(500, easing = EaseInOut))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.clickable {
                expand = !expand
            }
        ) {
            IconButton(onClick = {
                expand = true
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add desc")

            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Add Item information")

        }
        if (expand) {
            Row(Modifier.fillMaxWidth()) {
                repeat(3) { index ->
                    val value = when (index) {
                        0 -> item.name
                        1 -> item.weight
                        else -> item.price
                    }

                    val label = when (index) {
                        0 -> "Name"
                        1 -> "Weight"
                        else -> "Price"
                    }
                    val space = when (index) {
                        0 -> 1.2f
                        else -> 1f
                    }

                    val keyBoardOption = when (index) {
                        0 -> KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )

                        1 -> KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )

                        else -> KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    }

                    val onFocusChange = {
                        when (index) {
                            2 -> focusManager.clearFocus(true)
                            else -> focusManager.moveFocus(FocusDirection.Next)
                        }

                    }

                    val onChange: (String) -> Unit = {
                        item = when (index) {
                            0 -> item.copy(name = it)
                            1 -> item.copy(weight = it)
                            else -> item.copy(price = it)
                        }
//                        if (item.name.isNotEmpty() && item.price.isNotEmpty()) {
//                            listOfItems.add(item)
//                        }
                    }

                    if (index == 1) {
                        FieldWithIncDec(
                            modifier = Modifier
                                .weight(space)
                                .padding(4.dp),
                            input = value,
                            label = label,
                            onValueChange = onChange,
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    onFocusChange()
                                },
                                onDone = {
                                    onFocusChange()
                                    Log.d("AddBajarNameAndPrice: ", listOfItems.toString())
                                }
                            ),
                            keyboardOptions = keyBoardOption
                        )

                    } else {
                        OutlinedTextField(
                            value = value,
                            onValueChange = onChange,
                            modifier = Modifier
                                .weight(space)
                                .padding(4.dp),
                            keyboardOptions = keyBoardOption,
                            singleLine = true,
                            label = { Text(text = label) },
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    onFocusChange()
                                },
                                onDone = {
                                    onFocusChange()
                                    Log.d("AddBajarNameAndPrice: ", listOfItems.toString())
                                }
                            )
                        )
                    }


                }


            }
        }


    }
}
