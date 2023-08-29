package com.kausar.messmanagementapp.presentation.viewmodels

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kausar.messmanagementapp.data.model.Status
import com.kausar.messmanagementapp.data.model.TimerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject


@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class TimerViewModel @Inject constructor(): ViewModel() {

    private val _viewState = MutableLiveData<TimerModel>()
    val viewState: LiveData<TimerModel> = _viewState

    var countDown: CountDownTimer? = null

    init {
        _viewState.value = TimerModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startTimer(duration: Duration) {
        countDown = object : CountDownTimer(duration.toMillis(), 10) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTick(seconds: Long) {
                _viewState.value = TimerModel(
                    timeDuration = Duration.ofMillis(seconds),
                    status = Status.RUNNING,
                )
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                _viewState.value = _viewState.value!!.copy(
                    timeDuration = Duration.ZERO,
                    status = Status.FINISHED,
                )
            }

        }
        countDown?.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resetTimer() {
        countDown?.cancel()
        _viewState.value = _viewState.value!!.copy(
            status = Status.NONE,
            timeDuration = Duration.ofMillis(60000),
        )
    }
}