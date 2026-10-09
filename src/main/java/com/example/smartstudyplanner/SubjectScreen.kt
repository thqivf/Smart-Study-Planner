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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class StudentSubject(
    val id: String = "",
    val name: String = "",
    val mark: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    onBackClick: () -> Unit
) {

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    val subjects =
        remember {
            mutableStateListOf<StudentSubject>()
        }

    var subjectName by remember {
        mutableStateOf("")
    }

    var mark by remember {
        mutableStateOf("")
    }

    var editingId by remember {
        mutableStateOf<String?>(null)
    }

    var showEditor by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var selectedSubject by remember {
        mutableStateOf<StudentSubject?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(
        auth.currentUser?.uid
    ) {

        val user =
            auth.currentUser

        if (
            user == null
        ) {

            isLoading =
                false

            return@LaunchedEffect
        }

        firestore
            .collection("users")
            .document(user.uid)
            .collection("subjects")
            .get()
            .addOnSuccessListener { documents ->

                subjects.clear()

                documents.forEach { document ->

                    val savedName =
                        document.getString("name")
                            ?: ""

                    val savedMark =
                        document.getDouble("mark")
                            ?: 0.0

                    subjects.add(
                        StudentSubject(
                            id =
                                document.id,
                            name =
                                savedName,
                            mark =
                                savedMark
                        )
                    )
                }

                subjects.sortBy {
                    it.name.lowercase()
                }

                isLoading =
                    false
            }
            .addOnFailureListener {

                isLoading =
                    false

                errorMessage =
                    "Unable to load your subjects."
            }
    }

    val averageMark =
        if (
            subjects.isNotEmpty()
        ) {
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

    Surface(
        modifier =
            Modifier.fillMaxSize(),
        color =
            MaterialTheme
                .colorScheme
                .background
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {

            TopAppBar(
                title = {

                    Text(
                        text =
                            "Subjects & Marks",
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
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .background
                    )
            )

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 20.dp
                        ),
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
                        shape =
                            RoundedCornerShape(20.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text =
                                        "Academic Overview",
                                    fontSize =
                                        20.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onPrimaryContainer
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )

                                Text(
                                    text =
                                        "Keep your marks updated so the planner can identify subjects that need more study time.",
                                    fontSize =
                                        13.sp,
                                    lineHeight =
                                        19.sp,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onPrimaryContainer
                                )
                            }

                            Icon(
                                imageVector =
                                    Icons.Default.School,
                                contentDescription =
                                    null,
                                modifier =
                                    Modifier.size(48.dp),
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                            )
                        }
                    }
                }

                item {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        SubjectSummaryCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Subjects",
                            value =
                                subjects.size.toString()
                        )

                        SubjectSummaryCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Average",
                            value =
                                String.format(
                                    "%.1f",
                                    averageMark
                                )
                        )

                        SubjectSummaryCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Weakest",
                            value =
                                weakestSubject
                                    ?.mark
                                    ?.let {
                                        String.format(
                                            "%.0f",
                                            it
                                        )
                                    }
                                    ?: "0"
                        )
                    }
                }

                item {

                    Button(
                        onClick = {

                            editingId =
                                null

                            subjectName =
                                ""

                            mark =
                                ""

                            errorMessage =
                                ""

                            showEditor =
                                true
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                        shape =
                            RoundedCornerShape(16.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,
                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Add Subject",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }

                item {

                    Text(
                        text =
                            "Your Subjects",
                        fontSize =
                            19.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )
                }

                if (
                    isLoading
                ) {

                    item {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 50.dp
                                    ),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator()

                            Spacer(
                                modifier =
                                    Modifier.height(14.dp)
                            )

                            Text(
                                text =
                                    "Loading subjects...",
                                fontSize =
                                    13.sp,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }

                } else if (
                    subjects.isEmpty()
                ) {

                    item {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(18.dp),
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
                                        .padding(28.dp),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.MenuBook,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(42.dp),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
                                )

                                Text(
                                    text =
                                        "No subjects added yet",
                                    fontSize =
                                        16.sp,
                                    fontWeight =
                                        FontWeight.SemiBold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(5.dp)
                                )

                                Text(
                                    text =
                                        "Add your subjects and marks to start building your study plan.",
                                    fontSize =
                                        13.sp,
                                    textAlign =
                                        TextAlign.Center,
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
                        subjects,
                        key = {
                            it.id
                        }
                    ) { subject ->

                        SubjectCard(
                            subject =
                                subject,
                            onEditClick = {

                                editingId =
                                    subject.id

                                subjectName =
                                    subject.name

                                mark =
                                    if (
                                        subject.mark % 1.0 == 0.0
                                    ) {
                                        subject.mark
                                            .toInt()
                                            .toString()
                                    } else {
                                        subject.mark.toString()
                                    }

                                errorMessage =
                                    ""

                                showEditor =
                                    true
                            },
                            onDeleteClick = {

                                selectedSubject =
                                    subject

                                showDeleteDialog =
                                    true
                            }
                        )
                    }
                }

                if (
                    errorMessage.isNotBlank() &&
                    !showEditor
                ) {

                    item {

                        Text(
                            text =
                                errorMessage,
                            modifier =
                                Modifier.fillMaxWidth(),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error,
                            fontSize =
                                13.sp
                        )
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
    }

    if (
        showEditor
    ) {

        AlertDialog(
            onDismissRequest = {

                if (
                    !isSaving
                ) {
                    showEditor =
                        false
                }
            },
            title = {

                Text(
                    text =
                        if (
                            editingId == null
                        ) {
                            "Add Subject"
                        } else {
                            "Edit Subject"
                        },
                    fontWeight =
                        FontWeight.Bold
                )
            },
            text = {

                Column {

                    OutlinedTextField(
                        value =
                            subjectName,
                        onValueChange = {
                            subjectName =
                                it
                            errorMessage =
                                ""
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        singleLine =
                            true,
                        label = {
                            Text(
                                "Subject Name"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.MenuBook,
                                contentDescription =
                                    null
                            )
                        },
                        shape =
                            RoundedCornerShape(14.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    OutlinedTextField(
                        value =
                            mark,
                        onValueChange = {
                            mark =
                                it
                                    .filter { character ->
                                        character.isDigit() ||
                                                character == '.'
                                    }
                            errorMessage =
                                ""
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        singleLine =
                            true,
                        label = {
                            Text(
                                "Mark (%)"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.School,
                                contentDescription =
                                    null
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Decimal
                            ),
                        shape =
                            RoundedCornerShape(14.dp)
                    )

                    if (
                        errorMessage.isNotBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                errorMessage,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error,
                            fontSize =
                                13.sp
                        )
                    }
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        val cleanName =
                            subjectName.trim()

                        val parsedMark =
                            mark.toDoubleOrNull()

                        when {

                            cleanName.isBlank() -> {

                                errorMessage =
                                    "Please enter a subject name."
                            }

                            parsedMark == null -> {

                                errorMessage =
                                    "Please enter a valid mark."
                            }

                            parsedMark < 0.0 ||
                                    parsedMark > 100.0 -> {

                                errorMessage =
                                    "Mark must be between 0 and 100."
                            }

                            else -> {

                                val user =
                                    auth.currentUser

                                if (
                                    user == null
                                ) {

                                    errorMessage =
                                        "You are not logged in."

                                    return@Button
                                }

                                isSaving =
                                    true

                                errorMessage =
                                    ""

                                val userSubjects =
                                    firestore
                                        .collection("users")
                                        .document(user.uid)
                                        .collection("subjects")

                                val data =
                                    hashMapOf(
                                        "name" to
                                                cleanName,
                                        "mark" to
                                                parsedMark
                                    )

                                if (
                                    editingId == null
                                ) {

                                    userSubjects
                                        .add(data)
                                        .addOnSuccessListener { document ->

                                            subjects.add(
                                                StudentSubject(
                                                    id =
                                                        document.id,
                                                    name =
                                                        cleanName,
                                                    mark =
                                                        parsedMark
                                                )
                                            )

                                            subjects.sortBy {
                                                it.name.lowercase()
                                            }

                                            isSaving =
                                                false

                                            showEditor =
                                                false
                                        }
                                        .addOnFailureListener {

                                            isSaving =
                                                false

                                            errorMessage =
                                                "Unable to save the subject."
                                        }

                                } else {

                                    userSubjects
                                        .document(
                                            editingId!!
                                        )
                                        .set(data)
                                        .addOnSuccessListener {

                                            val index =
                                                subjects.indexOfFirst {
                                                    it.id ==
                                                            editingId
                                                }

                                            if (
                                                index >= 0
                                            ) {

                                                subjects[index] =
                                                    StudentSubject(
                                                        id =
                                                            editingId!!,
                                                        name =
                                                            cleanName,
                                                        mark =
                                                            parsedMark
                                                    )

                                                subjects.sortBy {
                                                    it.name.lowercase()
                                                }
                                            }

                                            isSaving =
                                                false

                                            showEditor =
                                                false
                                        }
                                        .addOnFailureListener {

                                            isSaving =
                                                false

                                            errorMessage =
                                                "Unable to update the subject."
                                        }
                                }
                            }
                        }
                    },
                    enabled =
                        !isSaving,
                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    if (
                        isSaving
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),
                            strokeWidth =
                                2.dp,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary
                        )

                    } else {

                        Icon(
                            imageVector =
                                Icons.Default.Save,
                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                "Save"
                        )
                    }
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        if (
                            !isSaving
                        ) {
                            showEditor =
                                false
                        }
                    }
                ) {

                    Text(
                        text =
                            "Cancel"
                    )
                }
            }
        )
    }

    if (
        showDeleteDialog &&
        selectedSubject != null
    ) {

        AlertDialog(
            onDismissRequest = {

                showDeleteDialog =
                    false
            },
            title = {

                Text(
                    text =
                        "Delete Subject?",
                    fontWeight =
                        FontWeight.Bold
                )
            },
            text = {

                Text(
                    text =
                        "Are you sure you want to delete ${selectedSubject!!.name}? This action cannot be undone."
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        val user =
                            auth.currentUser

                        val subject =
                            selectedSubject

                        if (
                            user == null ||
                            subject == null
                        ) {

                            showDeleteDialog =
                                false

                            return@Button
                        }

                        firestore
                            .collection("users")
                            .document(user.uid)
                            .collection("subjects")
                            .document(subject.id)
                            .delete()
                            .addOnSuccessListener {

                                subjects.removeAll {
                                    it.id ==
                                            subject.id
                                }

                                selectedSubject =
                                    null

                                showDeleteDialog =
                                    false
                            }
                            .addOnFailureListener {

                                errorMessage =
                                    "Unable to delete the subject."

                                showDeleteDialog =
                                    false
                            }
                    },
                    colors =
                        androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .error,
                            contentColor =
                                MaterialTheme
                                    .colorScheme
                                    .onError
                        ),
                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Delete"
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        showDeleteDialog =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancel"
                    )
                }
            }
        )
    }
}

@Composable
private fun SubjectSummaryCard(
    modifier: Modifier,
    title: String,
    value: String
) {

    Card(
        modifier =
            modifier,
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 14.dp,
                        horizontal = 8.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    value,
                fontSize =
                    19.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    title,
                fontSize =
                    11.sp,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubjectCard(
    subject: StudentSubject,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val markStatus =
        when {
            subject.mark < 40.0 ->
                "Needs attention"

            subject.mark < 60.0 ->
                "Needs improvement"

            subject.mark < 80.0 ->
                "Good progress"

            else ->
                "Strong performance"
        }

    val statusColor =
        when {
            subject.mark < 40.0 ->
                MaterialTheme.colorScheme.error

            subject.mark < 60.0 ->
                MaterialTheme.colorScheme.tertiary

            else ->
                MaterialTheme.colorScheme.primary
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier =
                    Modifier.size(46.dp),
                shape =
                    RoundedCornerShape(13.dp),
                color =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MenuBook,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.padding(11.dp),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimaryContainer
                )
            }

            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        subject.name,
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        markStatus,
                    fontSize =
                        12.sp,
                    color =
                        statusColor,
                    fontWeight =
                        FontWeight.Medium
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text =
                        if (
                            subject.mark % 1.0 == 0.0
                        ) {
                            "${subject.mark.toInt()}%"
                        } else {
                            "${subject.mark}%"
                        },
                    fontSize =
                        19.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        statusColor
                )

                Row {

                    IconButton(
                        onClick =
                            onEditClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Edit,
                            contentDescription =
                                "Edit subject",
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick =
                            onDeleteClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,
                            contentDescription =
                                "Delete subject",
                            modifier =
                                Modifier.size(20.dp),
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                    }
                }
            }
        }
    }
}