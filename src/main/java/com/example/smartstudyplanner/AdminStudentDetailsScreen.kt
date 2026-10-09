package com.example.smartstudyplanner

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

data class AdminSubjectRecord(
    val id: String = "",
    val name: String = "",
    val mark: Double = 0.0
)

data class AdminTimetableRecord(
    val id: String = "",
    val day: String = "",
    val subject: String = "",
    val time: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudentDetailsScreen(
    studentId: String,
    onBackClick: () -> Unit
) {

    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    var studentName by remember {
        mutableStateOf("Student")
    }

    var studentEmail by remember {
        mutableStateOf("")
    }

    val subjects =
        remember {
            mutableStateListOf<AdminSubjectRecord>()
        }

    val timetable =
        remember {
            mutableStateListOf<AdminTimetableRecord>()
        }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun loadStudentDetails() {

        isLoading = true
        errorMessage = ""

        firestore
            .collection("users")
            .document(studentId)
            .get()
            .addOnSuccessListener { document ->

                studentName =
                    document.getString("name")
                        ?: "Student"

                studentEmail =
                    document.getString("email")
                        ?: ""

                firestore
                    .collection("users")
                    .document(studentId)
                    .collection("subjects")
                    .get()
                    .addOnSuccessListener { subjectResult ->

                        subjects.clear()

                        subjectResult.documents.forEach { subjectDocument ->

                            subjects.add(
                                AdminSubjectRecord(
                                    id =
                                        subjectDocument.id,
                                    name =
                                        subjectDocument
                                            .getString(
                                                "name"
                                            )
                                            ?: "Unnamed Subject",
                                    mark =
                                        subjectDocument
                                            .getDouble(
                                                "mark"
                                            )
                                            ?: 0.0
                                )
                            )
                        }

                        subjects.sortBy {
                            it.name.lowercase()
                        }

                        firestore
                            .collection("users")
                            .document(studentId)
                            .collection("timetable")
                            .get()
                            .addOnSuccessListener { timetableResult ->

                                timetable.clear()

                                timetableResult.documents.forEach { timetableDocument ->

                                    timetable.add(
                                        AdminTimetableRecord(
                                            id =
                                                timetableDocument.id,
                                            day =
                                                timetableDocument
                                                    .getString(
                                                        "day"
                                                    )
                                                    ?: "",
                                            subject =
                                                timetableDocument
                                                    .getString(
                                                        "subject"
                                                    )
                                                    ?: "",
                                            time =
                                                timetableDocument
                                                    .getString(
                                                        "time"
                                                    )
                                                    ?: ""
                                        )
                                    )
                                }

                                timetable.sortBy {
                                    dayOrder(
                                        it.day
                                    )
                                }

                                isLoading = false
                            }
                            .addOnFailureListener { exception ->

                                isLoading = false

                                errorMessage =
                                    exception.message
                                        ?: "Unable to load timetable information."
                            }
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: "Unable to load subject information."
                    }
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: "Unable to load student information."
            }
    }

    LaunchedEffect(studentId) {
        loadStudentDetails()
    }

    val averageMark =
        if (subjects.isNotEmpty()) {
            subjects
                .map {
                    it.mark
                }
                .average()
        } else {
            0.0
        }

    val weakestSubject =
        subjects.minByOrNull {
            it.mark
        }

    val strongestSubject =
        subjects.maxByOrNull {
            it.mark
        }

    val performance =
        when {

            subjects.isEmpty() ->
                "No academic data"

            averageMark < 40 ->
                "Needs attention"

            averageMark < 60 ->
                "Needs improvement"

            averageMark < 80 ->
                "Good progress"

            else ->
                "Strong performance"
        }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            "Student Details",
                        fontWeight =
                            FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            onBackClick()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        if (isLoading) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        ),
                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(
                        20.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        16.dp
                    )
            ) {

                if (
                    errorMessage.isNotBlank()
                ) {

                    item {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                )
                        ) {

                            Text(
                                text =
                                    errorMessage,
                                modifier =
                                    Modifier.padding(
                                        16.dp
                                    ),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer
                            )
                        }
                    }
                }

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                20.dp
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
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        20.dp
                                    )
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Card(
                                    shape =
                                        RoundedCornerShape(
                                            16.dp
                                        ),
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                MaterialTheme
                                                    .colorScheme
                                                    .surface
                                        )
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Person,
                                        contentDescription =
                                            null,
                                        modifier =
                                            Modifier
                                                .size(
                                                    58.dp
                                                )
                                                .padding(
                                                    14.dp
                                                ),
                                        tint =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            14.dp
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
                                            studentName,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .headlineSmall,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    if (
                                        studentEmail.isNotBlank()
                                    ) {

                                        Spacer(
                                            modifier =
                                                Modifier.height(
                                                    4.dp
                                                )
                                        )

                                        Row(
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {

                                            Icon(
                                                imageVector =
                                                    Icons.Default.Email,
                                                contentDescription =
                                                    null,
                                                modifier =
                                                    Modifier.size(
                                                        16.dp
                                                    )
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.width(
                                                        5.dp
                                                    )
                                            )

                                            Text(
                                                text =
                                                    studentEmail,
                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        StudentStatCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.AutoMirrored.Filled.Subject,
                            title =
                                "Subjects",
                            value =
                                subjects.size.toString()
                        )

                        StudentStatCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.Default.CalendarMonth,
                            title =
                                "Classes",
                            value =
                                timetable.size.toString()
                        )
                    }
                }

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                18.dp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        20.dp
                                    )
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.School,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(
                                            28.dp
                                        ),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            10.dp
                                        )
                                )

                                Text(
                                    text =
                                        "Academic Performance",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        18.dp
                                    )
                            )

                            Text(
                                text =
                                    String.format(
                                        Locale.getDefault(),
                                        "%.1f%%",
                                        averageMark
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .displaySmall,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )

                            Text(
                                text =
                                    performance,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        14.dp
                                    )
                            )

                            LinearProgressIndicator(
                                progress = {
                                    (
                                            averageMark / 100.0
                                            ).toFloat()
                                        .coerceIn(
                                            0f,
                                            1f
                                        )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                if (
                    weakestSubject != null ||
                    strongestSubject != null
                ) {

                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
                                )
                        ) {

                            if (
                                weakestSubject != null
                            ) {

                                PerformanceCard(
                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),
                                    icon =
                                        Icons.Default.TrendingDown,
                                    title =
                                        "Weakest",
                                    subject =
                                        weakestSubject.name,
                                    mark =
                                        weakestSubject.mark
                                )
                            }

                            if (
                                strongestSubject != null
                            ) {

                                PerformanceCard(
                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),
                                    icon =
                                        Icons.Default.TrendingUp,
                                    title =
                                        "Strongest",
                                    subject =
                                        strongestSubject.name,
                                    mark =
                                        strongestSubject.mark
                                )
                            }
                        }
                    }
                }

                item {

                    Text(
                        text =
                            "Subject Performance",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                if (
                    subjects.isEmpty()
                ) {

                    item {

                        EmptyCard(
                            text =
                                "No subject information available."
                        )
                    }

                } else {

                    items(
                        items = subjects,
                        key = {
                            it.id
                        }
                    ) { subject ->

                        SubjectPerformanceCard(
                            subject =
                                subject
                        )
                    }
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )

                    Text(
                        text =
                            "Timetable",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                if (
                    timetable.isEmpty()
                ) {

                    item {

                        EmptyCard(
                            text =
                                "No timetable information available."
                        )
                    }

                } else {

                    items(
                        items = timetable,
                        key = {
                            it.id
                        }
                    ) { classItem ->

                        TimetableCard(
                            classItem =
                                classItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentStatCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String
) {

    Card(
        modifier =
            modifier,
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
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
        ) {

            Icon(
                imageVector =
                    icon,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        28.dp
                    ),
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )

            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    title,
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
    }
}

@Composable
private fun PerformanceCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subject: String,
    mark: Double
) {

    Card(
        modifier =
            modifier,
        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
        ) {

            Icon(
                imageVector =
                    icon,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        26.dp
                    ),
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )

            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            Text(
                text =
                    subject,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                maxLines =
                    2
            )

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            Text(
                text =
                    String.format(
                        Locale.getDefault(),
                        "%.1f%%",
                        mark
                    ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
            )
        }
    }
}

@Composable
private fun SubjectPerformanceCard(
    subject: AdminSubjectRecord
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.Subject,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            28.dp
                        ),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary
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
                            subject.name,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            performanceLabel(
                                subject.mark
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
                        String.format(
                            Locale.getDefault(),
                            "%.1f%%",
                            subject.mark
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
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
                    (
                            subject.mark / 100.0
                            ).toFloat()
                        .coerceIn(
                            0f,
                            1f
                        )
                },
                modifier =
                    Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TimetableCard(
    classItem: AdminTimetableRecord
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
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Card(
                shape =
                    RoundedCornerShape(
                        14.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Schedule,
                    contentDescription =
                        null,
                    modifier =
                        Modifier
                            .size(
                                50.dp
                            )
                            .padding(
                                12.dp
                            ),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimaryContainer
                )
            }

            Spacer(
                modifier =
                    Modifier.width(
                        14.dp
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
                        classItem.subject,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )

                Text(
                    text =
                        classItem.day,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                if (
                    classItem.time.isNotBlank()
                ) {

                    Text(
                        text =
                            classItem.time,
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
            }
        }
    }
}

@Composable
private fun EmptyCard(
    text: String
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

        Text(
            text =
                text,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        20.dp
                    ),
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}

private fun performanceLabel(
    mark: Double
): String {

    return when {

        mark < 40 ->
            "Needs attention"

        mark < 60 ->
            "Needs improvement"

        mark < 80 ->
            "Good progress"

        else ->
            "Strong performance"
    }
}

private fun dayOrder(
    day: String
): Int {

    return when (
        day.trim().lowercase()
    ) {

        "monday" ->
            1

        "tuesday" ->
            2

        "wednesday" ->
            3

        "thursday" ->
            4

        "friday" ->
            5

        "saturday" ->
            6

        "sunday" ->
            7

        else ->
            8
    }
}