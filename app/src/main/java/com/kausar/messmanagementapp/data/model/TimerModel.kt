package com.kausar.messmanagementapp.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
data class TimerModel(
    val timeDuration: Duration = Duration.ofSeconds(60),
    val status: Status = Status.NONE,
)

enum class Status {
    NONE, RUNNING, FINISHED
}
