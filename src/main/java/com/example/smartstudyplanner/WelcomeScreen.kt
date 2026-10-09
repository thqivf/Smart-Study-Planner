package com.example.smartstudyplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onAdminClick: () -> Unit
) {

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
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 28.dp,
                        vertical = 32.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Box(
                modifier =
                    Modifier
                        .size(110.dp)
                        .clip(
                            RoundedCornerShape(
                                30.dp
                            )
                        )
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.School,
                    contentDescription =
                        "Smart Study Planner",
                    modifier =
                        Modifier.size(58.dp),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimaryContainer
                )
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            Text(
                text =
                    "Smart Study Planner",
                fontSize =
                    30.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "Plan smarter. Study better. Stay on track.",
                fontSize =
                    16.sp,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center,
                lineHeight =
                    24.sp
            )

            Spacer(
                modifier =
                    Modifier.height(38.dp)
            )

            Button(
                onClick =
                    onLoginClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        contentColor =
                            MaterialTheme
                                .colorScheme
                                .onPrimary
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Login,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.size(10.dp)
                )

                Text(
                    text =
                        "Login",
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Icon(
                    imageVector =
                        Icons.Default.ArrowForward,
                    contentDescription =
                        null
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick =
                    onRegisterClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape =
                    RoundedCornerShape(16.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.PersonAdd,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.size(10.dp)
                )

                Text(
                    text =
                        "Create Account",
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Icon(
                    imageVector =
                        Icons.Default.ArrowForward,
                    contentDescription =
                        null
                )
            }

            Spacer(
                modifier =
                    Modifier.height(34.dp)
            )

            OutlinedButton(
                onClick =
                    onAdminClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AdminPanelSettings,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )

                Text(
                    text =
                        "Administrator Access",
                    fontSize =
                        14.sp,
                    fontWeight =
                        FontWeight.Medium
                )
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            Text(
                text =
                    "Academic productivity and study management",
                fontSize =
                    12.sp,
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