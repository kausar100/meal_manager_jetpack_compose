package com.kausar.messmanagementapp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kausar.messmanagementapp.MainActivity
import com.kausar.messmanagementapp.R
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.net.SocketException
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MealReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private val CHANNEL_ID = "meal_notification"

    companion object {
        private const val REMINDER_WORK_NAME = "meal_reminder"
//        private const val PARAM_NAME = "set_meal"

        @RequiresApi(Build.VERSION_CODES.O)
        fun runAt() {
            val workManager = WorkManager.getInstance()

            //trigger at 9.30 pm
            val alarmTime = LocalTime.of(21, 30)
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            println("now value $now")

            val nowTime = now.toLocalTime()
            println("nowtime $nowTime")

            // if same time, schedule for next day as well
            // if today's time had passed, schedule for next day
            if (nowTime == alarmTime || nowTime.isAfter(alarmTime)) {
                now = now.plusDays(1)
            }

            now = now.withHour(alarmTime.hour).withMinute(alarmTime.minute)

            val duration = Duration.between(LocalDateTime.now(), now)
            println("runAt = ${duration.seconds}s")

            // optional constraints
            /*
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            //optional data
            val data = workDataOf(PARAM_NAME to "Timer 01")
            */

            val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
                .setInitialDelay(duration.seconds, TimeUnit.SECONDS)
//                .setConstraints(constraints)
//                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork(
                REMINDER_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancel() {
            val workManager = WorkManager.getInstance()
            workManager.cancelUniqueWork(REMINDER_WORK_NAME)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {

        val context = applicationContext

//        val name = inputData.getString(PARAM_NAME)

        var isScheduleNext = true

        try {
            createNotificationChannel(context, CHANNEL_ID)
            showSimpleNotificationWithTapAction(
                context = context,
                channelId = CHANNEL_ID,
                notificationId = 0,
                textTitle = "Review your meal",
                textContent = "Please Checkout your meal information for tomorrow!"
            )
            Result.success()

        } catch (e: Exception) {
            if (runAttemptCount > 3) {
                return@coroutineScope Result.success()
            }
            when (e.cause) {
                is SocketException -> {
                    isScheduleNext = false
                    Result.retry()
                }

                else -> {
                    Result.failure()
                }
            }

            Result.failure()

        } finally {
            // only schedule next day if not retry, else it will overwrite the retry attempt
            // because we use uniqueName with ExistingWorkPolicy.REPLACE
            if (isScheduleNext) {
                // schedule for next day
                runAt()
            }
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Setup Meal"
            val descriptionText = "Setup your meal info"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSimpleNotificationWithTapAction(
        context: Context,
        channelId: String,
        notificationId: Int,
        textTitle: String,
        textContent: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }

}