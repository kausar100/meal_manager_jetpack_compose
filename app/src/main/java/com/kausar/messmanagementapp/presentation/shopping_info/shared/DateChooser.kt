package com.kausar.messmanagementapp.presentation.shopping_info.shared

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kausar.messmanagementapp.R
import java.util.Calendar
import java.util.Date

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
