package com.kausar.messmanagementapp.presentation.utility_bill

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.BillInfo
import com.kausar.messmanagementapp.presentation.shopping_info.calculateBillCost
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun BillInfoScreen(mainViewModel: MainViewModel,
                   navController: NavHostController,
                   firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {

    var amount by remember {
        mutableStateOf("")
    }

    var utilityBillInformation by remember {
        mutableStateOf(mutableListOf<BillInfo>())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    var billInfoSubmitted by remember {
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
            Modifier.wrapContentSize()
        ) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    Modifier.padding(16.dp).wrapContentSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "New Utility Bill Entry", fontWeight = FontWeight.ExtraBold)
                        if (amount.isNotEmpty()) {
                            Text(
                                text = "Cost : $amount Tk"
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    BillItemInfo(clear = billInfoSubmitted) {
                        utilityBillInformation = it
                        amount = calculateBillCost(it)
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
                                text = "Add Bill Entry",
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
                    modifier = Modifier.fillMaxHeight(.7f),
                    elevation = CardDefaults.elevatedCardElevation(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Review entry information",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            if (utilityBillInformation.isNotEmpty()) {
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
                            items(utilityBillInformation) { info ->
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
                                    Text(text = info.billName)

                                    Text(text = info.billCost)

                                }
                            }
                        }
                        AnimatedVisibility(utilityBillInformation.isNotEmpty()) {
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
                                        utilityBillInformation.clear()
                                        billInfoSubmitted = true
                                        amount = ""
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
            CustomProgressBar("Adding Utility...")
        }
    }
}

@Composable
fun BillItemInfo(clear: Boolean = false,info: (MutableList<BillInfo>) -> Unit) {

    val rows by remember {
        mutableStateOf(mutableListOf<BillInfo>())
    }

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var rowId by remember {
        mutableIntStateOf(0)
    }

    if(clear){
        rows.clear()
        rowId=0
    }

    fun addItem() {
        rows.add(rowId, BillInfo())
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
                Text(text = "Add Bill information")
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
                    SingleBillRow(rows[index]) { new ->
                        rows[index] = new
                        if (rows[index].billName.isNotEmpty()) {
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
fun SingleBillRow(item: BillInfo, onChanged: (BillInfo) -> Unit) {
    val focusManager = LocalFocusManager.current

    var value by remember {
        mutableStateOf(item)
    }

    Row(Modifier.fillMaxWidth()) {
        OutlinedTextField(value = value.billName,
            onValueChange = {
                value = value.copy(billName = it)
                onChanged(value)
            },
            modifier = Modifier
                .weight(1.3f)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            singleLine = true,
            label = { Text(text = "Bill Name") },
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            })
        )
        OutlinedTextField(value = value.billCost,
            onValueChange = {
                value = value.copy(billCost = it)
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
