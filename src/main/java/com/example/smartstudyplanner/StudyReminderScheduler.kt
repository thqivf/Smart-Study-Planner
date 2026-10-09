package com.example.smartstudyplanner

import android.content.Context
import java.util.Calendar

object StudyReminderScheduler {

    fun scheduleGeneratedStudyPlan(
        context: Context,
        studyPlans: List<StudyPlanItem>
    ) {

        studyPlans.forEach { studyPlan ->

            val dayOfWeek =
                getCalendarDayOfWeek(
                    studyPlan.day
                )

            if (
                dayOfWeek == -1
            ) {
                return@forEach
            }

            AlarmHelper.scheduleStudyReminder(
                context = context,
                subject = studyPlan.subject,
                duration = studyPlan.duration,
                reason = studyPlan.reason,
                dayOfWeek = dayOfWeek,
                hour = studyPlan.startHour,
                minute = studyPlan.startMinute
            )
        }
    }

    fun cancelGeneratedStudyPlan(
        context: Context,
        studyPlans: List<StudyPlanItem>
    ) {

        studyPlans.forEach { studyPlan ->

            val dayOfWeek =
                getCalendarDayOfWeek(
                    studyPlan.day
                )

            if (
                dayOfWeek == -1
            ) {
                return@forEach
            }

            AlarmHelper.cancelStudyReminder(
                context = context,
                subject = studyPlan.subject,
                dayOfWeek = dayOfWeek,
                hour = studyPlan.startHour,
                minute = studyPlan.startMinute
            )
        }
    }

    private fun getCalendarDayOfWeek(
        day: String
    ): Int {

        return when (
            day.trim().lowercase()
        ) {

            "monday" ->
                Calendar.MONDAY

            "tuesday" ->
                Calendar.TUESDAY

            "wednesday" ->
                Calendar.WEDNESDAY

            "thursday" ->
                Calendar.THURSDAY

            "friday" ->
                Calendar.FRIDAY

            "saturday" ->
                Calendar.SATURDAY

            "sunday" ->
                Calendar.SUNDAY

            else ->
                -1
        }
    }
}