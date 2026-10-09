package com.example.smartstudyplanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

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

    fun loginUser() {

        val userEmail =
            email.trim()

        when {

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
                    "Please enter your password."

                return
            }
        }

        errorMessage = ""
        isLoading = true

        auth
            .signInWithEmailAndPassword(
                userEmail,
                password
            )
            .addOnCompleteListener { task ->

                isLoading = false

                if (
                    task.isSuccessful
                ) {

                    onLoginClick()

                } else {

                    errorMessage =
                        task.exception?.message
                            ?: "Login failed. Please check your email and password."
                }
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            "Login",
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
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
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
                        120.dp
                    ),
                contentScale =
                    ContentScale.Fit
            )

            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )

            Text(
                text =
                    "Smart Study Planner",
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
                        6.dp
                    )
            )

            Text(
                text =
                    "Sign in to manage your studies",
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
                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Lock,
                        contentDescription =
                            null
                    )
                },
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
                    loginUser()
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

            TextButton(
                onClick = {
                    onRegisterClick()
                },
                enabled =
                    !isLoading
            ) {

                Text(
                    text =
                        "Don't have an account? Register",
                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}