package com.example.smartstudyplanner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object AlarmHelper {

    fun scheduleStudyReminder(
        context: Context,
        subject: String,
        duration: Int,
        reason: String,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val totalMinutes =
            hour * 60 + minute

        val normalizedHour =
            totalMinutes / 60

        val normalizedMinute =
            totalMinutes % 60

        val intent =
            Intent(
                context,
                StudyReminderReceiver::class.java
            ).apply {
                putExtra(
                    "subject",
                    subject
                )

                putExtra(
                    "duration",
                    duration
                )

                putExtra(
                    "reason",
                    reason
                )
            }

        val requestCode =
            buildRequestCode(
                subject = subject,
                dayOfWeek = dayOfWeek,
                totalMinutes = totalMinutes
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar =
            Calendar.getInstance().apply {

                set(
                    Calendar.DAY_OF_WEEK,
                    dayOfWeek
                )

                set(
                    Calendar.HOUR_OF_DAY,
                    normalizedHour
                )

                set(
                    Calendar.MINUTE,
                    normalizedMinute
                )

                set(
                    Calendar.SECOND,
                    0
                )

                set(
                    Calendar.MILLISECOND,
                    0
                )

                if (
                    timeInMillis <=
                    System.currentTimeMillis()
                ) {
                    add(
                        Calendar.WEEK_OF_YEAR,
                        1
                    )
                }
            }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelStudyReminder(
        context: Context,
        subject: String,
        dayOfWeek: Int,
        hour: Int,
        minute: Int = 0
    ) {
        val totalMinutes =
            hour * 60 + minute

        val intent =
            Intent(
                context,
                StudyReminderReceiver::class.java
            )

        val requestCode =
            buildRequestCode(
                subject = subject,
                dayOfWeek = dayOfWeek,
                totalMinutes = totalMinutes
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            pendingIntent
        )

        pendingIntent.cancel()
    }

    private fun buildRequestCode(
        subject: String,
        dayOfWeek: Int,
        totalMinutes: Int
    ): Int {
        return subject.hashCode() * 31 +
                dayOfWeek * 100000 +
                totalMinutes
    }
}