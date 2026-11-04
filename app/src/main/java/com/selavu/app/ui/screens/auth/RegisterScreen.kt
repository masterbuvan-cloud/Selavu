package com.selavu.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selavu.app.ui.components.SelavuPrimaryButton
import com.selavu.app.ui.components.SelavuTextField
import com.selavu.app.ui.components.SelavuTopBar
import com.selavu.app.ui.theme.BackgroundLight
import com.selavu.app.ui.theme.EmeraldDark
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.ErrorRed
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.TextPrimary
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { success ->
            snackbarHostState.showSnackbar(success)
        }
    }

    Scaffold(
        topBar = {
            SelavuTopBar(
                title = "Create Account",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Join Selavu",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy800
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Start taking control of your daily expenses today.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
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
                    // Email
                    SelavuTextField(
                        value = uiState.email,
                        onValueChange = { authViewModel.onEmailChange(it) },
                        label = "Email Address",
                        placeholder = "you@example.com",
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Navy800)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Age
                    SelavuTextField(
                        value = uiState.age,
                        onValueChange = { authViewModel.onAgeChange(it) },
                        label = "Age",
                        placeholder = "e.g. 25",
                        leadingIcon = {
                            Icon(Icons.Default.Cake, contentDescription = null, tint = Navy800)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Password
                    SelavuTextField(
                        value = uiState.password,
                        onValueChange = { authViewModel.onPasswordChange(it) },
                        label = "Password (min 8 chars)",
                        placeholder = "Enter your password",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Navy800)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Confirm Password
                    SelavuTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { authViewModel.onConfirmPasswordChange(it) },
                        label = "Confirm Password",
                        placeholder = "Re-enter your password",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Navy800)
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Register Button
                    SelavuPrimaryButton(
                        text = "Register",
                        onClick = {
                            authViewModel.register(
                                onSuccess = {
                                    onNavigateToLogin()
                                }
                            )
                        },
                        isLoading = uiState.isLoading,
                        containerColor = Navy800
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Already have an account row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

