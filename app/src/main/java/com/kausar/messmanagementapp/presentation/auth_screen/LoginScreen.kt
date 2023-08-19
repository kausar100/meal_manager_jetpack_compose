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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.CustomToast
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
    gotoRegistrationScreen: ()->Unit,
    onSubmit: (String, String) -> Unit
) {
    var showAboutScreen by remember {
        mutableStateOf(false)
    }

    Box(Modifier.fillMaxSize()) {
        if (showAboutScreen) {
            AboutScreen(onClose = {
                showAboutScreen = false
            })
        } else {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(topBar = {
                CustomTopAppBar(
                    title = Screen.Login.title,
                    showAction = true,
                    actionIcon = Icons.Default.Info,
                    onClickAction = {
                        showAboutScreen = true
                    },
                    canNavigateBack = false,
                    canLogout = false,
                    scrollBehavior = scrollBehavior
                )
            }) {
                LoginScreenContent(modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                    viewModel = mainViewModel,
                    firestore = viewModel,
                    toggleScreen = gotoRegistrationScreen,
                    onLogin = { phone, pin ->
                    //                        onSubmit(phone, pin)
                    })

            }

        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    toggleScreen: ()->Unit,
    firestore: FirebaseFirestoreDbViewModel,
    onLogin: (contact: String, pin: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var contactNo by remember {
        mutableStateOf(viewModel.contact.value)
    }

    var pinNo by remember {
        mutableStateOf("")
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

    val keyboardController = LocalSoftwareKeyboardController.current

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
                input = contactNo,
                onInputChange = {
                    if (it.length < 11) {
                        contactNo = it
                    } else if (it.length == 11) {
                        contactNo = it
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    } else {
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }

                },
                placeholder = { Text(text = "Enter your phone number") },
                label = { Text(text = "Contact Number") },
                prefixIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone, contentDescription = "contact number"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                )
            ) {
                focusManager.moveFocus(FocusDirection.Next)
            }

            CustomOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                input = pinNo,
                onInputChange = {
                    if (it.length < 4) {
                        pinNo = it
                    } else if (it.length == 4) {
                        pinNo = it
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    } else {
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }

                },
                placeholder = { Text(text = "Enter your pin number") },
                label = { Text(text = "Pin Number") },
                prefixIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock, contentDescription = "pin number"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
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

                    }
                    else if (pinNo.isBlank() || pinNo.isEmpty()) {
                        errMsg = "PLease enter your pin number"
                        successMsg = null
                        showToast = true

                    } else {
                        onLogin(contactNo, pinNo)
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
                    Screen.Login.title.toString(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

            }

            Box(contentAlignment = Alignment.TopCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = toggleScreen) {
                        Text(text = buildAnnotatedString {
                            append("Don't Have an Account! ")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF3F51B5), fontSize = 16.sp
                                )
                            ) {
                                append("Create here...")
                            }
                        }, color = Color.Black, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    CustomToast(
                        showToast = showToast, errorMessage = errMsg, successMessage = successMsg
                    ) {
                        showToast = false
                    }
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(gotoRegistrationScreen = {},
        onSubmit = { c, p ->

    })
}