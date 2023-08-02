package com.kausar.messmanagementapp.presentation.auth_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.CustomToast
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    onSubmit: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var screenTitle by rememberSaveable {
        mutableStateOf(
            if (mainViewModel.userName.value.isEmpty()) {
                Screen.SignUp.title
            } else {
                Screen.Login.title
            }
        )
    }
    var isLoginScreen by rememberSaveable {
        mutableStateOf(
            mainViewModel.userName.value.isNotEmpty()
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = screenTitle.toString(),
                canNavigateBack = false,
                canLogout = false,
                scrollBehavior = scrollBehavior
            ) {}
        }
    ) {
        AuthScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            isLoginScreen = isLoginScreen,
            buttonText = screenTitle.toString(),
            viewModel = mainViewModel,
            toggleScreen = {
                when (screenTitle) {
                    Screen.Login.title -> {
                        screenTitle = Screen.SignUp.title
                        isLoginScreen = false
                    }

                    Screen.SignUp.title -> {
                        screenTitle = Screen.Login.title
                        isLoginScreen = true
                    }
                }
            },
            onSubmit = { phone, name ->
                if (mainViewModel.userName.value.isEmpty()){
                    mainViewModel.saveUsername(name)
                    mainViewModel.saveContact(phone)
                }
                onSubmit(phone)
            }
        )
    }

}

@Composable
fun AuthScreenContent(
    modifier: Modifier = Modifier,
    isLoginScreen: Boolean = true,
    buttonText: String,
    viewModel: MainViewModel,
    toggleScreen: () -> Unit,
    onSubmit: (contact: String, name: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var user by remember {
        mutableStateOf(viewModel.userName.value)
    }
    var contactNo by remember {
        mutableStateOf(viewModel.contact.value)
    }

    var showToast by rememberSaveable {
        mutableStateOf(false)
    }
    var errMsg by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var successMsg by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                input = user,
                onInputChange = {
                    user = it
                },
                editable = !isLoginScreen,
                placeholder = { Text(text = "Enter your name") },
                label = { Text(text = "Name") },
                prefixIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "user"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            ) {
                focusManager.moveFocus(FocusDirection.Next)
            }
            CustomOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                input = contactNo,
                onInputChange = {
                    contactNo = it
                },
                placeholder = { Text(text = "Enter your contact number") },
                label = { Text(text = "Contact Number") },
                prefixIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "contact number"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            ) {
                focusManager.clearFocus(true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedButton(
                onClick = {
                    if (contactNo.isBlank() || contactNo.isEmpty()) {
                        errMsg = "PLease enter your contact number"
                        successMsg = null
                        showToast = true

                    } else if (user.isBlank() || user.isEmpty()) {
                        errMsg = "Please enter username"
                        successMsg = null
                        showToast = true

                    } else {
                        onSubmit(contactNo, user)
                    }

                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                ),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    buttonText, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
                )

            }
            Box(contentAlignment = Alignment.Center) {
                CustomToast(
                    showToast = showToast,
                    errorMessage = errMsg,
                    successMessage = successMsg
                ) {
                    showToast = false
                }

                if (isLoginScreen) {
                    TextButton(onClick = toggleScreen) {
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
                } else {
                    TextButton(onClick = toggleScreen) {
                        Text(text = buildAnnotatedString {
                            append("Already Have an Account! ")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF3F51B5),
                                    fontSize = 16.sp
                                )
                            ) {
                                append("Log in here...")
                            }
                        }, color = Color.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AuthScreen(onSubmit = {})
}