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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HelpSection(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit
) {

    val sections =
        listOf(

            HelpSection(
                title =
                    "Getting Started",
                description =
                    "After registering or logging in, the Dashboard provides access to all major Smart Study Planner features. Start by adding your subjects and marks, then add your class timetable before generating your Smart Timetable.",
                icon =
                    Icons.Default.School
            ),

            HelpSection(
                title =
                    "Subjects and Marks",
                description =
                    "Open Subjects from the Dashboard to add your subjects and academic marks. You can add, edit, or delete subjects. The application calculates your average mark and identifies your weakest and strongest subjects based on the marks entered.",
                icon =
                    Icons.Default.MenuBook
            ),

            HelpSection(
                title =
                    "Academic Weakness Detection",
                description =
                    "The application analyses the marks entered for each subject. Subjects with lower marks receive greater attention in the Smart Timetable. Performance is classified as Needs Attention, Needs Improvement, Good Progress, or Strong Performance.",
                icon =
                    Icons.Default.TrendingUp
            ),

            HelpSection(
                title =
                    "Smart Timetable",
                description =
                    "Open Schedule to add your actual classes. After entering your timetable, select Generate Smart Timetable. The system identifies available study periods and creates study sessions while considering your existing classes and subject performance.",
                icon =
                    Icons.Default.CalendarMonth
            ),

            HelpSection(
                title =
                    "Export Timetable",
                description =
                    "After generating your Smart Timetable, use Export Timetable as Image to save the timetable as an image. The exported timetable contains the weekly schedule and generated study sessions.",
                icon =
                    Icons.Default.CalendarMonth
            ),

            HelpSection(
                title =
                    "Focus Mode",
                description =
                    "Open Focus Mode and select a subject that you have already added. Enter a study duration and start the session. You can pause, resume, stop, or reset the session. When the timer reaches zero, the completed session is automatically recorded.",
                icon =
                    Icons.Default.Timer
            ),

            HelpSection(
                title =
                    "Study Progress",
                description =
                    "Study Progress records completed Focus Mode sessions. It displays total study sessions, total study time, average session duration, subject study activity, and recent completed sessions.",
                icon =
                    Icons.Default.TrendingUp
            ),

            HelpSection(
                title =
                    "AI Assistant",
                description =
                    "The AI Assistant provides academic assistance using information from your subjects, marks, timetable, and completed study sessions. You can ask about your weakest subject, marks, study progress, timetable, and study priorities.",
                icon =
                    Icons.Default.AutoAwesome
            ),

            HelpSection(
                title =
                    "Study Reminders",
                description =
                    "The application can provide notifications for generated study sessions. Make sure notification permission is enabled on your device so that scheduled study reminders can be displayed.",
                icon =
                    Icons.Default.Notifications
            ),

            HelpSection(
                title =
                    "Settings",
                description =
                    "Settings allows you to change the application theme, view account information, access notification settings, and log out of the application.",
                icon =
                    Icons.Default.Settings
            ),

            HelpSection(
                title =
                    "Admin Portal",
                description =
                    "Administrators can access the Admin Login from the Welcome Screen. The Admin Dashboard provides an overview of registered students and allows administrators to view student academic information, subjects, marks, and timetable details.",
                icon =
                    Icons.Default.AdminPanelSettings
            ),

            HelpSection(
                title =
                    "Account",
                description =
                    "Your account information is stored using Firebase Authentication. Use your registered email and password to log in. Logging out returns you to the Welcome Screen.",
                icon =
                    Icons.Default.Person
            )
        )

    var expandedSection by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.HelpOutline,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    24.dp
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.size(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Help & User Manual",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick =
                            onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
                    .padding(
                        horizontal = 16.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            item {

                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )

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
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.HelpOutline,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    42.dp
                                ),
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )

                        Text(
                            text =
                                "Smart Study Planner User Manual",
                            fontSize =
                                21.sp,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Use this guide to learn how to use the main features of the Smart Study Planner application.",
                            fontSize =
                                14.sp,
                            textAlign =
                                TextAlign.Center,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )
                    }
                }
            }

            item {

                Text(
                    text =
                        "How to Use the Application",
                    fontSize =
                        18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        )
                )
            }

            items(
                sections.size
            ) { index ->

                val section =
                    sections[index]

                val isExpanded =
                    expandedSection ==
                            section.title

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
                                    .surface
                        )
                ) {

                    Column {

                        androidx.compose.material3
                            .TextButton(
                                onClick = {

                                    expandedSection =
                                        if (
                                            isExpanded
                                        ) {
                                            null
                                        } else {
                                            section.title
                                        }
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Row(

                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector =
                                            section.icon,
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
                                            Modifier.size(
                                                12.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            section.title,
                                        modifier =
                                            Modifier.weight(
                                                1f
                                            ),
                                        textAlign =
                                            TextAlign.Start,
                                        fontSize =
                                            15.sp,
                                        fontWeight =
                                            FontWeight.SemiBold
                                    )

                                    Icon(
                                        imageVector =
                                            if (
                                                isExpanded
                                            ) {
                                                Icons.Default.ExpandLess
                                            } else {
                                                Icons.Default.ExpandMore
                                            },
                                        contentDescription =
                                            if (
                                                isExpanded
                                            ) {
                                                "Collapse"
                                            } else {
                                                "Expand"
                                            }
                                    )
                                }
                            }

                        if (
                            isExpanded
                        ) {

                            Text(
                                text =
                                    section.description,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 20.dp,
                                            end = 20.dp,
                                            bottom = 18.dp
                                        ),
                                fontSize =
                                    14.sp,
                                lineHeight =
                                    21.sp,
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
                            12.dp
                        )
                )

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
                                    .surfaceVariant
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {

                        Text(
                            text =
                                "Quick Start",
                            fontSize =
                                17.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "1. Register or log in.\n\n2. Add your subjects and marks.\n\n3. Add your class timetable.\n\n4. Generate your Smart Timetable.\n\n5. Use Focus Mode to complete study sessions.\n\n6. Check Study Progress to monitor your study activity.\n\n7. Use the AI Assistant for personalised academic guidance.",
                            fontSize =
                                14.sp,
                            lineHeight =
                                22.sp
                        )
                    }
                }

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