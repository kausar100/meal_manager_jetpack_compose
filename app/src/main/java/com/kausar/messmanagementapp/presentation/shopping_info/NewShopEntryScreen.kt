package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.Shopping
import com.kausar.messmanagementapp.data.model.ShoppingItem
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.shopping_info.shared.ChooseDate
import com.kausar.messmanagementapp.presentation.shopping_info.shared.DialogInformation
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SharedShoppingInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun NewShopEntry(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {
    val memberInfo = mainViewModel.memberInfo.value

    val memberNames = SharedShoppingInfo.getNames(memberInfo.listOfMember)

    var amount by remember {
        mutableStateOf("")
    }

    var memberName by remember {
        mutableStateOf(if (memberNames.isNotEmpty()) memberNames[0] else "")
    }

    var selectedMember by remember {
        mutableStateOf(
            if (memberInfo.listOfMember.isNotEmpty()) {
                memberInfo.listOfMember[0]
            } else {
                User()
            }
        )
    }

    var selectedDate by remember {
        mutableStateOf(getDate(Calendar.getInstance()))
    }

    var itemInformation by remember {
        mutableStateOf(mutableListOf<ShoppingItem>())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "New Shop Entry", fontWeight = FontWeight.ExtraBold)
                        if (amount.isNotEmpty()) {
                            Text(
                                text = "Cost : $amount Tk"
                            )
                        }

                    }

                    CustomDropDownMenu(title = "Select Member Name",
                        items = memberNames,
                        selectedItem = memberName,
                        onSelect = {
                            memberName = it
                            selectedMember =
                                SharedShoppingInfo.getUser(memberInfo.listOfMember, memberName)
                        })
                    ChooseDate(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp)
                            )
                            .padding(16.dp), date = selectedDate
                    ) {
                        selectedDate = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ShoppingItemInfo {
                        itemInformation = it
                        amount = calculateCost(it)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                navController.popBackStack()
                            }, shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Cancel", letterSpacing = 2.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedButton(
                            onClick = {
                                showDialog = true
                            },
                            shape = RoundedCornerShape(4.dp),
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
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.fillMaxHeight(.8f),
                    elevation = CardDefaults.elevatedCardElevation(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Review entry information",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            DialogInformation(
                                title = "Member", data = selectedMember.userName
                            )
                            DialogInformation(
                                title = "Date", data = selectedDate
                            )

                            if (itemInformation.isNotEmpty()) {
                                Text(
                                    text = "Item Details",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .weight(2f)
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(itemInformation) { info ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = info.name)

                                    Text(text = info.unit)

                                    Text(text = info.price)

                                }
                            }
                        }
                        AnimatedVisibility(itemInformation.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total cost", fontWeight = FontWeight.Bold)
                                Text(text = amount, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(
                            Modifier
                                .weight(1f)
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
                                        firestore.addNewShopping(
                                            user = selectedMember, data = Shopping(
                                                userName = selectedMember.userName,
                                                date = selectedDate,
                                                itemDetails = itemInformation,
                                                totalCost = amount
                                            )
                                        ).collectLatest {
                                            when (it) {
                                                is ResultState.Success -> {
                                                    showProgress = false
                                                    context.showToast(it.data)
                                                    //need to update shopping cost
                                                    mainViewModel.addShoppingCostToBalance(amount.toDouble())
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
            CustomProgressBar("Adding shopping...")
        }
    }

}

fun calculateCost(itemInfo: MutableList<ShoppingItem>): String {
    var amount = 0.0
    for (item in itemInfo) {
        if (item.price.isNotEmpty()) {
            amount += item.price.toDouble()
        }
    }
    return if (amount == 0.0) "" else amount.toString()
}

@Composable
fun ShoppingItemInfo(info: (MutableList<ShoppingItem>) -> Unit) {

    val rows by remember {
        mutableStateOf(mutableListOf<ShoppingItem>())
    }

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var rowId by remember {
        mutableIntStateOf(0)
    }

    fun addItem() {
        rows.add(rowId, ShoppingItem())
        rowId++
    }

    fun removeItem() {
        rows.removeLast()
        rowId--
        info(rows)
    }
    Column(Modifier.fillMaxHeight(.7f)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                addItem()
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add desc")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Add Item information")
            }
            AnimatedVisibility(rowId != 0) {
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = "remove desc",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable {
                        if (rowId >= 1) {
                            removeItem()
                        }
                    })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            Modifier.animateContentSize(tween(500, easing = EaseInOut)), state = lazyListState
        ) {
            scope.launch {
                if (rowId > 0) {
                    lazyListState.animateScrollToItem(rowId - 1)
                }
            }
            repeat(rowId) { index ->
                item {
                    SingleRow(rows[index]) { new ->
                        rows[index] = new
                        if (rows[index].name.isNotEmpty()) {
                            info(rows)
                        }
                    }
                }

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleRow(item: ShoppingItem, onChanged: (ShoppingItem) -> Unit) {
    val focusManager = LocalFocusManager.current

    var value by remember {
        mutableStateOf(item)
    }

    Row(Modifier.fillMaxWidth()) {
        OutlinedTextField(value = value.name,
            onValueChange = {
                value = value.copy(name = it)
                onChanged(value)
            },
            modifier = Modifier
                .weight(1.3f)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            singleLine = true,
            label = { Text(text = "Name") },
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            })
        )
        OutlinedTextField(value = value.unit,
            onValueChange = {
                value = value.copy(unit = it)
                onChanged(value)
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            singleLine = true,
            label = { Text(text = "Unit") },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Next)
            })
        )
        OutlinedTextField(value = value.price,
            onValueChange = {
                value = value.copy(price = it)
                onChanged(value)
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            singleLine = true,
            label = { Text(text = "Price") },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(true)
            })
        )
    }
}
