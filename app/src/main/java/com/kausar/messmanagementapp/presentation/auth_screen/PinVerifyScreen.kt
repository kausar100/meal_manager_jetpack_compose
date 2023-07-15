package com.kausar.messmanagementapp.presentation.auth_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.components.WelcomeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(userName: String, phoneNumber: String, onSubmit: (String) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var otp by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(topBar = {
        CustomTopAppBar(
            canNavigateBack = false, scrollBehavior = scrollBehavior
        ) {}
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeText(name = userName)
            Spacer(modifier = Modifier.fillMaxHeight(.1f))
            Text(
                text = "Please enter otp that will be sent to $phoneNumber",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            VerifyPinContent(otp = otp, onCodeEnter = {
                onSubmit(it)

            }, onChange = {
                otp = it
            })
        }
    }

}

@Composable
fun VerifyPinContent(onCodeEnter: (String) -> Unit, otp: String = "", onChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomOutlinedTextField(modifier = Modifier.fillMaxWidth(), input = otp, onInputChange = {
            onChange(it)
        }, placeholder = { Text(text = "Enter otp") }, prefixIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_pin),
                contentDescription = "otp",
                modifier = Modifier.background(color = Color.White)
            )
        }, label = { Text(text = "OTP") }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        )
        ) {
            focusManager.clearFocus(true)
        }
        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                onCodeEnter(otp)
            }, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F51B5)
            ), modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(text = "Verify OTP", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
    }


}