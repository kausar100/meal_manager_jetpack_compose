package com.kausar.messmanagementapp.presentation.auth_screen

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.components.CustomTopAppBar
import com.kausar.messmanagementapp.components.OtpTextField
import com.kausar.messmanagementapp.data.model.Status
import com.kausar.messmanagementapp.data.model.TimerModel
import com.kausar.messmanagementapp.data.model.User
import com.kausar.messmanagementapp.navigation.Screen
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseFirestoreDbViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.TimerViewModel
import com.kausar.messmanagementapp.utils.ResultState
import com.kausar.messmanagementapp.utils.format
import com.kausar.messmanagementapp.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(
    info: String,
    mainViewModel: MainViewModel,
    viewModel: AuthViewModel = hiltViewModel(),
    firestoreViewModel: FirebaseFirestoreDbViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel(),
    onRegistrationComplete: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userInformation = Gson().fromJson(info, User::class.java)

    var showProgress by rememberSaveable {
        mutableStateOf(false)
    }

    var progressMsg by rememberSaveable {
        mutableStateOf("")
    }

    var resendOtp by rememberSaveable {
        mutableStateOf(false)
    }

    var otpSent by remember {
        mutableStateOf(false)
    }

    var otp by rememberSaveable {
        mutableStateOf("")
    }
    if (resendOtp) {
        LaunchedEffect(key1 = true) {
            viewModel.resendOtp(
                phone = userInformation.contactNo,
                activity = context as Activity
            ).collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        progressMsg = "Sending otp..."
                        showProgress = true
                    }

                    is ResultState.Failure -> {
                        resendOtp = false
                        showProgress = false
                        it.message.message?.let { msg -> context.showToast(msg) }
                    }

                    is ResultState.Success -> {
                        resendOtp = false
                        delay(1000)
                        showProgress = false
                        timerViewModel.resetTimer()
                        context.showToast(it.data)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.createUserWithPhoneNumber(
            phone = userInformation.contactNo,
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
                        it.message.message?.let { msg -> context.showToast(msg) }
                    }

                    is ResultState.Success -> {
                        delay(1000)
                        showProgress = false
                        otpSent = true
                        timerViewModel.startTimer(Duration.ofSeconds(60))
                        context.showToast(it.data)
                    }
                }
            }
    }

    val timer by timerViewModel.viewState.observeAsState()

    Scaffold(topBar = {
        CustomTopAppBar(
            title = Screen.PinVerify.title,
            canNavigateBack = false,
            canLogout = false,
            scrollBehavior = scrollBehavior
        )
    }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center
        ) {
            if (otpSent) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter otp sent to ${userInformation.contactNo}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    VerifyPinContent(
                        onCodeEnter = { otp ->
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
                                        }

                                        is ResultState.Success -> {
                                            timerViewModel.resetTimer()
                                            if (userInformation.userType.isEmpty()) {
                                                //login
                                                Log.d("OtpVerifyScreen: ", "need to just login")
                                                showProgress = false
                                                context.showToast(it.data)
                                                mainViewModel.saveContact(userInformation.contactNo)
                                                onRegistrationComplete()
                                            } else {
                                                //registration
                                                Log.d("OtpVerifyScreen: ", "need to register")
                                                firestoreViewModel.registerUser(userInformation)
                                                    .collectLatest { result ->
                                                        when (result) {
                                                            is ResultState.Success -> {
                                                                showProgress = false
                                                                context.showToast(result.data)
                                                                mainViewModel.saveContact(
                                                                    userInformation.contactNo
                                                                )
                                                                onRegistrationComplete()
                                                            }

                                                            is ResultState.Failure -> {
                                                                showProgress = false
                                                                context.showToast(
                                                                    result.message.localizedMessage
                                                                        ?: "Some error occurred"
                                                                )
                                                            }

                                                            is ResultState.Loading -> {}
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        otp = otp,
                        onResendOtp = {
                            timerViewModel.startTimer(Duration.ofSeconds(60))
                            resendOtp = true
                        },
                        timer = timer
                    ) {
                        otp = it
                    }
                }
            }
            if (showProgress) {
                CustomProgressBar(msg = progressMsg)
            }

        }

    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VerifyPinContent(
    onCodeEnter: (String) -> Unit,
    otp: String = "",
    onResendOtp: () -> Unit = {},
    timer: TimerModel?,
    onChange: (String) -> Unit
) {

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OtpTextField(otpText = otp, onOtpTextChange = onChange)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            timer?.let {
                if (timer.status == Status.FINISHED) {
                    Text(
                        text = "Didn't get any otp!",
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    )

                } else if (timer.status == Status.RUNNING) {
                    Text(
                        text = "Time Remaining: ${timer.timeDuration.format()}",
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    )
                }
                if (timer.status != Status.NONE) {
                    TextButton(onClick = onResendOtp, enabled = timer.status == Status.FINISHED) {
                        Text(
                            text = "Resend otp",
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ElevatedButton(
            onClick = {
                onCodeEnter(otp)
            }, shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(text = "Verify OTP", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }

    }
}