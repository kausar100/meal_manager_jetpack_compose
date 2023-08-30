package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AccountBalance() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Magenta)
    ) {
        Text(text = "account balance")

    }

}