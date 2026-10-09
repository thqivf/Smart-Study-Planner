package com.example.smartstudyplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun login() {

        val trimmedEmail =
            email.trim()

        if (
            trimmedEmail.isBlank()
        ) {

            errorMessage =
                "Please enter your email address."

            return
        }

        if (
            password.isBlank()
        ) {

            errorMessage =
                "Please enter your password."

            return
        }

        isLoading = true
        errorMessage = ""

        val auth =
            FirebaseAuth.getInstance()

        val firestore =
            FirebaseFirestore.getInstance()

        auth
            .signInWithEmailAndPassword(
                trimmedEmail,
                password
            )
            .addOnSuccessListener {

                val user =
                    auth.currentUser

                if (
                    user == null
                ) {

                    isLoading = false

                    errorMessage =
                        "Unable to access the account."

                    return@addOnSuccessListener
                }

                firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        val role =
                            document.getString(
                                "role"
                            )

                        if (
                            role == "admin"
                        ) {

                            isLoading = false

                            onLoginSuccess()

                        } else {

                            auth.signOut()

                            isLoading = false

                            errorMessage =
                                "This account does not have administrator access."
                        }
                    }
                    .addOnFailureListener { exception ->

                        auth.signOut()

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: "Unable to verify administrator access."
                    }
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: "Login failed. Please check your email and password."
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            "Admin Login",
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

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .padding(
                        horizontal = 24.dp
                    ),
            contentAlignment =
                Alignment.TopCenter
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 40.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AdminPanelSettings,
                    contentDescription =
                        "Administrator",
                    modifier =
                        Modifier.size(
                            72.dp
                        ),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )

                Text(
                    text =
                        "Administrator Access",
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
                            8.dp
                        )
                )

                Text(
                    text =
                        "Sign in with an administrator account to access student information.",
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
                            28.dp
                        )
                )

                OutlinedTextField(
                    value =
                        email,
                    onValueChange = {
                        email = it
                        errorMessage = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Email"
                        )
                    },
                    singleLine = true,
                    enabled =
                        !isLoading
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

                OutlinedTextField(
                    value =
                        password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Password"
                        )
                    },
                    singleLine = true,
                    enabled =
                        !isLoading,
                    visualTransformation =
                        if (
                            passwordVisible
                        ) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible =
                                    !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (
                                        passwordVisible
                                    ) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                contentDescription =
                                    if (
                                        passwordVisible
                                    ) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    }
                            )
                        }
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                if (
                    errorMessage.isNotBlank()
                ) {

                    Text(
                        text =
                            errorMessage,
                        modifier =
                            Modifier.fillMaxWidth(),
                        color =
                            MaterialTheme
                                .colorScheme
                                .error,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )
                }

                Button(
                    onClick = {
                        login()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                52.dp
                            ),
                    enabled =
                        !isLoading
                ) {

                    if (
                        isLoading
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    22.dp
                                ),
                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Text(
                            text =
                                "Sign In"
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )

                Text(
                    text =
                        "Administrator accounts are restricted to authorized users.",
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