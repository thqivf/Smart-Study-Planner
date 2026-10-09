package com.example.smartstudyplanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val subject =
            intent.getStringExtra(
                "subject"
            ) ?: "Study Session"

        val duration =
            intent.getIntExtra(
                "duration",
                30
            )

        val reason =
            intent.getStringExtra(
                "reason"
            ) ?: ""

        NotificationHelper.showStudyReminder(
            context = context,
            subject = subject,
            duration = duration,
            reason = reason
        )
    }
}