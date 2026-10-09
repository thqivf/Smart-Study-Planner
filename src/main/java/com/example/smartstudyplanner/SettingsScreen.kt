package com.example.smartstudyplanner

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val context =
        LocalContext.current

    val auth =
        FirebaseAuth.getInstance()

    val firestore =
        FirebaseFirestore.getInstance()

    val currentUser =
        auth.currentUser

    var studentName by remember {
        mutableStateOf("Student")
    }

    var studentEmail by remember {
        mutableStateOf(
            currentUser?.email ?: "No email available"
        )
    }

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var showHelpScreen by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        currentUser?.uid
    ) {

        val uid =
            currentUser?.uid

        if (uid != null) {

            firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    val name =
                        document.getString("name")

                    val email =
                        document.getString("email")

                    if (
                        !name.isNullOrBlank()
                    ) {
                        studentName =
                            name
                    }

                    if (
                        !email.isNullOrBlank()
                    ) {
                        studentEmail =
                            email
                    }
                }
        }
    }

    if (showHelpScreen) {

        HelpScreen(
            onBackClick = {
                showHelpScreen = false
            }
        )

    } else {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {

                        Text(
                            text = "Settings",
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
                                    .surface,
                            titleContentColor =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface,
                            navigationIconContentColor =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                        )
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
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        16.dp
                    )
            ) {

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

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                16.dp
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    42.dp
                                ),
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
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
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )

                            Text(
                                text =
                                    studentEmail,
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

                Text(
                    text = "Appearance",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceContainer
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                if (isDarkMode) {
                                    Icons.Default.DarkMode
                                } else {
                                    Icons.Default.LightMode
                                },
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
                                Modifier.size(
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
                                text = "Dark Mode",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        3.dp
                                    )
                            )

                            Text(
                                text =
                                    if (isDarkMode) {
                                        "Dark appearance is enabled"
                                    } else {
                                        "Light appearance is enabled"
                                    },
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

                        Switch(
                            checked =
                                isDarkMode,
                            onCheckedChange =
                                onDarkModeChange
                        )
                    }
                }

                Text(
                    text = "Notifications",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceContainer
                        )
                ) {

                    Column {

                        Row(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Notifications,
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
                                    Modifier.size(
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
                                        "Notification Access",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleSmall,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            3.dp
                                        )
                                )

                                Text(
                                    text =
                                        "Manage notification and focus mode access",
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

                        Divider()

                        TextButton(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    ),

                            onClick = {

                                try {

                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                                        )
                                    )

                                } catch (
                                    exception: Exception
                                ) {

                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_SETTINGS
                                        )
                                    )
                                }
                            }

                        ) {

                            Text(
                                text =
                                    "Open Notification Access Settings"
                            )
                        }
                    }
                }

                Text(
                    text = "Application",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceContainer
                        )
                ) {

                    Column {

                        SettingsInformationRow(
                            icon =
                                Icons.Default.Settings,
                            title =
                                "Application",
                            value =
                                "Smart Study Planner"
                        )

                        Divider()

                        SettingsInformationRow(
                            icon =
                                Icons.Default.Info,
                            title =
                                "Version",
                            value =
                                "1.0"
                        )

                        Divider()

                        SettingsInformationRow(
                            icon =
                                Icons.Default.Security,
                            title =
                                "Account",
                            value =
                                "Student Account"
                        )

                        Divider()

                        TextButton(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    ),

                            onClick = {
                                showHelpScreen = true
                            }

                        ) {

                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

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
                                            "Help & User Manual",
                                        fontWeight =
                                            FontWeight.SemiBold,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurface
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                3.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            "Learn how to use Smart Study Planner",
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

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier
                                            .size(20.dp)
                                            .then(
                                                Modifier
                                            ),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                OutlinedButton(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                    onClick = {
                        showLogoutDialog = true
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Logout,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.size(8.dp)
                    )

                    Text(
                        text = "Log Out",
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Smart Study Planner",
                    modifier =
                        Modifier.fillMaxWidth(),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    textAlign =
                        androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text =
                        "Study smarter. Plan better.",
                    modifier =
                        Modifier.fillMaxWidth(),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    textAlign =
                        androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        if (showLogoutDialog) {

            AlertDialog(

                onDismissRequest = {
                    showLogoutDialog = false
                },

                icon = {

                    Icon(
                        imageVector =
                            Icons.Default.Logout,
                        contentDescription =
                            null
                    )
                },

                title = {

                    Text(
                        text = "Log Out"
                    )
                },

                text = {

                    Text(
                        text =
                            "Are you sure you want to log out of your account?"
                    )
                },

                confirmButton = {

                    TextButton(

                        onClick = {

                            showLogoutDialog = false
                            onLogoutClick()
                        }
                    ) {

                        Text(
                            text = "Log Out"
                        )
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {

                        Text(
                            text = "Cancel"
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsInformationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(18.dp),

        verticalAlignment =
            Alignment.CenterVertically
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
                Modifier.size(
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
                        .titleSmall,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(
                        3.dp
                    )
            )

            Text(
                text =
                    value,
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