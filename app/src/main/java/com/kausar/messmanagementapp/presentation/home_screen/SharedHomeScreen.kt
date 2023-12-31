package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.AddMoney
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.meal_info_list.ShowUser
import com.kausar.messmanagementapp.presentation.shopping_info.shared.MoneyInfo
import com.kausar.messmanagementapp.presentation.shopping_info.shared.SharedShoppingInfo
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
    val moneyInfo = viewModel.addMoneyInfo.value
    val balance = mainViewModel.balanceInfo.value
    val totalMoneyPerMember = viewModel.totalMoneyPerMember
    val totalMealCntPerMember = mainViewModel.totalMealCntPerMember


    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    LaunchedEffect(key1 = isConnected) {
        if (userInfo.userType.isEmpty()) {
            mainViewModel.getUserInfo()
        }
        if (mealInfoState.success.isEmpty() && mealInfoState.error.isEmpty()) {
            viewModel.getMealForToday()
        }
        if (memberState.listOfMember.isEmpty()) {
            mainViewModel.getMessMembers()
        }


    }
    LaunchedEffect(key1 = userInfo) {
        if (userInfo.userType.isNotEmpty()) {
            viewModel.getMoneyInfo(userInfo)
        }
        mainViewModel.getBalanceInformation()
    }

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
                    .fillMaxWidth(1f), contentAlignment = Alignment.Center
            ) {
                CustomProgressBar(msg = progMsg)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (newMeal) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        Modifier
                            .clickable { showPopup = true }
                            .border(1.dp, Color.Transparent, RoundedCornerShape(4.dp))
                            .padding(8.dp)) {
                        Text(
                            text = presentingDate, textAlign = TextAlign.Center
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
                    AddNewMeal(onCancel = {
                        newMeal = false
                        selectedDate = getDate(temp)
                        presentingDate = fetchDateAsString(temp)
                    },
                        selectedDate = selectedDate,
                        viewModel = viewModel,
                        updateMeal = { breakfast, lunch, dinner, cntb, cntl, cntd ->
                            val tempSelectedDate = selectedDate
                            val tempPresentingDate = presentingDate
                            selectedDate = getDate(temp)
                            presentingDate = fetchDateAsString(temp)
                            newMeal = false
                            progMsg = "Updating meal info..."
                            showToast = true

                            scope.launch {
                                viewModel.update(
                                    MealInfo(
                                        tempSelectedDate,
                                        getDayName(tempPresentingDate),
                                        breakfast,
                                        lunch,
                                        dinner,
                                        cntb,
                                        cntl,
                                        cntd
                                    )
                                ).collectLatest { result ->
                                    when (result) {
                                        is ResultState.Success -> {
                                            showToast = false
                                            context.showToast(result.data)
                                        }

                                        is ResultState.Failure -> {
                                            showToast = false
                                            result.message.localizedMessage?.let { msg ->
                                                context.showToast(
                                                    msg
                                                )
                                            }
                                        }

                                        is ResultState.Loading -> {

                                        }
                                    }
                                }
                            }
                        }) { breakfast, lunch, dinner, cntb, cntl, cntd ->
                        newMeal = false
                        progMsg = "Inserting new meal..."
                        showToast = true
                        scope.launch {
                            viewModel.insert(
                                MealInfo(
                                    selectedDate,
                                    getDayName(presentingDate),
                                    breakfast,
                                    lunch,
                                    dinner,
                                    cntb,
                                    cntl,
                                    cntd
                                )
                            ).collectLatest { result ->
                                when (result) {
                                    is ResultState.Success -> {
                                        showToast = false
                                        context.showToast(result.data)
                                    }

                                    is ResultState.Failure -> {
                                        showToast = false
                                        result.message.localizedMessage?.let { msg ->
                                            context.showToast(
                                                msg
                                            )
                                        }
                                    }

                                    is ResultState.Loading -> {

                                    }
                                }
                            }
                        }
                    }
                } else {
                    ReviewInformation(
                        user = userInfo,
                        moneyInfo = moneyInfo.info,
                        isLoading = moneyInfo.isLoading || !totalMoneyPerMember.containsKey(
                            userInfo.userId
                        ) || !totalMealCntPerMember.containsKey(
                            userInfo.userId
                        ),
                        mealCnt = totalMealCntPerMember[userInfo.userId]?.total.toString(),
                        mealRate = balance.mealRate,
                        mealCost = SharedShoppingInfo.getMealCost(
                            totalMealCntPerMember[userInfo.userId]?.total.toString(),
                            balance.mealRate
                        ),
                        depositAmount = totalMoneyPerMember[userInfo.userId].toString()
                    )

                    if (mealInfoState.success.isNotEmpty()) {
                        Text(
                            text = today, style = MaterialTheme.typography.bodyLarge
                        )
                        MealInformation(
                            mealInfo = mealInfoState.meal,
                        )
                    }
                    ElevatedButton(
                        onClick = {
                            newMeal = true
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Add / Edit Meal",
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    ShowMessage(mealInfoState)
                }

            }
        }

    }
}

@Composable
fun ReviewInformation(
    user: User,
    moneyInfo: List<AddMoney>,
    isLoading: Boolean = false,
    mealCnt: String = "0.0",
    mealRate: String = "0.0",
    mealCost: String = "0.0",
    depositAmount: String = "0.0"
) {
    val configuration = LocalConfiguration.current
    val widthInDp = configuration.screenWidthDp.dp

    var expand by remember { mutableStateOf(false) }
    Column(
        Modifier
            .animateContentSize(tween(500, easing = EaseInOut))
    ) {
        ShowUser(userInfo = user, showInfo = false, expand = expand, onClickUser = {
            expand = !expand
        })
        if (expand) {
            Column(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SingleRow(title = "Number of meal", data = mealCnt)
                SingleRow(title = "Meal rate", data = mealRate)
                SingleRow(title = "Meal cost", data = mealCost)
                SingleRow(title = "Total deposit amount", data = depositAmount)
                Spacer(modifier = Modifier.height(8.dp))
                if (moneyInfo.isNotEmpty()) {
                    LazyRow {
                        itemsIndexed(moneyInfo) { index, info ->
                            MoneyInfo(
                                modifier = Modifier
                                    .width(widthInDp / 2.4f)
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
                        } else {
                            Text(text = "No Balance found!")
                        }
                    }
                }

            }

        }
    }

}

@Composable
fun SingleRow(title: String, data: String) {
    Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = data, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ShowMessage(mealInfoState: FirebaseFirestoreDbViewModel.SingleMeal) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(1f), contentAlignment = Alignment.Center
    ) {
        if (mealInfoState.isLoading) {
            CustomProgressBar(msg = "Fetching meal info...")
        } else if (mealInfoState.error.isNotEmpty()) {
            Text(mealInfoState.error, textAlign = TextAlign.Center)
        }

    }

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
        if (month != calendar[Calendar.MONTH]) break
        val date = fetchDateAsString(calendar)
        dates.add(date)

        val firestoreDate = getDate(calendar)
        firestoreDates.add(firestoreDate)
    }

    DropdownMenu(expanded = expanded, onDismissRequest = {
        expanded = false
        onDismiss()
    }) {

        repeat(dates.size) {
            DropdownMenuItem(text = {
                Text(text = dates[it])
            }, onClick = {
                expanded = false
                onSelect(dates[it], firestoreDates[it])
            })
        }
    }

}

@Preview
@Composable
fun PreviewSharedHome() {
    SharedHomeScreen()
}