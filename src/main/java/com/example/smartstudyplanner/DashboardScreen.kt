package com.example.smartstudyplanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSubjectsClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onFocusClick: () -> Unit,
    onProgressClick: () -> Unit,
    onAIClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    val userId =
        auth.currentUser?.uid

    var studentName by remember {
        mutableStateOf("Student")
    }

    var subjectCount by remember {
        mutableStateOf(0)
    }

    var classCount by remember {
        mutableStateOf(0)
    }

    var averageMark by remember {
        mutableStateOf(0.0)
    }

    LaunchedEffect(userId) {

        if (
            userId == null
        ) {
            return@LaunchedEffect
        }

        val userRef =
            firestore
                .collection("users")
                .document(userId)

        userRef
            .get()
            .addOnSuccessListener { document ->

                studentName =
                    document.getString(
                        "name"
                    )
                        ?: "Student"
            }

        userRef
            .collection("subjects")
            .get()
            .addOnSuccessListener { documents ->

                subjectCount =
                    documents.size()

                val marks =
                    documents.mapNotNull { document ->

                        document.getDouble(
                            "mark"
                        )
                    }

                averageMark =
                    if (
                        marks.isNotEmpty()
                    ) {
                        marks.average()
                    } else {
                        0.0
                    }
            }

        userRef
            .collection("timetable")
            .get()
            .addOnSuccessListener { documents ->

                classCount =
                    documents.size()
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable.logo
                                ),
                            contentDescription =
                                "Smart Study Planner Logo",
                            modifier =
                                Modifier.size(
                                    42.dp
                                ),
                            contentScale =
                                ContentScale.Fit
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    10.dp
                                )
                        )

                        Column {

                            Text(
                                text =
                                    "Smart Study Planner",
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Student Dashboard",
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
                },

                actions = {

                    IconButton(
                        onClick = {
                            onSettingsClick()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Settings,
                            contentDescription =
                                "Settings"
                        )
                    }

                    IconButton(
                        onClick = {
                            onLogoutClick()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.Logout,
                            contentDescription =
                                "Logout"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    ),
            contentPadding =
                PaddingValues(
                    20.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {

            item {

                Text(
                    text =
                        "Welcome, $studentName",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
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
                        "Manage your subjects, schedule, focus sessions and academic progress.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
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

                    DashboardStatCard(
                        modifier =
                            Modifier.weight(
                                1f
                            ),
                        icon =
                            Icons.AutoMirrored.Filled.MenuBook,
                        title =
                            "Subjects",
                        value =
                            subjectCount.toString()
                    )

                    DashboardStatCard(
                        modifier =
                            Modifier.weight(
                                1f
                            ),
                        icon =
                            Icons.Default.CalendarMonth,
                        title =
                            "Classes",
                        value =
                            classCount.toString()
                    )
                }
            }

            item {

                DashboardStatCard(
                    modifier =
                        Modifier.fillMaxWidth(),
                    icon =
                        Icons.AutoMirrored.Filled.ShowChart,
                    title =
                        "Average Mark",
                    value =
                        String.format(
                            Locale.getDefault(),
                            "%.1f%%",
                            averageMark
                        )
                )
            }

            item {

                Text(
                    text =
                        "Study Tools",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            item {

                DashboardFeatureCard(
                    icon =
                        Icons.AutoMirrored.Filled.MenuBook,
                    title =
                        "Subjects",
                    description =
                        "Add, edit and manage your subjects and academic marks.",
                    onClick =
                        onSubjectsClick
                )
            }

            item {

                DashboardFeatureCard(
                    icon =
                        Icons.Default.CalendarMonth,
                    title =
                        "Schedule",
                    description =
                        "Manage your classes and generate a flexible study plan.",
                    onClick =
                        onScheduleClick
                )
            }

            item {

                DashboardFeatureCard(
                    icon =
                        Icons.Default.Timer,
                    title =
                        "Focus Mode",
                    description =
                        "Start focused study sessions and reduce interruptions.",
                    onClick =
                        onFocusClick
                )
            }

            item {

                DashboardFeatureCard(
                    icon =
                        Icons.AutoMirrored.Filled.ShowChart,
                    title =
                        "Study Progress",
                    description =
                        "Review your study sessions and overall study activity.",
                    onClick =
                        onProgressClick
                )
            }

            item {

                DashboardFeatureCard(
                    icon =
                        Icons.Default.AutoAwesome,
                    title =
                        "AI Assistant",
                    description =
                        "Get personalized academic guidance based on your subjects and marks.",
                    onClick =
                        onAIClick
                )
            }

            item {

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
                                    "Study Reminder",
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
                                        4.dp
                                    )
                            )

                            Text(
                                text =
                                    "Your generated study plan can send reminders when scheduled study sessions begin.",
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

            item {

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun DashboardStatCard(
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
private fun DashboardFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        onClick =
            onClick
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
                        icon,
                    contentDescription =
                        null,
                    modifier =
                        Modifier
                            .size(
                                52.dp
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
                        title,
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
                            4.dp
                        )
                )

                Text(
                    text =
                        description,
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