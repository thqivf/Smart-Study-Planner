package com.example.smartstudyplanner

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimetableImageExporter {

    private const val IMAGE_WIDTH = 2200
    private const val IMAGE_HEIGHT = 2200
    private const val START_HOUR = 8
    private const val END_HOUR = 22
    private const val ROW_HEIGHT = 120
    private const val DAY_WIDTH = 285
    private const val TIME_WIDTH = 200

    private val days =
        listOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )

    fun exportTimetable(
        context: Context,
        classes: List<StudentClass>,
        studyPlans: List<StudyPlanItem>
    ): Boolean {

        return try {

            val bitmap =
                Bitmap.createBitmap(
                    IMAGE_WIDTH,
                    IMAGE_HEIGHT,
                    Bitmap.Config.ARGB_8888
                )

            val canvas =
                Canvas(bitmap)

            val backgroundPaint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color =
                        android.graphics.Color.WHITE
                    style =
                        Paint.Style.FILL
                }

            canvas.drawRect(
                0f,
                0f,
                IMAGE_WIDTH.toFloat(),
                IMAGE_HEIGHT.toFloat(),
                backgroundPaint
            )

            drawTitle(canvas)

            val tableTop =
                220

            drawTableHeader(
                canvas,
                tableTop
            )

            drawTimeRows(
                canvas,
                tableTop
            )

            drawClasses(
                canvas,
                classes,
                tableTop
            )

            drawStudyPlans(
                canvas,
                studyPlans,
                tableTop
            )

            saveBitmap(
                context,
                bitmap
            )

            bitmap.recycle()

            true

        } catch (
            exception: Exception
        ) {

            false
        }
    }

    private fun drawTitle(
        canvas: Canvas
    ) {

        val titlePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.BLACK
                textSize =
                    64f
                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )
                textAlign =
                    Paint.Align.CENTER
            }

        canvas.drawText(
            "Smart Study Planner",
            IMAGE_WIDTH / 2f,
            80f,
            titlePaint
        )

        val subtitlePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.DKGRAY
                textSize =
                    34f
                textAlign =
                    Paint.Align.CENTER
            }

        canvas.drawText(
            "Weekly Study Timetable",
            IMAGE_WIDTH / 2f,
            140f,
            subtitlePaint
        )

        val datePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.GRAY
                textSize =
                    26f
                textAlign =
                    Paint.Align.CENTER
            }

        val date =
            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
            ).format(Date())

        canvas.drawText(
            date,
            IMAGE_WIDTH / 2f,
            185f,
            datePaint
        )
    }

    private fun drawTableHeader(
        canvas: Canvas,
        tableTop: Int
    ) {

        val headerPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.DKGRAY
                style =
                    Paint.Style.FILL
            }

        val textPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.WHITE
                textSize =
                    28f
                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )
                textAlign =
                    Paint.Align.CENTER
            }

        canvas.drawRect(
            0f,
            tableTop.toFloat(),
            TIME_WIDTH.toFloat(),
            (tableTop + ROW_HEIGHT).toFloat(),
            headerPaint
        )

        canvas.drawText(
            "Time",
            TIME_WIDTH / 2f,
            tableTop + 75f,
            textPaint
        )

        days.forEachIndexed { index, day ->

            val left =
                TIME_WIDTH +
                        index * DAY_WIDTH

            canvas.drawRect(
                left.toFloat(),
                tableTop.toFloat(),
                (left + DAY_WIDTH).toFloat(),
                (tableTop + ROW_HEIGHT).toFloat(),
                headerPaint
            )

            canvas.drawText(
                day,
                left + DAY_WIDTH / 2f,
                tableTop + 75f,
                textPaint
            )
        }
    }

    private fun drawTimeRows(
        canvas: Canvas,
        tableTop: Int
    ) {

        val gridPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.LTGRAY
                style =
                    Paint.Style.STROKE
                strokeWidth =
                    2f
            }

        val timePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.BLACK
                textSize =
                    24f
                textAlign =
                    Paint.Align.CENTER
            }

        for (
        hour in START_HOUR..END_HOUR
        ) {

            val rowIndex =
                hour - START_HOUR

            val top =
                tableTop +
                        ROW_HEIGHT +
                        rowIndex * ROW_HEIGHT

            canvas.drawRect(
                0f,
                top.toFloat(),
                IMAGE_WIDTH.toFloat(),
                (top + ROW_HEIGHT).toFloat(),
                gridPaint
            )

            canvas.drawText(
                formatHour(hour),
                TIME_WIDTH / 2f,
                top + 70f,
                timePaint
            )
        }

        for (
        index in 0..days.size
        ) {

            val x =
                TIME_WIDTH +
                        index * DAY_WIDTH

            canvas.drawLine(
                x.toFloat(),
                tableTop.toFloat(),
                x.toFloat(),
                (
                        tableTop +
                                ROW_HEIGHT +
                                (END_HOUR - START_HOUR) *
                                ROW_HEIGHT
                        ).toFloat(),
                gridPaint
            )
        }
    }

    private fun drawClasses(
        canvas: Canvas,
        classes: List<StudentClass>,
        tableTop: Int
    ) {

        classes.forEach { item ->

            val dayIndex =
                days.indexOfFirst {
                    it.equals(
                        item.day,
                        ignoreCase = true
                    )
                }

            if (
                dayIndex == -1
            ) {
                return@forEach
            }

            if (
                item.endMinute <=
                item.startMinute
            ) {
                return@forEach
            }

            drawTimeBlock(
                canvas = canvas,
                dayIndex = dayIndex,
                startMinute = item.startMinute,
                endMinute = item.endMinute,
                tableTop = tableTop,
                title = item.subject,
                subtitle = item.time,
                isStudyPlan = false
            )
        }
    }

    private fun drawStudyPlans(
        canvas: Canvas,
        studyPlans: List<StudyPlanItem>,
        tableTop: Int
    ) {

        studyPlans.forEach { item ->

            val dayIndex =
                days.indexOfFirst {
                    it.equals(
                        item.day,
                        ignoreCase = true
                    )
                }

            if (
                dayIndex == -1
            ) {
                return@forEach
            }

            val startMinute =
                item.startHour * 60 +
                        item.startMinute

            val endMinute =
                startMinute +
                        item.duration

            drawTimeBlock(
                canvas = canvas,
                dayIndex = dayIndex,
                startMinute = startMinute,
                endMinute = endMinute,
                tableTop = tableTop,
                title =
                    "Study: ${item.subject}",
                subtitle =
                    item.reason,
                isStudyPlan = true
            )
        }
    }

    private fun drawTimeBlock(
        canvas: Canvas,
        dayIndex: Int,
        startMinute: Int,
        endMinute: Int,
        tableTop: Int,
        title: String,
        subtitle: String,
        isStudyPlan: Boolean
    ) {

        val minimumMinute =
            START_HOUR * 60

        val maximumMinute =
            END_HOUR * 60

        val safeStart =
            startMinute.coerceIn(
                minimumMinute,
                maximumMinute
            )

        val safeEnd =
            endMinute.coerceIn(
                minimumMinute,
                maximumMinute
            )

        if (
            safeEnd <= safeStart
        ) {
            return
        }

        val minutesFromStart =
            safeStart -
                    minimumMinute

        val duration =
            safeEnd -
                    safeStart

        val top =
            tableTop +
                    ROW_HEIGHT +
                    (
                            minutesFromStart *
                                    ROW_HEIGHT /
                                    60
                            )

        val height =
            (
                    duration *
                            ROW_HEIGHT /
                            60
                    ).coerceAtLeast(45)

        val left =
            TIME_WIDTH +
                    dayIndex * DAY_WIDTH +
                    8

        val right =
            left +
                    DAY_WIDTH -
                    16

        val blockPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (isStudyPlan) {
                        android.graphics.Color.rgb(
                            225,
                            235,
                            250
                        )
                    } else {
                        android.graphics.Color.rgb(
                            240,
                            240,
                            240
                        )
                    }

                style =
                    Paint.Style.FILL
            }

        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            (top + height).toFloat(),
            blockPaint
        )

        val borderPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.DKGRAY
                style =
                    Paint.Style.STROKE
                strokeWidth =
                    3f
            }

        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            (top + height).toFloat(),
            borderPaint
        )

        val titlePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.BLACK
                textSize =
                    23f
                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )
            }

        val subtitlePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    android.graphics.Color.DKGRAY
                textSize =
                    18f
            }

        canvas.drawText(
            shortenText(
                title,
                20
            ),
            left + 12f,
            top + 32f,
            titlePaint
        )

        if (
            height >= 70
        ) {

            canvas.drawText(
                shortenText(
                    subtitle,
                    25
                ),
                left + 12f,
                top + 57f,
                subtitlePaint
            )
        }
    }

    private fun saveBitmap(
        context: Context,
        bitmap: Bitmap
    ) {

        val fileName =
            "SmartStudyPlanner_${
                SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(Date())
            }.png"

        val values =
            ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    fileName
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/png"
                )

                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q
                ) {

                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "Pictures/Smart Study Planner"
                    )

                    put(
                        MediaStore.Images.Media.IS_PENDING,
                        1
                    )
                }
            }

        val resolver =
            context.contentResolver

        val uri =
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
                ?: throw IllegalStateException(
                    "Unable to create image file"
                )

        try {

            resolver.openOutputStream(
                uri
            ).use { outputStream ->

                if (
                    outputStream == null
                ) {

                    throw IllegalStateException(
                        "Unable to open image output stream"
                    )
                }

                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
                )
            }

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {

                val completedValues =
                    ContentValues().apply {

                        put(
                            MediaStore.Images.Media.IS_PENDING,
                            0
                        )
                    }

                resolver.update(
                    uri,
                    completedValues,
                    null,
                    null
                )
            }

        } catch (
            exception: Exception
        ) {

            resolver.delete(
                uri,
                null,
                null
            )

            throw exception
        }
    }

    private fun formatHour(
        hour: Int
    ): String {

        val suffix =
            if (hour >= 12) {
                "PM"
            } else {
                "AM"
            }

        val displayHour =
            when {

                hour == 0 ->
                    12

                hour > 12 ->
                    hour - 12

                else ->
                    hour
            }

        return "$displayHour:00 $suffix"
    }

    private fun shortenText(
        text: String,
        maxLength: Int
    ): String {

        if (
            text.length <= maxLength
        ) {
            return text
        }

        return text
            .take(
                maxLength - 3
            )
            .trimEnd() +
                "..."
    }
}