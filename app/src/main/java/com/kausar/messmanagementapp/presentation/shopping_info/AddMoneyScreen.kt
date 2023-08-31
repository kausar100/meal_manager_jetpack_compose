package com.kausar.messmanagementapp.presentation.shopping_info

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomDropDownMenu
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.data.model.AddMoneyModel
import com.kausar.messmanagementapp.data.model.Demo
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun AddMoney(mainViewModel: MainViewModel, navController: NavHostController) {

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
            AddMoneyHeader(memberInfo.listOfMember, onCancel = {
                navController.popBackStack()
            }) { member, date, amount ->
                //need to add this money

            }
            Spacer(modifier = Modifier.height(8.dp))
            AddMoneyContent()
        }
    }

}

fun getNames(members: List<User>): List<String> {
    val names = mutableListOf<String>()
    if (members.isNotEmpty()) {
        for (member in members) {
            names.add(member.userName)
        }
    }
    return names

}

@Composable
fun AddMoneyContent() {

    LazyColumn(
        Modifier
            .fillMaxWidth()
    ) {

        items(Demo.listOfTestAddMoneyModel) { info ->
            SingleMoney(info)
        }
    }


}

fun getUser(members: List<User>, name: String): User {
    var selectedMember = User()
    for (member in members) {
        if (member.userName == name) {
            selectedMember = member
            break
        }
    }
    return selectedMember
}

@Composable
fun SingleMoney(info: AddMoneyModel) {
    Card(
        Modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column() {
                Text(text = info.date)
                Text(text = info.userName)
            }
            Text(text = info.amount)
        }
    }

}

@Composable
fun AddMoneyHeader(
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
            Text(text = "Add Money", fontWeight = FontWeight.ExtraBold)
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
                        text = "Add Money",
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

    }

}

@Composable
fun ChooseDate(modifier: Modifier, date: String, onChoose: (String) -> Unit) {
    val context = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    var selectedDate by remember {
        mutableStateOf(date)
    }

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            selectedDate = "$mDayOfMonth/${mMonth + 1}/$mYear"
            onChoose(selectedDate)
        }, mYear, mMonth, mDay
    )

    Row(
        modifier = modifier.clickable {
            mDatePickerDialog.show()
        },
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = "select date"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = date.ifEmpty { "Enter Date" })
    }
}
