package com.selavu.app.ui.screens.addedit

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selavu.app.model.ExpenseCategory
import com.selavu.app.model.PaymentMethod
import com.selavu.app.ui.components.CategoryChip
import com.selavu.app.ui.components.ConfirmDeleteDialog
import com.selavu.app.ui.components.SelavuPrimaryButton
import com.selavu.app.ui.components.SelavuTextField
import com.selavu.app.ui.components.SelavuTopBar
import com.selavu.app.ui.theme.BackgroundLight
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.ErrorRed
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.Navy50
import com.selavu.app.ui.theme.TextPrimary
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEditExpenseScreen(
    expenseViewModel: ExpenseViewModel,
    userId: String,
    expenseId: String? = null,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val formState by expenseViewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val isEditing = !expenseId.isNullOrBlank()

    LaunchedEffect(expenseId) {
        if (isEditing && expenseId != null) {
            expenseViewModel.loadExpenseForEdit(expenseId, userId)
        } else {
            expenseViewModel.resetForm()
        }
    }

    LaunchedEffect(formState.errorMessage) {
        formState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(formState.date))

    // Date Picker Dialog setup
    val calendar = Calendar.getInstance().apply { timeInMillis = formState.date }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            expenseViewModel.onDateChange(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    if (showDeleteConfirmation && isEditing && expenseId != null) {
        ConfirmDeleteDialog(
            title = "Delete Expense?",
            message = "Are you sure you want to delete '${formState.title}'? This action cannot be undone.",
            onConfirm = {
                showDeleteConfirmation = false
                expenseViewModel.deleteExpenseById(expenseId, userId) {
                    onBackClick()
                }
            },
            onDismiss = { showDeleteConfirmation = false },
        )
    }

    Scaffold(
        topBar = {
            SelavuTopBar(
                title = if (isEditing) "Edit Expense" else "Add Expense",
                onBackClick = onBackClick,
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Expense",
                                tint = ErrorRed
                            )
                        }
                    }
                }
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Amount Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SelavuTextField(
                        value = formState.amount,
                        onValueChange = { expenseViewModel.onAmountChange(it) },
                        label = "Amount (₹)",
                        placeholder = "0.00",
                        leadingIcon = {
                            Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = EmeraldGreen)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        )
                    )
                }
            }

            // Details Card (Title, Category, Date, Payment Method, Notes)
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
                    // Expense Title
                    SelavuTextField(
                        value = formState.title,
                        onValueChange = { expenseViewModel.onTitleChange(it) },
                        label = "Expense Title *",
                        placeholder = "e.g., Grocery Shopping, Metro Ticket",
                        leadingIcon = {
                            Icon(Icons.Default.Title, contentDescription = null, tint = Navy800)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Category Selection
                    Column {
                        Text(
                            text = "Category *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display Category selection chips in grid/flow
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExpenseCategory.entries.chunked(2).forEach { rowCategories ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowCategories.forEach { cat ->
                                        CategoryChip(
                                            category = cat,
                                            isSelected = formState.category == cat,
                                            onClick = { expenseViewModel.onCategoryChange(cat) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Date Picker Input
                    Column {
                        Text(
                            text = "Date *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Navy50)
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select Date",
                                        tint = Navy800,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    )
                                }
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Payment Method Selection
                    Column {
                        Text(
                            text = "Payment Method *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PaymentMethod.entries.forEach { method ->
                                val isSelected = formState.paymentMethod == method
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Navy800 else Navy50)
                                        .clickable { expenseViewModel.onPaymentMethodChange(method) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = method.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Notes (Optional)
                    SelavuTextField(
                        value = formState.notes,
                        onValueChange = { expenseViewModel.onNotesChange(it) },
                        label = "Notes (Optional)",
                        placeholder = "Add remarks or details...",
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Navy800)
                        },
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            // Save Button
            SelavuPrimaryButton(
                text = if (isEditing) "Update Expense" else "Save Expense",
                onClick = {
                    expenseViewModel.saveExpense(userId) {
                        onBackClick()
                    }
                },
                isLoading = formState.isSaving,
                containerColor = Navy800
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

