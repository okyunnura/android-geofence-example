package com.example.okumura.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.NonNull
import android.util.Log
import org.jetbrains.annotations.NotNull

class Notification {

    companion object {
        const val SERVICE_CHANNEL: String = "service"
        const val NOTIFY_CHANNEL: String = "notify"

        fun createChannel(@NonNull context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                manager.createNotificationChannel(
                    NotificationChannel(
                        SERVICE_CHANNEL,
                        "Service Notifications",
                        NotificationManager.IMPORTANCE_LOW
                    )
                )

                manager.createNotificationChannel(
                    NotificationChannel(
                        NOTIFY_CHANNEL,
                        "Notify Notifications",
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }

        fun builder(@NonNull context: Context, channel: String, title: String, message: String): Notification.Builder {
            Log.d("okyunnura", message)
            val builder: Notification.Builder = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> Notification.Builder(context, channel)
                else -> Notification.Builder(context)
            }
            return builder
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
        }

        fun notify(@NotNull context: Context, message: String) {
            val builder = builder(context, NOTIFY_CHANNEL, "sample", message)

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(101, builder.build())
        }
    }
}