package com.kausar.messmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.ui.theme.MessManagementAppTheme
import com.kausar.messmanagementapp.utils.NotifyWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessManagementAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    mainViewModel = hiltViewModel()
                    if (mainViewModel.userName.value.isNotEmpty()) {
                        setupWorker()
                    }
                    MainScreen(mainViewModel)
                }
            }
        }
    }

    private fun setupWorker() {
        val workerRequest = PeriodicWorkRequestBuilder<NotifyWorker>(2, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workerRequest)
    }
}