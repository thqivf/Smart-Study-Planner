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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.PersonAdd
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
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
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

    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun registerUser() {

        val name =
            fullName.trim()

        val userEmail =
            email.trim()

        when {

            name.isBlank() -> {

                errorMessage =
                    "Please enter your full name."

                return
            }

            userEmail.isBlank() -> {

                errorMessage =
                    "Please enter your email address."

                return
            }

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(userEmail)
                .matches() -> {

                errorMessage =
                    "Please enter a valid email address."

                return
            }

            password.isBlank() -> {

                errorMessage =
                    "Please enter a password."

                return
            }

            password.length < 6 -> {

                errorMessage =
                    "Password must contain at least 6 characters."

                return
            }

            confirmPassword.isBlank() -> {

                errorMessage =
                    "Please confirm your password."

                return
            }

            password != confirmPassword -> {

                errorMessage =
                    "Passwords do not match."

                return
            }
        }

        errorMessage = ""
        isLoading = true

        auth
            .createUserWithEmailAndPassword(
                userEmail,
                password
            )
            .addOnCompleteListener { task ->

                if (
                    task.isSuccessful
                ) {

                    val user =
                        auth.currentUser

                    if (
                        user == null
                    ) {

                        isLoading = false

                        errorMessage =
                            "Registration failed. Please try again."

                        return@addOnCompleteListener
                    }

                    val userData =
                        hashMapOf(
                            "name" to name,
                            "email" to userEmail,
                            "role" to "student"
                        )

                    firestore
                        .collection("users")
                        .document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {

                            isLoading = false

                            onRegisterClick()
                        }
                        .addOnFailureListener { exception ->

                            isLoading = false

                            errorMessage =
                                exception.message
                                    ?: "Unable to save your account information."
                        }

                } else {

                    isLoading = false

                    errorMessage =
                        task.exception?.message
                            ?: "Registration failed. Please try again."
                }
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            "Create Account",
                        fontWeight =
                            FontWeight.SemiBold
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

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .padding(
                        horizontal = 28.dp,
                        vertical = 20.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.PersonAdd,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        64.dp
                    ),
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )

            Text(
                text =
                    "Register",
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
                        8.dp
                    )
            )

            Text(
                text =
                    "Create your Smart Study Planner account.",
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
                    fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Full Name"
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
                    email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Email Address"
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
                        14.dp
                    )
            )

            OutlinedTextField(
                value =
                    confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Confirm Password"
                    )
                },
                singleLine = true,
                enabled =
                    !isLoading,
                visualTransformation =
                    if (
                        confirmPasswordVisible
                    ) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingIcon = {

                    IconButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                    ) {

                        Icon(
                            imageVector =
                                if (
                                    confirmPasswordVisible
                                ) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription =
                                if (
                                    confirmPasswordVisible
                                ) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                        )
                    }
                }
            )

            if (
                errorMessage.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

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
            }

            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )

            Button(
                onClick = {
                    registerUser()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            52.dp
                        ),
                shape =
                    RoundedCornerShape(
                        14.dp
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
                            "Create Account",
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        "Already have an account?"
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            6.dp
                        )
                )

                androidx.compose.material3.TextButton(
                    onClick = {
                        onLoginClick()
                    },
                    enabled =
                        !isLoading
                ) {

                    Text(
                        text =
                            "Login",
                        fontWeight =
                            FontWeight.SemiBold
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