package com.example.smartstudyplanner

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

data class StudentClass(
    val id: String = "",
    val day: String = "",
    val subject: String = "",
    val time: String = "",
    val startMinute: Int = 0,
    val endMinute: Int = 0
)

data class StudyPlanItem(
    val id: String = "",
    val day: String = "",
    val subject: String = "",
    val startHour: Int = 0,
    val startMinute: Int = 0,
    val duration: Int = 30,
    val reason: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBackClick: () -> Unit
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    val classes = remember {
        mutableStateListOf<StudentClass>()
    }

    val studyPlans = remember {
        mutableStateListOf<StudyPlanItem>()
    }

    var showClassDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showGeneratedDialog by remember {
        mutableStateOf(false)
    }

    var selectedClass by remember {
        mutableStateOf<StudentClass?>(null)
    }

    var classToDelete by remember {
        mutableStateOf<StudentClass?>(null)
    }

    var selectedDay by remember {
        mutableStateOf("Monday")
    }

    var subjectInput by remember {
        mutableStateOf("")
    }

    var startTimeInput by remember {
        mutableStateOf("")
    }

    var endTimeInput by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isGenerating by remember {
        mutableStateOf(false)
    }

    var statusMessage by remember {
        mutableStateOf("")
    }

    var showDayMenu by remember {
        mutableStateOf(false)
    }

    val days = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    LaunchedEffect(currentUser?.uid) {

        val uid =
            currentUser?.uid
                ?: return@LaunchedEffect

        firestore
            .collection("users")
            .document(uid)
            .collection("timetable")
            .get()
            .addOnSuccessListener { result ->

                classes.clear()

                result.documents.forEach { document ->

                    classes.add(
                        StudentClass(
                            id = document.id,
                            day = document.getString("day") ?: "",
                            subject = document.getString("subject") ?: "",
                            time = document.getString("time") ?: "",
                            startMinute =
                                document.getLong("startMinute")
                                    ?.toInt()
                                    ?: 0,
                            endMinute =
                                document.getLong("endMinute")
                                    ?.toInt()
                                    ?: 0
                        )
                    )
                }
            }

        firestore
            .collection("users")
            .document(uid)
            .collection("generatedTimetable")
            .get()
            .addOnSuccessListener { result ->

                studyPlans.clear()

                result.documents.forEach { document ->

                    studyPlans.add(
                        StudyPlanItem(
                            id = document.id,
                            day = document.getString("day") ?: "",
                            subject = document.getString("subject") ?: "",
                            startHour =
                                document.getLong("startHour")
                                    ?.toInt()
                                    ?: 0,
                            startMinute =
                                document.getLong("startMinute")
                                    ?.toInt()
                                    ?: 0,
                            duration =
                                document.getLong("duration")
                                    ?.toInt()
                                    ?: 30,
                            reason =
                                document.getString("reason")
                                    ?: ""
                        )
                    )
                }
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Schedule",
                        fontWeight = FontWeight.SemiBold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

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
                            Modifier.padding(18.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier =
                                    Modifier.size(30.dp),
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                            )

                            Spacer(
                                modifier =
                                    Modifier.size(12.dp)
                            )

                            Text(
                                text = "Weekly Timetable",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Add your classes and generate a balanced weekly study timetable based on your subject performance.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )
                    }
                }
            }

            item {

                Button(

                    modifier =
                        Modifier.fillMaxWidth(),

                    onClick = {

                        selectedClass = null
                        selectedDay = "Monday"
                        subjectInput = ""
                        startTimeInput = ""
                        endTimeInput = ""
                        errorMessage = ""
                        showClassDialog = true
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(
                        modifier =
                            Modifier.size(8.dp)
                    )

                    Text(
                        text = "Add Class"
                    )
                }
            }

            if (classes.isNotEmpty()) {

                item {

                    Text(
                        text = "Your Classes",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(
                    items = classes,
                    key = { it.id }
                ) { item ->

                    ClassCard(
                        item = item,

                        onEditClick = {

                            selectedClass = item
                            selectedDay = item.day
                            subjectInput = item.subject

                            startTimeInput =
                                minutesToTime(
                                    item.startMinute
                                )

                            endTimeInput =
                                minutesToTime(
                                    item.endMinute
                                )

                            errorMessage = ""
                            showClassDialog = true
                        },

                        onDeleteClick = {

                            classToDelete = item
                            showDeleteDialog = true
                        }
                    )
                }
            }

            item {

                Button(

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isGenerating,

                    onClick = {

                        generateStudyPlan(
                            context = context,
                            firestore = firestore,
                            uid = currentUser?.uid,
                            classes = classes.toList(),

                            onStart = {

                                isGenerating = true
                                statusMessage = ""
                            },

                            onComplete = { generated ->

                                isGenerating = false

                                studyPlans.clear()
                                studyPlans.addAll(generated)

                                statusMessage =
                                    if (generated.isEmpty()) {
                                        "No suitable study periods were found."
                                    } else {
                                        "${generated.size} study sessions added to your weekly timetable."
                                    }

                                showGeneratedDialog = true
                            },

                            onError = { message ->

                                isGenerating = false
                                statusMessage = message
                            }
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.AutoAwesome,
                        contentDescription = null
                    )

                    Spacer(
                        modifier =
                            Modifier.size(8.dp)
                    )

                    Text(
                        text =
                            if (isGenerating) {
                                "Generating Timetable..."
                            } else {
                                "Generate Smart Timetable"
                            }
                    )
                }
            }

            if (statusMessage.isNotBlank()) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(14.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Notifications,
                                contentDescription = null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )

                            Spacer(
                                modifier =
                                    Modifier.size(10.dp)
                            )

                            Text(
                                text = statusMessage,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }
                    }
                }
            }

            if (studyPlans.isNotEmpty()) {

                item {

                    Text(
                        text = "Generated Timetable",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(8.dp)
                        ) {

                            Row(
                                modifier =
                                    Modifier.horizontalScroll(
                                        rememberScrollState()
                                    )
                            ) {

                                TimetableGrid(
                                    classes =
                                        classes.toList(),
                                    studyPlans =
                                        studyPlans.toList()
                                )
                            }
                        }
                    }
                }

                item {

                    OutlinedButton(

                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            val exported =
                                TimetableImageExporter
                                    .exportTimetable(
                                        context = context,
                                        classes = classes.toList(),
                                        studyPlans = studyPlans.toList()
                                    )

                            Toast.makeText(
                                context,
                                if (exported) {
                                    "Timetable exported successfully."
                                } else {
                                    "Unable to export timetable."
                                },
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Download,
                            contentDescription = null
                        )

                        Spacer(
                            modifier =
                                Modifier.size(8.dp)
                        )

                        Text(
                            text =
                                "Export Timetable as Image"
                        )
                    }
                }
            }

            item {

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }

    if (showClassDialog) {

        AlertDialog(

            onDismissRequest = {
                showClassDialog = false
            },

            title = {

                Text(
                    text =
                        if (selectedClass == null) {
                            "Add Class"
                        } else {
                            "Edit Class"
                        }
                )
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    ExposedDropdownMenuBox(

                        expanded = showDayMenu,

                        onExpandedChange = {
                            showDayMenu = !showDayMenu
                        }
                    ) {

                        OutlinedTextField(

                            value = selectedDay,

                            onValueChange = {},

                            readOnly = true,

                            label = {
                                Text("Day")
                            },

                            trailingIcon = {

                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded =
                                            showDayMenu
                                    )
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                        )

                        DropdownMenu(

                            expanded = showDayMenu,

                            onDismissRequest = {
                                showDayMenu = false
                            }
                        ) {

                            days.forEach { day ->

                                DropdownMenuItem(

                                    text = {
                                        Text(day)
                                    },

                                    onClick = {

                                        selectedDay = day
                                        showDayMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(

                        value = subjectInput,

                        onValueChange = {
                            subjectInput = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Subject")
                        },

                        singleLine = true
                    )

                    OutlinedTextField(

                        value = startTimeInput,

                        onValueChange = {
                            startTimeInput =
                                formatTimeInput(it)
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Start Time")
                        },

                        placeholder = {
                            Text("Example: 09:00")
                        },

                        singleLine = true
                    )

                    OutlinedTextField(

                        value = endTimeInput,

                        onValueChange = {
                            endTimeInput =
                                formatTimeInput(it)
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("End Time")
                        },

                        placeholder = {
                            Text("Example: 10:00")
                        },

                        singleLine = true
                    )

                    if (errorMessage.isNotBlank()) {

                        Text(
                            text = errorMessage,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val startMinute =
                            parseTimeToMinutes(
                                startTimeInput
                            )

                        val endMinute =
                            parseTimeToMinutes(
                                endTimeInput
                            )

                        when {

                            subjectInput.isBlank() -> {

                                errorMessage =
                                    "Please enter a subject."
                            }

                            startMinute == null ||
                                    endMinute == null -> {

                                errorMessage =
                                    "Please enter valid times using HH:mm format."
                            }

                            endMinute <= startMinute -> {

                                errorMessage =
                                    "End time must be later than start time."
                            }

                            hasTimeOverlap(
                                classes =
                                    classes,
                                day =
                                    selectedDay,
                                startMinute =
                                    startMinute,
                                endMinute =
                                    endMinute,
                                excludedId =
                                    selectedClass?.id
                            ) -> {

                                errorMessage =
                                    "This class overlaps with another class."
                            }

                            else -> {

                                saveClass(
                                    firestore =
                                        firestore,
                                    uid =
                                        currentUser?.uid,
                                    selectedClass =
                                        selectedClass,
                                    day =
                                        selectedDay,
                                    subject =
                                        subjectInput.trim(),
                                    startMinute =
                                        startMinute,
                                    endMinute =
                                        endMinute,

                                    onSuccess = { updatedClass ->

                                        if (selectedClass == null) {

                                            classes.add(
                                                updatedClass
                                            )

                                        } else {

                                            val index =
                                                classes.indexOfFirst {
                                                    it.id ==
                                                            updatedClass.id
                                                }

                                            if (index >= 0) {

                                                classes[index] =
                                                    updatedClass
                                            }
                                        }

                                        showClassDialog =
                                            false
                                    },

                                    onError = { message ->

                                        errorMessage = message
                                    }
                                )
                            }
                        }
                    }
                ) {

                    Text(
                        text = "Save"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showClassDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    if (
        showDeleteDialog &&
        classToDelete != null
    ) {

        AlertDialog(

            onDismissRequest = {

                showDeleteDialog = false
                classToDelete = null
            },

            title = {

                Text(
                    text = "Delete Class"
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to delete this class from your timetable?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val item = classToDelete
                        val uid = currentUser?.uid

                        if (
                            item != null &&
                            uid != null
                        ) {

                            firestore
                                .collection("users")
                                .document(uid)
                                .collection("timetable")
                                .document(item.id)
                                .delete()
                                .addOnSuccessListener {

                                    classes.remove(item)

                                    showDeleteDialog = false
                                    classToDelete = null
                                }
                        }
                    }
                ) {

                    Text(
                        text = "Delete"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDeleteDialog = false
                        classToDelete = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    if (showGeneratedDialog) {

        AlertDialog(

            onDismissRequest = {
                showGeneratedDialog = false
            },

            title = {

                Text(
                    text = "Timetable Generated"
                )
            },

            text = {

                Text(
                    text =
                        "Your weekly timetable has been generated using your classes and subject performance. The study sessions are distributed across the week to keep the timetable balanced."
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {
                        showGeneratedDialog = false
                    }
                ) {

                    Text(
                        text = "Done"
                    )
                }
            }
        )
    }
}

@Composable
private fun ClassCard(
    item: StudentClass,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.Schedule,
                contentDescription = null,
                modifier =
                    Modifier.size(28.dp),
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.size(10.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = item.subject,
                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        "${item.day} • ${item.time}",
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

            IconButton(
                onClick = onEditClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Edit,
                    contentDescription =
                        "Edit"
                )
            }

            IconButton(
                onClick = onDeleteClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Delete,
                    contentDescription =
                        "Delete"
                )
            }
        }
    }
}

@Composable
private fun TimetableGrid(
    classes: List<StudentClass>,
    studyPlans: List<StudyPlanItem>
) {

    val days =
        listOf(
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"
        )

    val fullDays =
        listOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )

    val timeWidth = 64.dp
    val dayWidth = 104.dp
    val rowHeight = 64.dp

    Column {

        Row {

            Box(
                modifier =
                    Modifier
                        .width(timeWidth)
                        .height(46.dp)
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        )
                        .border(
                            0.5.dp,
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "TIME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            days.forEach { day ->

                Box(
                    modifier =
                        Modifier
                            .width(dayWidth)
                            .height(46.dp)
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                            )
                            .border(
                                0.5.dp,
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = day,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        (8..21).forEach { hour ->

            Row {

                Box(
                    modifier =
                        Modifier
                            .width(timeWidth)
                            .height(rowHeight)
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                            )
                            .border(
                                0.5.dp,
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            formatHour(
                                hour
                            ),
                        fontSize = 9.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        textAlign =
                            TextAlign.Center
                    )
                }

                fullDays.forEach { day ->

                    val classItem =
                        findClassAtHour(
                            day =
                                day,
                            hour =
                                hour,
                            classes =
                                classes
                        )

                    val studyItem =
                        findStudyAtHour(
                            day =
                                day,
                            hour =
                                hour,
                            studyPlans =
                                studyPlans
                        )

                    val background =
                        when {

                            classItem != null ->
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer

                            studyItem != null ->
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer

                            else ->
                                MaterialTheme
                                    .colorScheme
                                    .surface
                        }

                    Box(
                        modifier =
                            Modifier
                                .width(dayWidth)
                                .height(rowHeight)
                                .background(
                                    background
                                )
                                .border(
                                    0.5.dp,
                                    MaterialTheme
                                        .colorScheme
                                        .outlineVariant
                                )
                                .padding(3.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        when {

                            classItem != null -> {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text =
                                            shortenSubject(
                                                classItem.subject
                                            ),
                                        fontSize = 10.sp,
                                        fontWeight =
                                            FontWeight.Bold,
                                        textAlign =
                                            TextAlign.Center,
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "CLASS",
                                        fontSize = 7.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }

                            studyItem != null -> {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text =
                                            shortenSubject(
                                                studyItem.subject
                                            ),
                                        fontSize = 10.sp,
                                        fontWeight =
                                            FontWeight.Bold,
                                        textAlign =
                                            TextAlign.Center,
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "STUDY",
                                        fontSize = 7.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Text(
                                        text =
                                            "${studyItem.duration}m",
                                        fontSize = 7.sp
                                    )
                                }
                            }

                            else -> {

                                Text(
                                    text = "",
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun findClassAtHour(
    day: String,
    hour: Int,
    classes: List<StudentClass>
): StudentClass? {

    val start =
        hour * 60

    val end =
        start + 60

    return classes.firstOrNull { item ->

        item.day.equals(
            day,
            ignoreCase = true
        ) &&
                item.startMinute < end &&
                item.endMinute > start
    }
}

private fun findStudyAtHour(
    day: String,
    hour: Int,
    studyPlans: List<StudyPlanItem>
): StudyPlanItem? {

    val start =
        hour * 60

    val end =
        start + 60

    return studyPlans.firstOrNull { item ->

        val itemStart =
            item.startHour * 60 +
                    item.startMinute

        val itemEnd =
            itemStart +
                    item.duration

        item.day.equals(
            day,
            ignoreCase = true
        ) &&
                itemStart < end &&
                itemEnd > start
    }
}

private fun generateStudyPlan(
    context: Context,
    firestore: FirebaseFirestore,
    uid: String?,
    classes: List<StudentClass>,
    onStart: () -> Unit,
    onComplete: (List<StudyPlanItem>) -> Unit,
    onError: (String) -> Unit
) {

    if (uid == null) {

        onError(
            "No signed-in student account was found."
        )

        return
    }

    if (classes.isEmpty()) {

        onError(
            "Please add at least one class before generating a timetable."
        )

        return
    }

    onStart()

    val userReference =
        firestore
            .collection("users")
            .document(uid)

    userReference
        .collection("subjects")
        .get()
        .addOnSuccessListener { result ->

            val subjects =
                result.documents.mapNotNull { document ->

                    val name =
                        document
                            .getString("name")
                            ?.trim()
                            ?: ""

                    if (name.isBlank()) {
                        null
                    } else {

                        val mark =
                            document
                                .getDouble("mark")
                                ?: document
                                    .getLong("mark")
                                    ?.toDouble()
                                ?: 0.0

                        SubjectPriority(
                            name = name,
                            mark = mark
                        )
                    }
                }

            if (subjects.isEmpty()) {

                onError(
                    "Please add your subjects and marks first."
                )

                return@addOnSuccessListener
            }

            val generated =
                createBalancedTimetable(
                    classes =
                        classes,
                    subjects =
                        subjects
                )

            userReference
                .collection("generatedTimetable")
                .get()
                .addOnSuccessListener { oldPlans ->

                    oldPlans.documents.forEach { document ->

                        val subject =
                            document
                                .getString("subject")
                                ?: ""

                        val day =
                            document
                                .getString("day")
                                ?: ""

                        val startHour =
                            document
                                .getLong("startHour")
                                ?.toInt()
                                ?: -1

                        val startMinute =
                            document
                                .getLong("startMinute")
                                ?.toInt()
                                ?: 0

                        val calendarDay =
                            dayToCalendarDay(
                                day
                            )

                        if (
                            subject.isNotBlank() &&
                            calendarDay != -1 &&
                            startHour >= 0
                        ) {

                            AlarmHelper
                                .cancelStudyReminder(
                                    context =
                                        context,
                                    subject =
                                        subject,
                                    dayOfWeek =
                                        calendarDay,
                                    hour =
                                        startHour,
                                    minute =
                                        startMinute
                                )
                        }
                    }

                    val deleteBatch =
                        firestore.batch()

                    oldPlans.documents.forEach { document ->

                        deleteBatch.delete(
                            document.reference
                        )
                    }

                    deleteBatch
                        .commit()
                        .addOnSuccessListener {

                            val saveBatch =
                                firestore.batch()

                            generated.forEach { item ->

                                val reference =
                                    userReference
                                        .collection(
                                            "generatedTimetable"
                                        )
                                        .document()

                                saveBatch.set(
                                    reference,
                                    hashMapOf(
                                        "day" to
                                                item.day,
                                        "subject" to
                                                item.subject,
                                        "startHour" to
                                                item.startHour,
                                        "startMinute" to
                                                item.startMinute,
                                        "duration" to
                                                item.duration,
                                        "reason" to
                                                item.reason
                                    )
                                )
                            }

                            saveBatch
                                .commit()
                                .addOnSuccessListener {

                                    StudyReminderScheduler
                                        .scheduleGeneratedStudyPlan(
                                            context =
                                                context,
                                            studyPlans =
                                                generated
                                        )

                                    val finalItems =
                                        generated.mapIndexed { index, item ->

                                            item.copy(
                                                id =
                                                    "generated_$index"
                                            )
                                        }

                                    onComplete(
                                        finalItems
                                    )
                                }
                                .addOnFailureListener {

                                    onError(
                                        it.message
                                            ?: "Unable to save the generated timetable."
                                    )
                                }
                        }
                        .addOnFailureListener {

                            onError(
                                it.message
                                    ?: "Unable to update the timetable."
                            )
                        }
                }
                .addOnFailureListener {

                    onError(
                        it.message
                            ?: "Unable to prepare the timetable."
                    )
                }
        }
        .addOnFailureListener {

            onError(
                it.message
                    ?: "Unable to load subjects."
            )
        }
}

private data class SubjectPriority(
    val name: String,
    val mark: Double
)

private fun createBalancedTimetable(
    classes: List<StudentClass>,
    subjects: List<SubjectPriority>
): List<StudyPlanItem> {

    val days =
        listOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )

    val sortedSubjects =
        subjects.sortedBy {
            it.mark
        }

    val targetCount =
        sortedSubjects.associate { subject ->

            subject.name to
                    if (subject.mark < 60) {
                        2
                    } else {
                        1
                    }
        }

    val assignedCount =
        mutableMapOf<String, Int>()

    val result =
        mutableListOf<StudyPlanItem>()

    val occupied =
        mutableMapOf<String, MutableList<Pair<Int, Int>>>()

    classes.forEach { classItem ->

        val day =
            normalizeDay(
                classItem.day
            )

        occupied
            .getOrPut(day) {
                mutableListOf()
            }
            .add(
                classItem.startMinute to
                        classItem.endMinute
            )
    }

    var subjectIndex = 0

    val maximumSessions =
        minOf(
            10,
            sortedSubjects.sumOf {
                targetCount[it.name] ?: 1
            }
        )

    var safetyCounter = 0

    while (
        result.size < maximumSessions &&
        safetyCounter < 100
    ) {

        safetyCounter++

        val availableSubjects =
            sortedSubjects.filter { subject ->

                val current =
                    assignedCount[
                        subject.name
                    ] ?: 0

                val target =
                    targetCount[
                        subject.name
                    ] ?: 1

                current < target
            }

        if (availableSubjects.isEmpty()) {
            break
        }

        val subject =
            availableSubjects[
                subjectIndex %
                        availableSubjects.size
            ]

        subjectIndex++

        val placed =
            findBestStudySlot(
                days =
                    days,
                occupied =
                    occupied,
                subject =
                    subject
            )

        if (placed == null) {
            continue
        }

        val day =
            placed.first

        val startMinute =
            placed.second

        val duration =
            studyDuration(
                subject.mark
            )

        val endMinute =
            startMinute +
                    duration

        val reason =
            when {

                subject.mark < 40 ->
                    "Priority subject based on current academic performance."

                subject.mark < 60 ->
                    "Additional practice recommended based on current academic performance."

                subject.mark < 80 ->
                    "Regular revision recommended to strengthen performance."

                else ->
                    "Short revision session to maintain performance."
            }

        result.add(
            StudyPlanItem(
                day =
                    day,
                subject =
                    subject.name,
                startHour =
                    startMinute / 60,
                startMinute =
                    startMinute % 60,
                duration =
                    duration,
                reason =
                    reason
            )
        )

        occupied
            .getOrPut(day) {
                mutableListOf()
            }
            .add(
                startMinute to
                        endMinute
            )

        assignedCount[
            subject.name
        ] =
            (
                    assignedCount[
                        subject.name
                    ] ?: 0
                    ) + 1
    }

    return result.sortedWith(
        compareBy<StudyPlanItem>(
            {
                dayToOrder(
                    it.day
                )
            },
            {
                it.startHour
            },
            {
                it.startMinute
            }
        )
    )
}

private fun findBestStudySlot(
    days: List<String>,
    occupied: Map<String, List<Pair<Int, Int>>>,
    subject: SubjectPriority
): Pair<String, Int>? {

    val duration =
        studyDuration(
            subject.mark
        )

    val preferredStart =
        if (subject.mark < 60) {
            18 * 60
        } else {
            17 * 60
        }

    val possibleSlots =
        mutableListOf<Pair<String, Int>>()

    days.forEach { day ->

        val dayOccupied =
            occupied[day]
                ?: emptyList()

        var minute =
            8 * 60

        while (
            minute + duration <=
            22 * 60
        ) {

            val slotEnd =
                minute + duration

            val overlaps =
                dayOccupied.any { range ->

                    minute < range.second &&
                            slotEnd > range.first
                }

            if (!overlaps) {

                possibleSlots.add(
                    day to minute
                )
            }

            minute += 30
        }
    }

    if (possibleSlots.isEmpty()) {
        return null
    }

    return possibleSlots.minByOrNull { slot ->

        val dayIndex =
            dayToOrder(
                slot.first
            )

        val distance =
            kotlin.math.abs(
                slot.second -
                        preferredStart
            )

        dayIndex * 100000 +
                distance
    }
}

private fun studyDuration(
    mark: Double
): Int {

    return when {

        mark < 40 ->
            60

        mark < 60 ->
            45

        mark < 80 ->
            30

        else ->
            20
    }
}

private fun hasTimeOverlap(
    classes: List<StudentClass>,
    day: String,
    startMinute: Int?,
    endMinute: Int?,
    excludedId: String?
): Boolean {

    if (
        startMinute == null ||
        endMinute == null
    ) {
        return false
    }

    return classes.any { item ->

        if (item.id == excludedId) {

            false

        } else if (
            !item.day.equals(
                day,
                ignoreCase = true
            )
        ) {

            false

        } else {

            startMinute < item.endMinute &&
                    endMinute > item.startMinute
        }
    }
}

private fun saveClass(
    firestore: FirebaseFirestore,
    uid: String?,
    selectedClass: StudentClass?,
    day: String,
    subject: String,
    startMinute: Int,
    endMinute: Int,
    onSuccess: (StudentClass) -> Unit,
    onError: (String) -> Unit
) {

    if (uid == null) {

        onError(
            "No signed-in student account was found."
        )

        return
    }

    val data =
        hashMapOf(
            "day" to day,
            "subject" to subject,
            "time" to
                    "${minutesToTime(startMinute)} - ${minutesToTime(endMinute)}",
            "startMinute" to startMinute,
            "endMinute" to endMinute
        )

    val reference =
        if (selectedClass == null) {

            firestore
                .collection("users")
                .document(uid)
                .collection("timetable")
                .document()

        } else {

            firestore
                .collection("users")
                .document(uid)
                .collection("timetable")
                .document(
                    selectedClass.id
                )
        }

    reference
        .set(data)
        .addOnSuccessListener {

            onSuccess(
                StudentClass(
                    id =
                        reference.id,
                    day =
                        day,
                    subject =
                        subject,
                    time =
                        "${minutesToTime(startMinute)} - ${minutesToTime(endMinute)}",
                    startMinute =
                        startMinute,
                    endMinute =
                        endMinute
                )
            )
        }
        .addOnFailureListener {

            onError(
                it.message
                    ?: "Unable to save class."
            )
        }
}

private fun parseTimeToMinutes(
    value: String
): Int? {

    val parts =
        value
            .trim()
            .split(":")

    if (parts.size != 2) {
        return null
    }

    val hour =
        parts[0]
            .toIntOrNull()

    val minute =
        parts[1]
            .toIntOrNull()

    if (
        hour == null ||
        minute == null
    ) {
        return null
    }

    if (
        hour !in 0..23 ||
        minute !in 0..59
    ) {
        return null
    }

    return hour * 60 +
            minute
}

private fun minutesToTime(
    totalMinutes: Int
): String {

    val hour =
        totalMinutes / 60

    val minute =
        totalMinutes % 60

    return String.format(
        Locale.getDefault(),
        "%02d:%02d",
        hour,
        minute
    )
}

private fun formatTimeInput(
    value: String
): String {

    val digits =
        value
            .filter {
                it.isDigit()
            }
            .take(4)

    return when {

        digits.length <= 2 ->
            digits

        else ->
            digits.take(2) +
                    ":" +
                    digits.drop(2)
    }
}

private fun formatHour(
    hour: Int
): String {

    val displayHour =
        when {

            hour == 0 ->
                12

            hour > 12 ->
                hour - 12

            else ->
                hour
        }

    val suffix =
        if (hour >= 12) {
            "PM"
        } else {
            "AM"
        }

    return "$displayHour $suffix"
}

private fun shortenSubject(
    value: String
): String {

    return if (
        value.length <= 16
    ) {
        value
    } else {
        value.take(13) + "..."
    }
}

private fun normalizeDay(
    day: String
): String {

    return when {

        day.equals(
            "Monday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("mon") ->
            "Monday"

        day.equals(
            "Tuesday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("tue") ->
            "Tuesday"

        day.equals(
            "Wednesday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("wed") ->
            "Wednesday"

        day.equals(
            "Thursday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("thu") ->
            "Thursday"

        day.equals(
            "Friday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("fri") ->
            "Friday"

        day.equals(
            "Saturday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("sat") ->
            "Saturday"

        day.equals(
            "Sunday",
            ignoreCase = true
        ) ||
                day.lowercase()
                    .startsWith("sun") ->
            "Sunday"

        else ->
            day
    }
}

private fun dayToOrder(
    day: String
): Int {

    return when (
        normalizeDay(day)
    ) {

        "Monday" ->
            0

        "Tuesday" ->
            1

        "Wednesday" ->
            2

        "Thursday" ->
            3

        "Friday" ->
            4

        "Saturday" ->
            5

        "Sunday" ->
            6

        else ->
            7
    }
}

private fun dayToCalendarDay(
    day: String
): Int {

    return when (
        normalizeDay(day)
    ) {

        "Monday" ->
            Calendar.MONDAY

        "Tuesday" ->
            Calendar.TUESDAY

        "Wednesday" ->
            Calendar.WEDNESDAY

        "Thursday" ->
            Calendar.THURSDAY

        "Friday" ->
            Calendar.FRIDAY

        "Saturday" ->
            Calendar.SATURDAY

        "Sunday" ->
            Calendar.SUNDAY

        else ->
            -1
    }
}