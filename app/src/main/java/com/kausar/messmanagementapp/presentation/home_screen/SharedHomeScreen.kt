package com.kausar.messmanagementapp.presentation.home_screen

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.fetchDateAsString
import com.kausar.messmanagementapp.utils.getDate
import com.kausar.messmanagementapp.utils.getDayName
import com.kausar.messmanagementapp.utils.network_connection.ConnectionState
import com.kausar.messmanagementapp.utils.network_connection.connectivityState
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun SharedHomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
) {
    val calendar = Calendar.getInstance()
    val temp = Calendar.getInstance()
    temp.add(Calendar.DATE, 1)
    val today by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }

    var presentingDate by rememberSaveable {
        mutableStateOf(fetchDateAsString(temp))
    }

    var selectedDate by rememberSaveable {
        mutableStateOf(getDate(temp))
    }

    var newMeal by remember {
        mutableStateOf(false)
    }

    var progMsg by remember {
        mutableStateOf("")
    }

    var showToast by remember {
        mutableStateOf(false)
    }

    var showPopup by remember {
        mutableStateOf(false)
    }

    val userInfo = mainViewModel.userInfo.value
    val mealInfoState = viewModel.mealInfo.value
    val memberState = mainViewModel.memberInfo.value

    val mealCnt = mainViewModel.count.value

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    LaunchedEffect(key1 = isConnected) {
        if (userInfo.userType.isEmpty()) {
            mainViewModel.getUserInfo()
        }
        viewModel.getMealForToday()
        if (memberState.listOfMember.isEmpty()) {
            mainViewModel.getMessMembers()
        }
    }

    Log.d("SharedHomeScreen: ", "${memberState.listOfMember}")

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (showToast) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f),
                contentAlignment = Alignment.Center
            ) {

                CustomProgressBar(msg = progMsg)

            }
        }
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (newMeal) {
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    Modifier
                        .clickable { showPopup = true }
                        .border(1.dp, Color.Transparent, RoundedCornerShape(4.dp))
                        .padding(8.dp)) {
                    Text(
                        text = presentingDate,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(imageVector = Icons.Default.Edit, contentDescription = "choose date")
                }
                if (showPopup) {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        PopUpOption(onDismiss = {
                            showPopup = !showPopup
                        }, onSelect = { show, insert ->
                            showPopup = false
                            presentingDate = show
                            selectedDate = insert
                        })
                    }
                }
            } else {
                if (userInfo.userType.isNotEmpty()) {
                    LaunchedEffect(key1 = true) {
                        viewModel.getAllMeal(userInfo.userId)
                    }
                    Text(
                        text = "Number of meal until today",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline
                    )
//                    MealInfoScreen(firestore = viewModel)
                    mealCnt.cnt?.let {
                        MealSummary(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(.4f),
                            totalMeal = it.total.toString(),
                            numberOfBreakfast = it.breakfast.toString(),
                            numberOfLunch = it.lunch.toString(),
                            numberOfDinner = it.dinner.toString()
                        )
                    }
                        ?: if (mealCnt.error.isNotEmpty()) {
                            Text(
                                text = mealCnt.error,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        } else {
                            CircularProgressIndicator(Modifier.padding(top = 8.dp))
                        }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = today,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (newMeal) {
                AddNewMeal(
                    modifier = Modifier
                        .fillMaxHeight(.80f)
                        .fillMaxWidth(1f),
                    selectedDate = selectedDate,
                    viewModel = viewModel,
                    onCancel = {
                        newMeal = false
                        selectedDate = getDate(temp)
                        presentingDate = fetchDateAsString(temp)
                    },
                    updateMeal = { breakfast, lunch, dinner ->
                        progMsg = "Updating meal info..."
                        showToast = true
                        scope.launch {
                            viewModel.update(
                                MealInfo(
                                    selectedDate,
                                    getDayName(presentingDate),
                                    breakfast,
                                    lunch,
                                    dinner
                                )
                            ).collectLatest { result ->
                                when (result) {
                                    is ResultState.Success -> {
                                        showToast = false
                                        context.showToast(result.data)
                                        viewModel.getAllMeal(userInfo.userId)
                                        selectedDate = getDate(temp)
                                        presentingDate = fetchDateAsString(temp)
                                        newMeal = false
                                    }

                                    is ResultState.Failure -> {
                                        showToast = false
                                        result.message.localizedMessage?.let { msg ->
                                            context.showToast(
                                                msg
                                            )
                                            selectedDate = getDate(temp)
                                            presentingDate = fetchDateAsString(temp)
                                            newMeal = false
                                        }
                                    }

                                    is ResultState.Loading -> {

                                    }
                                }
                            }
                        }
                    },
                    addMeal = { breakfast, lunch, dinner ->
                        progMsg = "Inserting new meal..."
                        showToast = true
                        scope.launch {
                            viewModel.insert(
                                MealInfo(
                                    selectedDate,
                                    getDayName(presentingDate),
                                    breakfast,
                                    lunch,
                                    dinner
                                )
                            ).collectLatest { result ->
                                when (result) {
                                    is ResultState.Success -> {
                                        showToast = false
                                        context.showToast(result.data)
                                        viewModel.getAllMeal(userInfo.userId)
                                        newMeal = false
                                    }

                                    is ResultState.Failure -> {
                                        showToast = false
                                        result.message.localizedMessage?.let { msg ->
                                            context.showToast(
                                                msg
                                            )
                                            newMeal = false
                                        }
                                    }

                                    is ResultState.Loading -> {

                                    }
                                }
                            }
                        }
                    }
                )
            } else {
                if (mealInfoState.success.isNotEmpty()) {
                    MealInformation(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(.7f)
                            .padding(16.dp),
                        mealInfo = mealInfoState.meal,
                    )
                }
                ShowMessage(mealInfoState)
            }

        }
        if (!newMeal) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                FloatingActionButton(
                    onClick = {
                        newMeal = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add meal",
                    )
                }
            }
        }
    }
}

@Composable
fun ShowMessage(mealInfoState: FirebaseFirestoreDbViewModel.SingleMeal) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(1f),
        contentAlignment = Alignment.Center
    ) {
        if (mealInfoState.isLoading) {
            CustomProgressBar(msg = "Fetching meal info...")
        } else if (mealInfoState.error.isNotEmpty()) {
            Text(mealInfoState.error, textAlign = TextAlign.Center)
        }

    }

}

@Composable
fun MealInformation(
    modifier: Modifier = Modifier,
    mealInfo: MealInfo?
) {
    var breakFast by rememberSaveable { mutableStateOf(mealInfo?.breakfast ?: false) }
    var lunch by rememberSaveable { mutableStateOf(mealInfo?.lunch ?: false) }
    var dinner by rememberSaveable { mutableStateOf(mealInfo?.dinner ?: false) }

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Meal time",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Meal status",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

        }
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title = listOf("Breakfast", "Lunch", "Dinner")
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                repeat(title.size) {
                    Text(text = title[it], textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                CustomCheckBox(isEnabled = false, isChecked = breakFast, onCheckChange = {
                    breakFast = it
                })
                CustomCheckBox(isEnabled = false, isChecked = lunch, onCheckChange = {
                    lunch = it
                })
                CustomCheckBox(isEnabled = false, isChecked = dinner, onCheckChange = {
                    dinner = it
                })
            }
        }
    }
}

@Composable
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isChecked: Boolean = false,
    onCheckChange: (Boolean) -> Unit
) {
    Checkbox(enabled = isEnabled,
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = { onCheckChange(it) })
}

@Composable
fun PopUpOption(onDismiss: () -> Unit, onSelect: (String, String) -> Unit) {
    var expanded by remember { mutableStateOf(true) }

    val calendar = Calendar.getInstance()
    val month = calendar[Calendar.MONTH]

    val dates = mutableListOf<String>()

    val firestoreDates = mutableListOf<String>()

    for (i in 1..3) {
        calendar.add(Calendar.DATE, 1)
        if (month != calendar[Calendar.MONTH])
            break
        val date = fetchDateAsString(calendar)
        dates.add(date)

        val firestoreDate = getDate(calendar)
        firestoreDates.add(firestoreDate)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
            onDismiss()
        }
    ) {

        repeat(dates.size) {
            DropdownMenuItem(
                text = {
                    Text(text = dates[it])
                },
                onClick = {
                    expanded = false
                    onSelect(dates[it], firestoreDates[it])
                }
            )
        }
    }

}

@Preview
@Composable
fun PreviewSharedHome() {
    SharedHomeScreen()
}