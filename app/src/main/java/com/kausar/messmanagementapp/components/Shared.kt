package com.kausar.messmanagementapp.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog


@Composable
fun CustomProgressBar(){
    Dialog(onDismissRequest = { /*TODO*/ }) {
        CircularProgressIndicator()
    }

}