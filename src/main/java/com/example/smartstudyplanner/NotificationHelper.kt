package com.example.smartstudyplanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID =
        "study_reminders"

    private const val CHANNEL_NAME =
        "Study Reminders"

    private const val CHANNEL_DESCRIPTION =
        "Notifications for scheduled study sessions"

    fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager
                        .IMPORTANCE_HIGH
                ).apply {

                    description =
                        CHANNEL_DESCRIPTION

                    enableVibration(true)
                }

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(
                channel
            )
        }
    }

    fun showStudyReminder(
        context: Context,
        subject: String,
        duration: Int,
        reason: String
    ) {

        createNotificationChannel(
            context
        )

        val title =
            "Study Session"

        val message =
            if (reason.isBlank()) {
                "$subject study session starts now. Duration: $duration minutes."
            } else {
                "$subject study session starts now. $reason"
            }

        val notification =
            NotificationCompat
                .Builder(
                    context,
                    CHANNEL_ID
                )
                .setSmallIcon(
                    android.R.drawable.ic_popup_reminder
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    message
                )
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(
                            message
                        )
                )
                .setPriority(
                    NotificationCompat
                        .PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .setCategory(
                    NotificationCompat
                        .CATEGORY_REMINDER
                )
                .build()

        try {

            NotificationManagerCompat
                .from(context)
                .notify(
                    subject.hashCode(),
                    notification
                )

        } catch (
            exception: SecurityException
        ) {
        }
    }
}