package com.example.smartstudyplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

data class AdminStudent(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val subjectCount: Int = 0,
    val classCount: Int = 0,
    val averageMark: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onStudentClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {

    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    val students =
        remember {
            mutableStateListOf<AdminStudent>()
        }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun loadStudents() {

        isLoading = true
        errorMessage = ""

        firestore
            .collection("users")
            .orderBy(
                "name",
                Query.Direction.ASCENDING
            )
            .get()
            .addOnSuccessListener { result ->

                students.clear()

                val studentDocuments =
                    result.documents.filter { document ->

                        val role =
                            document.getString("role")
                                ?: "student"

                        role != "admin"
                    }

                if (studentDocuments.isEmpty()) {

                    isLoading = false

                    return@addOnSuccessListener
                }

                var completedRequests = 0

                studentDocuments.forEach { document ->

                    val studentId =
                        document.id

                    val studentName =
                        document.getString("name")
                            ?: "Unnamed Student"

                    val studentEmail =
                        document.getString("email")
                            ?: ""

                    firestore
                        .collection("users")
                        .document(studentId)
                        .collection("subjects")
                        .get()
                        .addOnSuccessListener { subjectResult ->

                            val marks =
                                subjectResult.documents.mapNotNull { subjectDocument ->

                                    subjectDocument.getDouble(
                                        "mark"
                                    )
                                }

                            val averageMark =
                                if (marks.isNotEmpty()) {
                                    marks.average()
                                } else {
                                    0.0
                                }

                            firestore
                                .collection("users")
                                .document(studentId)
                                .collection("timetable")
                                .get()
                                .addOnSuccessListener { timetableResult ->

                                    students.add(
                                        AdminStudent(
                                            id = studentId,
                                            name = studentName,
                                            email = studentEmail,
                                            subjectCount =
                                                subjectResult.size(),
                                            classCount =
                                                timetableResult.size(),
                                            averageMark =
                                                averageMark
                                        )
                                    )

                                    completedRequests++

                                    if (
                                        completedRequests ==
                                        studentDocuments.size
                                    ) {

                                        students.sortBy {
                                            it.name.lowercase()
                                        }

                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener {

                                    students.add(
                                        AdminStudent(
                                            id = studentId,
                                            name = studentName,
                                            email = studentEmail,
                                            subjectCount =
                                                subjectResult.size(),
                                            classCount = 0,
                                            averageMark =
                                                averageMark
                                        )
                                    )

                                    completedRequests++

                                    if (
                                        completedRequests ==
                                        studentDocuments.size
                                    ) {

                                        students.sortBy {
                                            it.name.lowercase()
                                        }

                                        isLoading = false
                                    }
                                }
                        }
                        .addOnFailureListener {

                            students.add(
                                AdminStudent(
                                    id = studentId,
                                    name = studentName,
                                    email = studentEmail
                                )
                            )

                            completedRequests++

                            if (
                                completedRequests ==
                                studentDocuments.size
                            ) {

                                students.sortBy {
                                    it.name.lowercase()
                                }

                                isLoading = false
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: "Unable to load student information."
            }
    }

    LaunchedEffect(Unit) {
        loadStudents()
    }

    val overallAverage =
        if (students.isNotEmpty()) {
            students
                .map {
                    it.averageMark
                }
                .average()
        } else {
            0.0
        }

    val totalSubjects =
        students.sumOf {
            it.subjectCount
        }

    val totalClasses =
        students.sumOf {
            it.classCount
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
                                Icons.Default.School,
                            contentDescription =
                                "Admin",
                            modifier =
                                Modifier.size(
                                    28.dp
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
                                "Admin Dashboard",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            loadStudents()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,
                            contentDescription =
                                "Refresh"
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
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    )
            )
        }

    ) { innerPadding ->

        if (isLoading) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        ),
                contentAlignment =
                    Alignment.Center
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
                            "Student Overview",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
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
                            "Monitor student academic information and study activity.",
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

                if (
                    errorMessage.isNotBlank()
                ) {

                    item {

                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                ),
                            shape =
                                RoundedCornerShape(
                                    16.dp
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

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        AdminSummaryCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.Default.Groups,
                            title =
                                "Students",
                            value =
                                students.size.toString()
                        )

                        AdminSummaryCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.Default.School,
                            title =
                                "Subjects",
                            value =
                                totalSubjects.toString()
                        )
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

                        AdminSummaryCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.Default.Person,
                            title =
                                "Classes",
                            value =
                                totalClasses.toString()
                        )

                        AdminSummaryCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            icon =
                                Icons.AutoMirrored.Filled.TrendingUp,
                            title =
                                "Average",
                            value =
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f%%",
                                    overallAverage
                                )
                        )
                    }
                }

                item {

                    Text(
                        text =
                            "Students",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                if (
                    students.isEmpty()
                ) {

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

                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            24.dp
                                        ),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Groups,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(
                                            48.dp
                                        ),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            12.dp
                                        )
                                )

                                Text(
                                    text =
                                        "No students found",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
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
                                        "Registered students will appear here.",
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
                        }
                    }

                } else {

                    items(
                        items = students,
                        key = {
                            it.id
                        }
                    ) { student ->

                        AdminStudentCard(
                            student =
                                student,
                            onClick = {
                                onStudentClick(
                                    student.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSummaryCard(
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
                    ),
            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp
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
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AdminStudentCard(
    student: AdminStudent,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
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
                        Icons.Default.Person,
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
                        student.name,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                if (
                    student.email.isNotBlank()
                ) {

                    Text(
                        text =
                            student.email,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        maxLines =
                            1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {

                    Text(
                        text =
                            "${student.subjectCount} subjects",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Text(
                        text =
                            "${student.classCount} classes",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Text(
                        text =
                            String.format(
                                Locale.getDefault(),
                                "%.1f%%",
                                student.averageMark
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription =
                    "View student",
                modifier =
                    Modifier.size(
                        18.dp
                    ),
                tint =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}