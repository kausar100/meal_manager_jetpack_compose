package com.kausar.messmanagementapp.presentation.utility_bill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@Composable
fun UtilityScreen(navController: NavHostController= rememberNavController(), mainViewModel: MainViewModel = viewModel(), firestore: FirebaseFirestoreDbViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center){
        //bill information provided by manager (show to everyone)
        //add bill by manager
        Button(onClick = {navController.navigate(Screen.AddUtilityBill.route)}) {
            Text(text = "Add Utility Bill Information")
        }
        //show bill information by specific user(paid, due and amount)
        //also show meal cost by month
    }
}