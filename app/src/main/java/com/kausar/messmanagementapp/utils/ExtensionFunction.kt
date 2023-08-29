package com.kausar.messmanagementapp.utils

import android.content.Context
import android.widget.Toast
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
fun Duration.format(): String {
    val seconds = abs(seconds)
    return String.format(
        "%02d:%02d",
        seconds % 3600 / 60,
        seconds % 60
    )
}

fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, message, duration).show()