package com.example.smartstudyplanner

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    onBackClick: () -> Unit
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val auth =
        FirebaseAuth.getInstance()

    val firestore =
        FirebaseFirestore.getInstance()

    var subjects by remember {
        mutableStateOf(
            emptyList<String>()
        )
    }

    var selectedSubject by remember {
        mutableStateOf("")
    }

    var durationInput by remember {
        mutableStateOf("30")
    }

    var remainingSeconds by remember {
        mutableIntStateOf(0)
    }

    var totalSeconds by remember {
        mutableIntStateOf(0)
    }

    var isRunning by remember {
        mutableStateOf(false)
    }

    var isPaused by remember {
        mutableStateOf(false)
    }

    var isCompleted by remember {
        mutableStateOf(false)
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    var isLoadingSubjects by remember {
        mutableStateOf(true)
    }

    var message by remember {
        mutableStateOf("")
    }

    fun restoreNotificationMode() {

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (
            notificationManager.isNotificationPolicyAccessGranted
        ) {

            notificationManager.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_ALL
            )
        }
    }

    fun enableFocusNotifications() {

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (
            notificationManager.isNotificationPolicyAccessGranted
        ) {

            notificationManager.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
            )
        }
    }

    fun saveCompletedSession() {

        val user =
            auth.currentUser
                ?: return

        if (
            selectedSubject.isBlank()
        ) {
            return
        }

        val duration =
            totalSeconds / 60

        if (
            duration <= 0
        ) {
            return
        }

        val data =
            hashMapOf(
                "subject" to
                        selectedSubject,
                "duration" to
                        duration,
                "completed" to
                        true,
                "completedAt" to
                        System.currentTimeMillis()
            )

        firestore
            .collection("users")
            .document(user.uid)
            .collection("focusSessions")
            .add(data)
            .addOnSuccessListener {

                message =
                    "Focus session completed and recorded in Study Progress."
            }
            .addOnFailureListener {

                message =
                    "The session finished, but the progress record could not be saved."
            }
    }

    fun startFocusSession() {

        val duration =
            durationInput
                .toIntOrNull()

        if (
            selectedSubject.isBlank()
        ) {

            message =
                "Please select a subject before starting."

            return
        }

        if (
            duration == null ||
            duration <= 0
        ) {

            message =
                "Please enter a valid duration."

            return
        }

        if (
            duration > 180
        ) {

            message =
                "The maximum Focus Mode duration is 180 minutes."

            return
        }

        totalSeconds =
            duration * 60

        remainingSeconds =
            duration * 60

        isRunning =
            true

        isPaused =
            false

        isCompleted =
            false

        message =
            "Focus Mode started for $selectedSubject."

        enableFocusNotifications()
    }

    fun pauseFocusSession() {

        isPaused =
            true

        message =
            "Focus session paused."
    }

    fun resumeFocusSession() {

        isPaused =
            false

        message =
            "Focus session resumed."

        enableFocusNotifications()
    }

    fun stopFocusSession() {

        isRunning =
            false

        isPaused =
            false

        isCompleted =
            false

        remainingSeconds =
            0

        totalSeconds =
            0

        restoreNotificationMode()

        message =
            "Focus session stopped."
    }

    fun resetFocusMode() {

        isRunning =
            false

        isPaused =
            false

        isCompleted =
            false

        remainingSeconds =
            0

        totalSeconds =
            0

        restoreNotificationMode()

        message =
            "Focus session reset."
    }

    LaunchedEffect(Unit) {

        val user =
            auth.currentUser

        if (
            user == null
        ) {

            isLoadingSubjects =
                false

            message =
                "Please log in to use Focus Mode."

            return@LaunchedEffect
        }

        firestore
            .collection("users")
            .document(user.uid)
            .collection("subjects")
            .get()
            .addOnSuccessListener { result ->

                subjects =
                    result.documents
                        .mapNotNull { document ->

                            document
                                .getString(
                                    "name"
                                )
                                ?.trim()
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                        }
                        .distinct()
                        .sorted()

                isLoadingSubjects =
                    false
            }
            .addOnFailureListener {

                isLoadingSubjects =
                    false

                message =
                    "Unable to load your subjects."
            }
    }

    LaunchedEffect(
        isRunning,
        isPaused,
        remainingSeconds
    ) {

        if (
            isRunning &&
            !isPaused &&
            remainingSeconds > 0
        ) {

            delay(1000)

            remainingSeconds =
                remainingSeconds - 1

            if (
                remainingSeconds <= 0
            ) {

                isRunning =
                    false

                isPaused =
                    false

                isCompleted =
                    true

                restoreNotificationMode()

                saveCompletedSession()
            }
        }
    }

    LaunchedEffect(
        isRunning
    ) {

        if (
            !isRunning
        ) {
            restoreNotificationMode()
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
                                Icons.Default.Schedule,
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
                                "Focus Mode",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            restoreNotificationMode()

                            onBackClick()
                        }
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
                        horizontal = 20.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
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

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        ),

                    shape =
                        RoundedCornerShape(
                            18.dp
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
                                    Icons.Default.NotificationsOff,
                                contentDescription =
                                    null,
                                modifier =
                                    Modifier.size(
                                        28.dp
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        12.dp
                                    )
                            )

                            Column {

                                Text(
                                    text =
                                        "Focused Study",
                                    fontSize =
                                        18.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Select a subject and complete a focused study session.",
                                    fontSize =
                                        13.sp
                                )
                            }
                        }
                    }
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
                                    Icons.Default.MenuBook,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )

                            Text(
                                text =
                                    "Study Subject",
                                fontSize =
                                    17.sp,
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

                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            OutlinedButton(

                                onClick = {

                                    if (
                                        !isRunning
                                    ) {
                                        dropdownExpanded =
                                            true
                                    }
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    !isLoadingSubjects &&
                                            !isRunning
                            ) {

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.MenuBook,
                                        contentDescription =
                                            null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(
                                                10.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            if (
                                                selectedSubject.isBlank()
                                            ) {
                                                if (
                                                    isLoadingSubjects
                                                ) {
                                                    "Loading subjects..."
                                                } else {
                                                    "Select a subject"
                                                }
                                            } else {
                                                selectedSubject
                                            }
                                    )
                                }
                            }

                            DropdownMenu(

                                expanded =
                                    dropdownExpanded,

                                onDismissRequest = {

                                    dropdownExpanded =
                                        false
                                }
                            ) {

                                if (
                                    subjects.isEmpty()
                                ) {

                                    DropdownMenuItem(

                                        text = {

                                            Text(
                                                "No subjects added"
                                            )
                                        },

                                        onClick = {

                                            dropdownExpanded =
                                                false
                                        }
                                    )

                                } else {

                                    subjects.forEach { subject ->

                                        DropdownMenuItem(

                                            text = {

                                                Text(
                                                    subject
                                                )
                                            },

                                            onClick = {

                                                selectedSubject =
                                                    subject

                                                dropdownExpanded =
                                                    false

                                                message =
                                                    ""
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                                    Icons.Default.Schedule,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )

                            Text(
                                text =
                                    "Study Duration",
                                fontSize =
                                    17.sp,
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

                        OutlinedTextField(

                            value =
                                durationInput,

                            onValueChange = { value ->

                                if (
                                    value.all {
                                        it.isDigit()
                                    } &&
                                    value.length <= 3
                                ) {

                                    durationInput =
                                        value
                                }
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                !isRunning,

                            label = {

                                Text(
                                    "Duration in minutes"
                                )
                            },

                            singleLine =
                                true
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Recommended duration: 25 to 60 minutes.",
                            fontSize =
                                12.sp,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

            item {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 4.dp
                            ),

                    shape =
                        CircleShape,

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(
                                    270.dp
                                )
                                .clip(
                                    CircleShape
                                )
                                .background(
                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (
                            totalSeconds > 0
                        ) {

                            val progress =
                                (
                                        remainingSeconds
                                            .toFloat()
                                        ) /
                                        totalSeconds
                                            .toFloat()

                            CircularProgressIndicator(

                                progress = {
                                    progress
                                        .coerceIn(
                                            0f,
                                            1f
                                        )
                                },

                                modifier =
                                    Modifier.size(
                                        245.dp
                                    ),

                                strokeWidth =
                                    10.dp
                            )
                        }

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    formatRemainingTime(
                                        remainingSeconds
                                    ),
                                fontSize =
                                    42.sp,
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
                                    when {

                                        isCompleted ->
                                            "Completed"

                                        isRunning &&
                                                isPaused ->
                                            "Paused"

                                        isRunning ->
                                            selectedSubject

                                        else ->
                                            "Ready"
                                    },
                                fontSize =
                                    14.sp,
                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {

                if (
                    message.isNotBlank()
                ) {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .secondaryContainer
                            ),

                        shape =
                            RoundedCornerShape(
                                14.dp
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    14.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    if (
                                        isCompleted
                                    ) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Default.NotificationsOff
                                    },
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )

                            Text(
                                text =
                                    message,
                                fontSize =
                                    13.sp
                            )
                        }
                    }
                }
            }

            item {

                if (
                    !isRunning
                ) {

                    Button(

                        onClick =
                            ::startFocusSession,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    52.dp
                                ),

                        enabled =
                            selectedSubject.isNotBlank()
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.PlayArrow,
                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                if (
                                    isCompleted
                                ) {
                                    "Start New Session"
                                } else {
                                    "Start Focus Session"
                                }
                        )
                    }
                }
            }

            item {

                if (
                    isRunning
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {

                        Button(

                            onClick = {

                                if (
                                    isPaused
                                ) {
                                    resumeFocusSession()
                                } else {
                                    pauseFocusSession()
                                }
                            },

                            modifier =
                                Modifier
                                    .weight(
                                        1f
                                    )
                                    .height(
                                        52.dp
                                    )
                        ) {

                            Icon(
                                imageVector =
                                    if (
                                        isPaused
                                    ) {
                                        Icons.Default.PlayArrow
                                    } else {
                                        Icons.Default.Pause
                                    },
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        6.dp
                                    )
                            )

                            Text(
                                text =
                                    if (
                                        isPaused
                                    ) {
                                        "Resume"
                                    } else {
                                        "Pause"
                                    }
                            )
                        }

                        OutlinedButton(

                            onClick =
                                ::stopFocusSession,

                            modifier =
                                Modifier
                                    .weight(
                                        1f
                                    )
                                    .height(
                                        52.dp
                                    )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        6.dp
                                    )
                            )

                            Text(
                                text =
                                    "Stop"
                            )
                        }
                    }
                }
            }

            item {

                OutlinedButton(

                    onClick =
                        ::resetFocusMode,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isRunning
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Refresh,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )

                    Text(
                        text =
                            "Reset Focus Mode"
                    )
                }
            }

            item {

                OutlinedButton(

                    onClick = {

                        val intent =
                            Intent(
                                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                            )

                        context.startActivity(
                            intent
                        )
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.NotificationsOff,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )

                    Text(
                        text =
                            "Notification Access Settings"
                    )
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

private fun formatRemainingTime(
    totalSeconds: Int
): String {

    val safeSeconds =
        totalSeconds.coerceAtLeast(
            0
        )

    val minutes =
        safeSeconds / 60

    val seconds =
        safeSeconds % 60

    return String.format(
        Locale.getDefault(),
        "%02d:%02d",
        minutes,
        seconds
    )
}