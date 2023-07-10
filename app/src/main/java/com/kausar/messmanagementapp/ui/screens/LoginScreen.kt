package com.kausar.messmanagementapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.ui.utils.CustomTextField
import com.kausar.messmanagementapp.ui.utils.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(gotoSignupScreen: () -> Unit, onLogin: (String)->Unit) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Log in",
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LoginScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            navigateToSignup = gotoSignupScreen,
            onPressLogin = onLogin
        )
    }

}

@Composable
fun LoginScreenContent(modifier: Modifier = Modifier, navigateToSignup: () -> Unit, onPressLogin: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var contactNo by remember {
            mutableStateOf("")
        }

        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            input = contactNo,
            onInputChange = {
                contactNo = it
            },
            placeholder = { Text(text = "Enter your contact number") },
            label = { Text(text = "Contact Number") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        ) {
            focusManager.moveFocus(FocusDirection.Next)
        }

        var password by remember {
            mutableStateOf("")
        }

        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            input = password,
            onInputChange = {
                password = it
            },
            placeholder = { Text(text = "Enter your password") },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        ) {
            focusManager.clearFocus(true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = { onPressLogin("Kausar") },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F51B5)
            ),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(text = "Log in".uppercase())
        }

        TextButton(onClick = navigateToSignup) {
            Text(text = buildAnnotatedString {
                append("Don't Have an Account! ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF3F51B5),
                        fontSize = 16.sp
                    )
                ) {
                    append("Create here...")
                }
            }, color = Color.Black, fontSize = 16.sp)
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(gotoSignupScreen = { /*TODO*/ }, onLogin = {})
}