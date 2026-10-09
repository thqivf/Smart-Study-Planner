package com.example.smartstudyplanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

private val LightColors =
    lightColorScheme()

private val DarkColors =
    darkColorScheme()

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                1001
            )
        }

        setContent {

            var isDarkMode by remember {
                mutableStateOf(false)
            }

            SmartStudyPlannerApp(
                isDarkMode =
                    isDarkMode,
                onDarkModeChange = {
                    isDarkMode = it
                }
            )
        }
    }
}

@Composable
fun SmartStudyPlannerApp(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    MaterialTheme(
        colorScheme =
            if (isDarkMode) {
                DarkColors
            } else {
                LightColors
            }
    ) {

        var currentScreen by remember {

            mutableStateOf(
                if (
                    FirebaseAuth
                        .getInstance()
                        .currentUser != null
                ) {
                    "dashboard"
                } else {
                    "welcome"
                }
            )
        }

        var selectedStudentId by remember {
            mutableStateOf("")
        }

        when (
            currentScreen
        ) {

            "welcome" -> {

                WelcomeScreen(
                    onLoginClick = {
                        currentScreen =
                            "login"
                    },
                    onRegisterClick = {
                        currentScreen =
                            "register"
                    },
                    onAdminClick = {
                        currentScreen =
                            "adminLogin"
                    }
                )
            }

            "login" -> {

                LoginScreen(
                    onLoginClick = {

                        val user =
                            FirebaseAuth
                                .getInstance()
                                .currentUser

                        if (
                            user != null
                        ) {
                            currentScreen =
                                "dashboard"
                        }
                    },
                    onRegisterClick = {
                        currentScreen =
                            "register"
                    },
                    onBackClick = {
                        currentScreen =
                            "welcome"
                    }
                )
            }

            "register" -> {

                RegisterScreen(
                    onRegisterClick = {

                        currentScreen =
                            "dashboard"
                    },
                    onLoginClick = {

                        currentScreen =
                            "login"
                    },
                    onBackClick = {

                        currentScreen =
                            "welcome"
                    }
                )
            }

            "dashboard" -> {

                DashboardScreen(
                    onSubjectsClick = {

                        currentScreen =
                            "subjects"
                    },
                    onScheduleClick = {

                        currentScreen =
                            "schedule"
                    },
                    onFocusClick = {

                        currentScreen =
                            "focus"
                    },
                    onProgressClick = {

                        currentScreen =
                            "progress"
                    },
                    onAIClick = {

                        currentScreen =
                            "ai"
                    },
                    onSettingsClick = {

                        currentScreen =
                            "settings"
                    },
                    onLogoutClick = {

                        FirebaseAuth
                            .getInstance()
                            .signOut()

                        currentScreen =
                            "welcome"
                    }
                )
            }

            "subjects" -> {

                SubjectScreen(
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    }
                )
            }

            "schedule" -> {

                ScheduleScreen(
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    }
                )
            }

            "focus" -> {

                FocusScreen(
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    }
                )
            }

            "progress" -> {

                StudyProgressScreen(
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    }
                )
            }

            "ai" -> {

                AIAssistantScreen(
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    }
                )
            }

            "settings" -> {

                SettingsScreen(
                    isDarkMode =
                        isDarkMode,
                    onDarkModeChange = {
                        onDarkModeChange(
                            it
                        )
                    },
                    onBackClick = {

                        currentScreen =
                            "dashboard"
                    },
                    onLogoutClick = {

                        FirebaseAuth
                            .getInstance()
                            .signOut()

                        currentScreen =
                            "welcome"
                    }
                )
            }

            "adminLogin" -> {

                AdminLoginScreen(
                    onLoginSuccess = {

                        currentScreen =
                            "adminDashboard"
                    },
                    onBackClick = {

                        currentScreen =
                            "welcome"
                    }
                )
            }

            "adminDashboard" -> {

                AdminDashboardScreen(
                    onStudentClick = {
                            studentId ->

                        selectedStudentId =
                            studentId

                        currentScreen =
                            "adminStudentDetails"
                    },
                    onLogoutClick = {

                        FirebaseAuth
                            .getInstance()
                            .signOut()

                        currentScreen =
                            "welcome"
                    }
                )
            }

            "adminStudentDetails" -> {

                AdminStudentDetailsScreen(
                    studentId =
                        selectedStudentId,
                    onBackClick = {

                        currentScreen =
                            "adminDashboard"
                    }
                )
            }
        }
    }
}