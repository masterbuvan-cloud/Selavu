package com.selavu.app.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selavu.app.ui.components.ConfirmDeleteDialog
import com.selavu.app.ui.components.SelavuPrimaryButton
import com.selavu.app.ui.theme.BackgroundLight
import com.selavu.app.ui.theme.DividerColor
import com.selavu.app.ui.theme.EmeraldDark
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.EmeraldLight
import com.selavu.app.ui.theme.ErrorRed
import com.selavu.app.ui.theme.ErrorRedSoft
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.Navy50
import com.selavu.app.ui.theme.TextPrimary
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.AuthViewModel
import com.selavu.app.viewmodel.ExpenseViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel,
    onLogout: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val totalTransactions by expenseViewModel.totalTransactions.collectAsState()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val userInitial = if (authState.currentUserEmail.isNotBlank()) {
        authState.currentUserEmail.first().uppercase()
    } else {
        "U"
    }

    if (showLogoutConfirmation) {
        ConfirmDeleteDialog(
            title = "Log Out?",
            message = "Are you sure you want to log out of Selavu?",
            onConfirm = {
                showLogoutConfirmation = false
                authViewModel.logout(onLogout)
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Avatar circle
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Navy800),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitial,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 36.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = authState.currentUserEmail.ifEmpty { "User Profile" },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(EmeraldLight)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Active Local Session",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EmeraldDark,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Account Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                ProfileInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email Address",
                    value = authState.currentUserEmail.ifEmpty { "Not specified" }
                )

                ProfileInfoRow(
                    icon = Icons.Default.Cake,
                    label = "Age",
                    value = if (authState.currentUserAge > 0) "${authState.currentUserAge} years" else "Not specified"
                )

                ProfileInfoRow(
                    icon = Icons.Default.Receipt,
                    label = "Total Expenses Logged",
                    value = "$totalTransactions records"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Security & Privacy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Security & Privacy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Text(
                    text = "• Authenticated securely with Firebase Authentication.\n• Expenses stored under your private account in Cloud Firestore.\n• Real-time synchronization and offline caching.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Logout Button
        SelavuPrimaryButton(
            text = "Log Out",
            onClick = { showLogoutConfirmation = true },
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            containerColor = ErrorRed,
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Version info
        Text(
            text = "Selavu v1.0.0 (Daily Expense Tracker)",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Navy50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Navy800,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
        }
    }
}

