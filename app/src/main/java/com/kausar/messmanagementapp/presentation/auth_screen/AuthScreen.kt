package com.kausar.messmanagementapp.presentation.auth_screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.utils.CustomTextField
import com.kausar.messmanagementapp.utils.CustomToast
import com.kausar.messmanagementapp.utils.CustomTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSubmit: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var screenTitle by rememberSaveable {
        mutableStateOf(Screen.Login.title)
    }
    var isLoginScreen by rememberSaveable {
        mutableStateOf(true)
    }

    var showToast by rememberSaveable {
        mutableStateOf(false)
    }

    val state = viewModel.authState.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit)
    {
        if (state.value?.isSuccess != null && state.value?.isSuccess!!.isNotEmpty()) {
            onSubmit()
        }else{
            showToast = true
        }
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = screenTitle.toString(),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        CustomToast(
            showToast = showToast,
            successMessage = state.value?.isSuccess,
            errorMessage = state.value?.isError,
        ) {
            showToast = false
        }
        AuthScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            authState = state.value,
            isLoginScreen = isLoginScreen,
            buttonText = screenTitle.toString(),
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
            onSubmit = { phone, user ->
                if (isLoginScreen) {
                    scope.launch {
                        viewModel.registerUser(
                            context as Activity,
                            userName = "",
                            phoneNumber = "+8801315783246"
                        )
                    }

                }
            }
        )
    }

}

@Composable
fun AuthScreenContent(
    modifier: Modifier = Modifier,
    isLoginScreen: Boolean = true,
    buttonText: String,
    toggleScreen: () -> Unit,
    onSubmit: (contact: String, name: String?) -> Unit,
    authState: AuthState?,
) {
    val focusManager = LocalFocusManager.current

    var user by remember {
        mutableStateOf("")
    }
    var contactNo by remember {
        mutableStateOf("01315783246")
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
            AnimatedVisibility(visible = !isLoginScreen) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    input = user,
                    onInputChange = {
                        user = it
                    },
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
            }
            CustomTextField(
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
                    onSubmit(contactNo, user.ifEmpty { "No user" })
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                ),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = if (authState?.isLoading == true)
                        "Checking Credential"
                    else
                        buttonText.uppercase()
                )
                if (authState?.isLoading == true) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(color = Color.White)
                }

            }
            Box {
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
    AuthScreen {

    }
}