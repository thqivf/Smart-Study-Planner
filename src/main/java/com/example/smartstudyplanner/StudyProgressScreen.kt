package com.example.smartstudyplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FocusSessionRecord(
    val id: String = "",
    val subject: String = "",
    val duration: Int = 0,
    val completed: Boolean = false,
    val completedAt: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun StudyProgressScreen(
    onBackClick: () -> Unit
) {

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    var sessions by remember {
        mutableStateOf(
            emptyList<FocusSessionRecord>()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(
        currentUser?.uid
    ) {

        if (
            currentUser == null
        ) {
            isLoading = false
            return@LaunchedEffect
        }

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(
                currentUser.uid
            )
            .collection("focusSessions")
            .get()
            .addOnSuccessListener { result ->

                val loadedSessions =
                    result.documents.mapIndexedNotNull {
                            index,
                            document ->

                        val subject =
                            document.getString(
                                "subject"
                            ) ?: "Study Session"

                        val duration =
                            readDuration(
                                document.get(
                                    "duration"
                                )
                            )

                        val completed =
                            document.getBoolean(
                                "completed"
                            ) ?: false

                        val completedAt =
                            readCompletedAt(
                                document.get(
                                    "completedAt"
                                )
                            )

                        FocusSessionRecord(
                            id =
                                document.id.ifBlank {
                                    "session_${index}_${subject}_${completedAt}"
                                },
                            subject =
                                subject,
                            duration =
                                duration,
                            completed =
                                completed,
                            completedAt =
                                completedAt
                        )
                    }
                        .sortedByDescending {
                            it.completedAt
                        }

                sessions =
                    loadedSessions

                isLoading = false
            }
            .addOnFailureListener {

                sessions =
                    emptyList()

                isLoading = false
            }
    }

    val completedSessions =
        sessions.filter {
            it.completed
        }

    val totalSessions =
        completedSessions.size

    val totalMinutes =
        completedSessions.sumOf {
            it.duration
        }

    val averageDuration =
        if (
            totalSessions > 0
        ) {
            totalMinutes /
                    totalSessions
        } else {
            0
        }

    val subjectTotals =
        completedSessions
            .groupBy {
                it.subject
            }
            .mapValues { entry ->
                entry.value.sumOf {
                    it.duration
                }
            }
            .toList()
            .sortedByDescending {
                it.second
            }

    val topSubject =
        subjectTotals
            .firstOrNull()

    val progressTargetMinutes =
        600

    val progress =
        (
                totalMinutes.toFloat() /
                        progressTargetMinutes.toFloat()
                )
            .coerceIn(
                0f,
                1f
            )

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Study Progress",
                        fontWeight =
                            FontWeight.SemiBold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick =
                            onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults
                        .topAppBarColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surface
                        )
            )
        }

    ) { paddingValues ->

        if (
            isLoading
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                androidx.compose.material3.CircularProgressIndicator()
            }

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        )
                        .padding(
                            horizontal = 20.dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        14.dp
                    )
            ) {

                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )
                }

                item {

                    ProgressOverviewCard(
                        totalSessions =
                            totalSessions,
                        totalMinutes =
                            totalMinutes,
                        averageDuration =
                            averageDuration
                    )
                }

                item {

                    StudyGoalCard(
                        totalMinutes =
                            totalMinutes,
                        progress =
                            progress
                    )
                }

                item {

                    if (
                        topSubject != null
                    ) {

                        TopSubjectCard(
                            subject =
                                topSubject.first,
                            minutes =
                                topSubject.second
                        )
                    }
                }

                if (
                    subjectTotals.isNotEmpty()
                ) {

                    item {

                        SectionTitle(
                            text =
                                "Subject Activity"
                        )
                    }

                    itemsIndexed(
                        items =
                            subjectTotals,
                        key = { index, item ->

                            "subject_${index}_${item.first}_${item.second}"
                        }
                    ) { _, item ->

                        SubjectProgressCard(
                            subject =
                                item.first,
                            minutes =
                                item.second,
                            totalMinutes =
                                totalMinutes
                        )
                    }
                }

                item {

                    SectionTitle(
                        text =
                            "Recent Study Sessions"
                    )
                }

                if (
                    completedSessions.isEmpty()
                ) {

                    item {

                        EmptyProgressCard()
                    }

                } else {

                    itemsIndexed(
                        items =
                            completedSessions.take(
                                10
                            ),
                        key = { index, session ->

                            session.id.ifBlank {
                                "recent_${index}_${session.subject}_${session.completedAt}"
                            }
                        }
                    ) { _, session ->

                        SessionCard(
                            session =
                                session
                        )
                    }
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ProgressOverviewCard(
    totalSessions: Int,
    totalMinutes: Int,
    averageDuration: Int
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    20.dp
                )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Assessment,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            28.dp
                        ),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimaryContainer
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )

                Text(
                    text =
                        "Overall Study Activity",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onPrimaryContainer
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                ProgressStat(
                    icon =
                        Icons.Default.Timer,
                    value =
                        totalSessions.toString(),
                    label =
                        "Sessions"
                )

                ProgressStat(
                    icon =
                        Icons.Default.AccessTime,
                    value =
                        formatMinutes(
                            totalMinutes
                        ),
                    label =
                        "Study Time"
                )

                ProgressStat(
                    icon =
                        Icons.Default.TrendingUp,
                    value =
                        "${averageDuration}m",
                    label =
                        "Average"
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ProgressStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector =
                icon,
            contentDescription =
                null,
            modifier =
                Modifier.size(
                    22.dp
                ),
            tint =
                MaterialTheme
                    .colorScheme
                    .onPrimaryContainer
        )

        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )

        Text(
            text =
                value,
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme
                    .colorScheme
                    .onPrimaryContainer
        )

        Text(
            text =
                label,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onPrimaryContainer
        )
    }
}

@androidx.compose.runtime.Composable
private fun StudyGoalCard(
    totalMinutes: Int,
    progress: Float
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ShowChart,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            24.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            10.dp
                        )
                )

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            "Study Goal",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            "${formatMinutes(totalMinutes)} of 10 hours",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }

                Text(
                    text =
                        "${(progress * 100).toInt()}%",
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            8.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                8.dp
                            )
                        )
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun TopSubjectCard(
    subject: String,
    minutes: Int
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    18.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.MenuBook,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        26.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        "Most Studied Subject",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )

                Text(
                    text =
                        subject,
                    fontWeight =
                        FontWeight.SemiBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Text(
                text =
                    formatMinutes(
                        minutes
                    ),
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text =
            text,
        style =
            MaterialTheme
                .typography
                .titleMedium,
        fontWeight =
            FontWeight.SemiBold,
        modifier =
            Modifier.padding(
                top = 4.dp
            )
    )
}

@androidx.compose.runtime.Composable
private fun SubjectProgressCard(
    subject: String,
    minutes: Int,
    totalMinutes: Int
) {

    val progress =
        if (
            totalMinutes > 0
        ) {
            minutes.toFloat() /
                    totalMinutes.toFloat()
        } else {
            0f
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MenuBook,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            22.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            10.dp
                        )
                )

                Text(
                    text =
                        subject,
                    modifier =
                        Modifier.weight(
                            1f
                        ),
                    fontWeight =
                        FontWeight.Medium,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text =
                        formatMinutes(
                            minutes
                        ),
                    fontWeight =
                        FontWeight.SemiBold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )

            LinearProgressIndicator(
                progress = {
                    progress.coerceIn(
                        0f,
                        1f
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            7.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                7.dp
                            )
                        )
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun SessionCard(
    session: FocusSessionRecord
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.Timer,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        24.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        session.subject,
                    fontWeight =
                        FontWeight.SemiBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )

                Text(
                    text =
                        formatDate(
                            session.completedAt
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Text(
                text =
                    "${session.duration} min",
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun EmptyProgressCard() {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.CalendarToday,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        34.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )

            Text(
                text =
                    "No completed study sessions yet",
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            Text(
                text =
                    "Complete a Focus Mode session to see your progress here.",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )
        }
    }
}

private fun readDuration(
    value: Any?
): Int {

    return when (
        value
    ) {

        is Number ->
            value.toInt()

        is String ->
            value.toIntOrNull()
                ?: 0

        else ->
            0
    }
}

private fun readCompletedAt(
    value: Any?
): Long {

    return when (
        value
    ) {

        is Timestamp ->
            value.toDate().time

        is Date ->
            value.time

        is Number ->
            value.toLong()

        is String -> {

            value.toLongOrNull()
                ?: parseDateString(
                    value
                )
        }

        else ->
            0L
    }
}

private fun parseDateString(
    value: String
): Long {

    val formats =
        listOf(
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm"
        )

    formats.forEach { pattern ->

        try {

            val date =
                SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                ).parse(
                    value
                )

            if (
                date != null
            ) {
                return date.time
            }

        } catch (
            exception: Exception
        ) {
        }
    }

    return 0L
}

private fun formatMinutes(
    minutes: Int
): String {

    val hours =
        minutes / 60

    val remainingMinutes =
        minutes % 60

    return when {

        hours > 0 ->
            "${hours}h ${remainingMinutes}m"

        else ->
            "${remainingMinutes}m"
    }
}

private fun formatDate(
    timestamp: Long
): String {

    if (
        timestamp <= 0L
    ) {
        return "Study session completed"
    }

    return try {

        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(
            Date(
                timestamp
            )
        )

    } catch (
        exception: Exception
    ) {

        "Study session completed"
    }
}