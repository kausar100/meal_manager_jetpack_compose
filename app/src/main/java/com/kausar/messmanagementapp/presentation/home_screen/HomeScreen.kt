package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.data.model.MealInfo
import com.kausar.messmanagementapp.data.model.User
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
fun HomeScreen(
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

    var showMealInfoScreen by remember {
        mutableStateOf(false)
    }

    val userInfo = mainViewModel.userInfo.value
    val mealInfoState = viewModel.mealInfo.value

    val connection by connectivityState()
    val isConnected = (connection === ConnectionState.Available)

    LaunchedEffect(key1 = isConnected){
        if(userInfo.userType.isEmpty()){
            mainViewModel.getUserInfo()
        }
        viewModel.getMealForToday()
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.fillMaxHeight(.1f))

            if (newMeal) {
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
                        mainViewModel.getMessProfilePic(userInfo.messId, userInfo.messName)
                        viewModel.getAllMeal(userInfo.userId)
                    }
                    CustomListTile(
                        userData = userInfo,
                        picture = mainViewModel.messPicture.value
                    ) {
                        showMealInfoScreen = true

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = today, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                    modifier = Modifier.background(Color.Transparent, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add meal",
                        tint = Color.Black
                    )
                }
            }
        }

        if (showMealInfoScreen) {
            ShowMealInformationDialog(onDismiss = {
                showMealInfoScreen = false
            }, firestore = viewModel)

        }

    }
}

@Composable
fun ShowMealInformationDialog(onDismiss: () -> Unit, firestore: FirebaseFirestoreDbViewModel) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Number of Meal")
            },
            shape = RoundedCornerShape(8.dp),
            text = {
                MealInfoScreen(firestore = firestore)
            },
            confirmButton = {
                Button(

                    onClick = onDismiss,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {

            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListTile(userData: User, picture: String, onClickInfo: () -> Unit) {

    ListItem(
        headlineText = {
            Text(text = userData.messName, fontWeight = FontWeight.Bold)
        },
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable {
                onClickInfo()
            },
        leadingContent = {
            if (picture.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(picture)
                        .crossfade(true).build(),
                    placeholder = painterResource(id = R.drawable.ic_home),
                    contentDescription = stringResource(id = R.string.mess_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

            } else {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "profile",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, color = Color.Gray, shape = CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .clip(CircleShape)
                )
            }

        },
        trailingContent = {
            IconButton(onClick = onClickInfo) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "count_meal"
                )
            }
        },
        supportingText = { Text(text = userData.userType, fontWeight = FontWeight.Bold) },
    )

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
                    fontSize = 16.sp
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
                    width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .weight(2f)
        ) {
            val title = listOf("Breakfast", "Lunch", "Dinner")
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                repeat(title.size) {
                    Text(text = title[it], textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
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
fun PreviewHome() {
    HomeScreen()
}