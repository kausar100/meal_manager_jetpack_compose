package com.kausar.messmanagementapp.presentation.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.data.model.toMealInfo
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.fetchDateAsString
import java.util.Calendar

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val calendar = Calendar.getInstance()
    val today by rememberSaveable {
        mutableStateOf(fetchDateAsString(calendar))
    }
    val data = mainViewModel.todayTotalMealCnt.value

    LaunchedEffect(key1 = true) {
        if (mainViewModel.memberInfo.value.listOfMember.isEmpty()) {
            mainViewModel.getMessMembers()
        }else{
            mainViewModel.getMembersTodayMealCount()
        }
        if(mainViewModel.totalMealCntPerMember.value.isNotEmpty()){
            mainViewModel.getTotalMealCount()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = today,
                textAlign = TextAlign.Center
            )
            MealInformation(mealInfo = data.toMealInfo())

        }

    }

}

@Preview
@Composable
fun PreviewHome() {
    HomeScreen()
}