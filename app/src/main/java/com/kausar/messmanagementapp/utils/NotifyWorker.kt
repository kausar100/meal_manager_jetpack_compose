package com.kausar.messmanagementapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kausar.messmanagementapp.R

class NotifyWorker(private val context: Context, params: WorkerParameters) : Worker(context,params){
    private  val CHANNEL_ID = "meal_notification"
    override fun doWork(): Result {
        Log.d("meal","Notify working start...")
        createNotificationChannel(context, CHANNEL_ID)
        showSimpleNotification(
            context = context,
            channelId = CHANNEL_ID,
            notificationId = 0,
            textTitle = "Review your meal",
            textContent = "Please Checkout your meal information for tomorrow!"
        )

        return Result.success()
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

    private fun showSimpleNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        textTitle: String,
        textContent: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(priority)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }


}