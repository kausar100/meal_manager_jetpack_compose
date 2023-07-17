package com.kausar.messmanagementapp.presentation.auth_screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomOutlinedTextField
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.components.WelcomeText
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(
    phoneNumber: String, viewModel: AuthViewModel = hiltViewModel(), onSubmit: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showProgress by rememberSaveable {
        mutableStateOf(false)
    }

    var progressMsg by rememberSaveable {
        mutableStateOf("")
    }

    var otpEnter by rememberSaveable {
        mutableStateOf(false)
    }

    var resendOtp by rememberSaveable {
        mutableStateOf(false)
    }


    var otp by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(topBar = {
        CustomTopAppBar(
            title = Screen.PinVerify.title, canNavigateBack = false, scrollBehavior = scrollBehavior
        ) {}
    }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center
        ) {
            LaunchedEffect(key1 = true) {
                viewModel.createUserWithPhoneNumber(
                    phone = phoneNumber,
                    activity = context as Activity
                )
                    .collectLatest {
                        when (it) {
                            is ResultState.Loading -> {
                                progressMsg = "Sending otp..."
                                showProgress = true
                            }

                            is ResultState.Failure -> {
                                showProgress = false
                                otpEnter = true
                                it.message.message?.let { it1 -> context.showToast(it1) }
                            }

                            is ResultState.Success -> {
                                showProgress = false
                                otpEnter = true
                                context.showToast(it.data)
                            }
                        }
                    }
            }
            if (otpEnter) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WelcomeText()
                    Spacer(modifier = Modifier.fillMaxHeight(.1f))
                    Text(
                        text = "Enter otp sent to $phoneNumber",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    VerifyPinContent(otp = otp, onCodeEnter = { otp ->
                        scope.launch(Dispatchers.Main) {
                            viewModel.signInWithCredential(
                                otp
                            ).collectLatest {
                                when (it) {
                                    is ResultState.Loading -> {
                                        progressMsg = "Verifying otp..."
                                        showProgress = true
                                    }

                                    is ResultState.Failure -> {
                                        showProgress = false
                                        context.showToast("Please enter correct otp!")
                                        otpEnter = true
                                    }

                                    is ResultState.Success -> {
                                        showProgress = false
                                        context.showToast(it.data)
                                        onSubmit()
                                    }
                                }
                            }
                        }
                    }, onResendOtp = {
                        resendOtp = true
                    }, onChange = {
                        otp = it
                    })
                }
            }
            if (showProgress) {
                CustomProgressBar(msg = progressMsg)
            }

        }

    }

}

@Composable
fun VerifyPinContent(
    onCodeEnter: (String) -> Unit,
    otp: String = "",
    onResendOtp: () -> Unit,
    onChange: (String) -> Unit
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
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Didn't get any otp!",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Black,
            )
            TextButton(onClick = onResendOtp) {
                Text(
                    text = "Resend otp",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color(0xFF3F51B5),
                )
            }

        }
    }


}