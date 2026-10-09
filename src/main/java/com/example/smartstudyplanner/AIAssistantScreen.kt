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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

data class AIChatMessage(
    val message: String,
    val isUser: Boolean
)

data class AISubjectData(
    val name: String,
    val mark: Double
)

data class AIFocusData(
    val subject: String,
    val duration: Int,
    val completed: Boolean
)

data class AIClassData(
    val day: String,
    val subject: String,
    val time: String
)

data class AIStudyPlanData(
    val day: String,
    val subject: String,
    val startHour: Int,
    val startMinute: Int,
    val duration: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onBackClick: () -> Unit
) {

    val auth =
        FirebaseAuth.getInstance()

    val firestore =
        FirebaseFirestore.getInstance()

    val messages =
        remember {
            mutableStateListOf<AIChatMessage>()
        }

    var inputMessage by remember {
        mutableStateOf("")
    }

    var studentName by remember {
        mutableStateOf("")
    }

    var subjects by remember {
        mutableStateOf(
            emptyList<AISubjectData>()
        )
    }

    var focusSessions by remember {
        mutableStateOf(
            emptyList<AIFocusData>()
        )
    }

    var classes by remember {
        mutableStateOf(
            emptyList<AIClassData>()
        )
    }

    var studyPlans by remember {
        mutableStateOf(
            emptyList<AIStudyPlanData>()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    fun weakestSubject(): AISubjectData? {
        return subjects.minByOrNull {
            it.mark
        }
    }

    fun strongestSubject(): AISubjectData? {
        return subjects.maxByOrNull {
            it.mark
        }
    }

    fun averageMark(): Double {

        if (
            subjects.isEmpty()
        ) {
            return 0.0
        }

        return subjects
            .map {
                it.mark
            }
            .average()
    }

    fun totalCompletedSessions(): Int {

        return focusSessions.count {
            it.completed
        }
    }

    fun totalStudyMinutes(): Int {

        return focusSessions
            .filter {
                it.completed
            }
            .sumOf {
                it.duration
            }
    }

    fun subjectStudyMinutes(
        subjectName: String
    ): Int {

        return focusSessions
            .filter {
                it.completed &&
                        it.subject.equals(
                            subjectName,
                            ignoreCase = true
                        )
            }
            .sumOf {
                it.duration
            }
    }

    fun generateResponse(
        question: String
    ): String {

        val normalized =
            question
                .trim()
                .lowercase()

        val weakest =
            weakestSubject()

        val strongest =
            strongestSubject()

        val average =
            averageMark()

        val completedSessions =
            totalCompletedSessions()

        val totalMinutes =
            totalStudyMinutes()

        if (
            normalized.isBlank()
        ) {

            return "Please enter a question about your studies."
        }

        if (
            normalized.contains("hello") ||
            normalized.contains("hi") ||
            normalized == "hey"
        ) {

            return if (
                studentName.isBlank()
            ) {

                "Hello. I am your academic assistant. How can I help you with your studies?"

            } else {

                "Hello, $studentName. I am ready to help you with your studies."
            }
        }

        if (
            normalized.contains("weakest") ||
            normalized.contains("weak subject") ||
            normalized.contains("need improvement") ||
            normalized.contains("focus on")
        ) {

            return if (
                weakest != null
            ) {

                val studyTime =
                    subjectStudyMinutes(
                        weakest.name
                    )

                "Your current weakest subject is ${weakest.name} with a mark of ${formatMark(weakest.mark)}%. You have completed $studyTime minutes of Focus Mode study for this subject. I recommend giving ${weakest.name} additional study time."

            } else {

                "You have not added any subjects and marks yet. Add your subjects first so I can identify which subject needs the most attention."
            }
        }

        if (
            normalized.contains("strongest") ||
            normalized.contains("best subject")
        ) {

            return if (
                strongest != null
            ) {

                "Your strongest subject is ${strongest.name} with a mark of ${formatMark(strongest.mark)}%. Continue regular revision to maintain this performance."

            } else {

                "You have not added any subjects and marks yet."
            }
        }

        if (
            normalized.contains("my marks") ||
            normalized.contains("my mark") ||
            normalized.contains("marks") ||
            normalized.contains("grades")
        ) {

            if (
                subjects.isEmpty()
            ) {

                return "You have not added any subjects and marks yet."
            }

            return subjects.joinToString(
                separator = "\n"
            ) {
                "${it.name}: ${formatMark(it.mark)}%"
            }
        }

        if (
            normalized.contains("average") ||
            normalized.contains("overall") ||
            normalized.contains("performance")
        ) {

            return if (
                subjects.isEmpty()
            ) {

                "I cannot calculate your academic average yet because you have not added any subjects and marks."

            } else {

                "Your current average mark is ${formatMark(average)}% across ${subjects.size} subjects."
            }
        }

        if (
            normalized.contains("progress") ||
            normalized.contains("study time") ||
            normalized.contains("studied") ||
            normalized.contains("study hours")
        ) {

            val hours =
                totalMinutes / 60

            val minutes =
                totalMinutes % 60

            return "You have completed $completedSessions Focus Mode session${if (completedSessions == 1) "" else "s"} with a total study time of ${hours}h ${minutes}m."
        }

        if (
            normalized.contains("timetable") ||
            normalized.contains("schedule") ||
            normalized.contains("class") ||
            normalized.contains("today")
        ) {

            if (
                studyPlans.isNotEmpty()
            ) {

                val plan =
                    studyPlans
                        .sortedWith(
                            compareBy(
                                {
                                    dayOrder(
                                        it.day
                                    )
                                },
                                {
                                    it.startHour * 60 +
                                            it.startMinute
                                }
                            )
                        )
                        .take(5)

                val planText =
                    plan.joinToString(
                        separator = "\n"
                    ) {

                        "${it.day}: ${it.subject} at ${
                            formatTime(
                                it.startHour,
                                it.startMinute
                            )
                        } for ${it.duration} minutes"
                    }

                return "Your generated study timetable currently contains ${studyPlans.size} sessions. Here are some study sessions:\n$planText"
            }

            if (
                classes.isNotEmpty()
            ) {

                val classText =
                    classes
                        .take(5)
                        .joinToString(
                            separator = "\n"
                        ) {

                            "${it.day}: ${it.subject} at ${it.time}"
                        }

                return "You currently have ${classes.size} scheduled classes:\n$classText"
            }

            return "You have not created a timetable yet. Add your classes in Schedule and generate a Smart Timetable."
        }

        if (
            normalized.contains("what should i study") ||
            normalized.contains("what should i revise") ||
            normalized.contains("study first") ||
            normalized.contains("recommend")
        ) {

            if (
                weakest == null
            ) {

                return "Add your subjects and marks first. I can then recommend which subject should receive more attention."
            }

            val studyTime =
                subjectStudyMinutes(
                    weakest.name
                )

            return "Based on your current marks, I recommend prioritising ${weakest.name}. Your mark is ${formatMark(weakest.mark)}%, and you have completed $studyTime minutes of study for this subject."
        }

        if (
            normalized.contains("advice") ||
            normalized.contains("improve") ||
            normalized.contains("improvement") ||
            normalized.contains("how can i")
        ) {

            if (
                weakest == null
            ) {

                return "Start by adding your subjects and marks. This will allow me to provide personalised academic advice."
            }

            return when {

                weakest.mark < 40 ->

                    "Your ${weakest.name} mark is below 40%. Prioritise this subject in your study timetable, review the fundamentals, and use Focus Mode for longer sessions."

                weakest.mark < 60 ->

                    "Your ${weakest.name} mark is ${formatMark(weakest.mark)}%. Give this subject additional revision time and practise questions regularly."

                weakest.mark < 80 ->

                    "Your ${weakest.name} mark is ${formatMark(weakest.mark)}%. Continue regular revision and focus on areas where you lose marks."

                else ->

                    "Your current marks show good performance. Continue consistent revision while maintaining attention on your lower-scoring subjects."
            }
        }

        if (
            normalized.contains("focus mode") ||
            normalized.contains("focus session")
        ) {

            return "Focus Mode allows you to select a subject, set a study duration, and complete a focused session. Completed sessions are recorded in Study Progress."
        }

        if (
            normalized.contains("subject")
        ) {

            return if (
                subjects.isEmpty()
            ) {

                "You have not added any subjects yet."

            } else {

                "You currently have ${subjects.size} subjects: ${
                    subjects.joinToString(
                        ", "
                    ) {
                        it.name
                    }
                }."
            }
        }

        if (
            normalized.contains("thank") ||
            normalized.contains("thanks")
        ) {

            return "You're welcome. Keep your study sessions consistent and use your timetable to manage your time effectively."
        }

        return "I can help you with your subjects, marks, academic performance, study progress, Focus Mode, timetable, and study recommendations. Try asking me which subject needs more attention or how your study progress is going."
    }

    fun sendMessage() {

        val question =
            inputMessage.trim()

        if (
            question.isBlank()
        ) {
            return
        }

        messages.add(
            AIChatMessage(
                message =
                    question,
                isUser =
                    true
            )
        )

        inputMessage =
            ""

        messages.add(
            AIChatMessage(
                message =
                    generateResponse(
                        question
                    ),
                isUser =
                    false
            )
        )
    }

    LaunchedEffect(Unit) {

        val user =
            auth.currentUser

        if (
            user == null
        ) {

            isLoading =
                false

            messages.add(
                AIChatMessage(
                    message =
                        "Please log in to use the academic assistant.",
                    isUser =
                        false
                )
            )

            return@LaunchedEffect
        }

        val userRef =
            firestore
                .collection("users")
                .document(user.uid)

        userRef
            .get()
            .addOnSuccessListener { document ->

                studentName =
                    document.getString(
                        "name"
                    ) ?: ""
            }

        userRef
            .collection("subjects")
            .get()
            .addOnSuccessListener { result ->

                subjects =
                    result.documents
                        .mapNotNull { document ->

                            val name =
                                document
                                    .getString(
                                        "name"
                                    )
                                    ?.trim()
                                    ?: ""

                            if (
                                name.isBlank()
                            ) {
                                null
                            } else {

                                AISubjectData(
                                    name =
                                        name,
                                    mark =
                                        document
                                            .getDouble(
                                                "mark"
                                            )
                                            ?: 0.0
                                )
                            }
                        }
                        .sortedBy {
                            it.mark
                        }
            }

        userRef
            .collection("focusSessions")
            .get()
            .addOnSuccessListener { result ->

                focusSessions =
                    result.documents.map { document ->

                        AIFocusData(
                            subject =
                                document.getString(
                                    "subject"
                                ) ?: "",
                            duration =
                                document
                                    .getLong(
                                        "duration"
                                    )
                                    ?.toInt()
                                    ?: 0,
                            completed =
                                document
                                    .getBoolean(
                                        "completed"
                                    )
                                    ?: false
                        )
                    }
            }

        userRef
            .collection("timetable")
            .get()
            .addOnSuccessListener { result ->

                classes =
                    result.documents.map { document ->

                        AIClassData(
                            day =
                                document.getString(
                                    "day"
                                ) ?: "",
                            subject =
                                document.getString(
                                    "subject"
                                ) ?: "",
                            time =
                                document.getString(
                                    "time"
                                ) ?: ""
                        )
                    }
            }

        userRef
            .collection("generatedTimetable")
            .get()
            .addOnSuccessListener { result ->

                studyPlans =
                    result.documents.map { document ->

                        AIStudyPlanData(
                            day =
                                document.getString(
                                    "day"
                                ) ?: "",
                            subject =
                                document.getString(
                                    "subject"
                                ) ?: "",
                            startHour =
                                document
                                    .getLong(
                                        "startHour"
                                    )
                                    ?.toInt()
                                    ?: 0,
                            startMinute =
                                document
                                    .getLong(
                                        "startMinute"
                                    )
                                    ?.toInt()
                                    ?: 0,
                            duration =
                                document
                                    .getLong(
                                        "duration"
                                    )
                                    ?.toInt()
                                    ?: 0
                        )
                    }
            }

        isLoading =
            false

        if (
            messages.isEmpty()
        ) {

            messages.add(
                AIChatMessage(
                    message =
                        if (
                            studentName.isBlank()
                        ) {
                            "Hello. I am your academic assistant. I can help you understand your subjects, marks, study progress, timetable, and study priorities."
                        } else {
                            "Hello, $studentName. I am your academic assistant. I can help you with your subjects, marks, study progress, timetable, and study priorities."
                        },
                    isUser =
                        false
                )
            )
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

                        Icon(
                            imageVector =
                                Icons.Default.AutoAwesome,
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
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "AI Assistant",
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

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
        ) {

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    ),

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

                    Text(
                        text =
                            "Academic Assistant",
                        fontSize =
                            18.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            if (
                                isLoading
                            ) {
                                "Loading your academic information..."
                            } else {
                                "Your assistant is connected to your subjects, timetable, and study progress."
                            },
                        fontSize =
                            13.sp
                    )
                }
            }

            if (
                subjects.isNotEmpty()
            ) {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp
                            ),

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

                        Text(
                            text =
                                "Academic Summary",
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
                                "Average: ${formatMark(averageMark())}%"
                        )

                        Text(
                            text =
                                "Weakest: ${
                                    weakestSubject()
                                        ?.name
                                        ?: "-"
                                }"
                        )

                        Text(
                            text =
                                "Strongest: ${
                                    strongestSubject()
                                        ?.name
                                        ?: "-"
                                }"
                        )

                        Text(
                            text =
                                "Study time: ${totalStudyMinutes()} minutes"
                        )
                    }
                }
            }

            LazyColumn(

                modifier =
                    Modifier
                        .weight(
                            1f
                        )
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
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

                items(
                    messages
                ) { message ->

                    AIMessageCard(
                        message =
                            message
                    )
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

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            16.dp
                        )
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    Button(
                        onClick = {

                            inputMessage =
                                "Which subject should I focus on?"
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(
                            text =
                                "Study Priority",
                            fontSize =
                                11.sp
                        )
                    }

                    Button(
                        onClick = {

                            inputMessage =
                                "How is my study progress?"
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(
                            text =
                                "My Progress",
                            fontSize =
                                11.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.Bottom
                ) {

                    OutlinedTextField(

                        value =
                            inputMessage,

                        onValueChange = {
                            inputMessage =
                                it
                        },

                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        label = {

                            Text(
                                "Ask the assistant"
                            )
                        },

                        maxLines =
                            4
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )

                    IconButton(

                        onClick =
                            ::sendMessage,

                        enabled =
                            inputMessage
                                .isNotBlank()
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Send,
                            contentDescription =
                                "Send"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AIMessageCard(
    message: AIChatMessage
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (
                message.isUser
            ) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Card(

            modifier =
                Modifier.fillMaxWidth(
                    0.88f
                ),

            shape =
                RoundedCornerShape(
                    16.dp
                ),

            colors =
                if (
                    message.isUser
                ) {

                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )

                } else {

                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    )
                }
        ) {

            Text(
                text =
                    message.message,
                modifier =
                    Modifier.padding(
                        14.dp
                    ),
                fontSize =
                    14.sp
            )
        }
    }
}

private fun formatMark(
    mark: Double
): String {

    return String.format(
        Locale.getDefault(),
        "%.1f",
        mark
    )
}

private fun dayOrder(
    day: String
): Int {

    return when (
        day.lowercase()
    ) {

        "monday" ->
            0

        "tuesday" ->
            1

        "wednesday" ->
            2

        "thursday" ->
            3

        "friday" ->
            4

        "saturday" ->
            5

        "sunday" ->
            6

        else ->
            7
    }
}

private fun formatTime(
    hour: Int,
    minute: Int
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
        if (
            hour >= 12
        ) {
            "PM"
        } else {
            "AM"
        }

    return String.format(
        Locale.getDefault(),
        "%d:%02d %s",
        displayHour,
        minute,
        suffix
    )
}