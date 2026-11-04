package com.selavu.app.ui.screens.dashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selavu.app.model.Expense
import com.selavu.app.ui.components.EmptyStateView
import com.selavu.app.ui.components.ExpenseItemCard
import com.selavu.app.ui.theme.BackgroundLight
import com.selavu.app.ui.theme.EmeraldDark
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.EmeraldLight
import com.selavu.app.ui.theme.Navy700
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.Navy900
import com.selavu.app.ui.theme.Navy50
import com.selavu.app.ui.theme.TextOnNavy
import com.selavu.app.ui.theme.TextPrimary
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.AuthViewModel
import com.selavu.app.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit,
    onNavigateToAllExpenses: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val todayTotal by expenseViewModel.todayTotal.collectAsState()
    val monthTotal by expenseViewModel.monthTotal.collectAsState()
    val totalTransactions by expenseViewModel.totalTransactions.collectAsState()
    val allExpenses by expenseViewModel.allExpenses.collectAsState()

    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    val currentDateFormatted = dateFormatter.format(Date())

    val userDisplayName = if (authState.currentUserEmail.isNotBlank()) {
        authState.currentUserEmail.substringBefore("@")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } else {
        "User"
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
    }

    val recentExpenses = allExpenses.take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Hero Header Card (Navy Blue with soft green accent)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Navy900, Navy800, Navy700)
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        // User Welcome & Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome back,",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextOnNavy.copy(alpha = 0.75f)
                                    )
                                )
                                Text(
                                    text = userDisplayName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnNavy,
                                        fontSize = 22.sp
                                    )
                                )
                            }

                            // Quick add pill button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                                    .clickable(onClick = onNavigateToAddExpense)
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Expense",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentDateFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextOnNavy.copy(alpha = 0.65f),
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Total Monthly Expense inside Hero Card
                        Text(
                            text = "This Month's Spending",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextOnNavy.copy(alpha = 0.8f),
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = try { currencyFormat.format(monthTotal) } catch (e: Exception) { "₹${"%.2f".format(monthTotal)}" },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 32.sp
                            )
                        )
                    }
                }
            }
        }

        // Summary Cards Section (Today & Total Transactions)
        item {
            Text(
                text = "Expense Summary",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Today's Expense Card
                SummaryMetricCard(
                    title = "Today's Expenses",
                    amount = try { currencyFormat.format(todayTotal) } catch (e: Exception) { "₹${"%.2f".format(todayTotal)}" },
                    icon = Icons.Default.Today,
                    iconBgColor = EmeraldLight,
                    iconTint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )

                // Total Transactions Card
                SummaryMetricCard(
                    title = "Transactions",
                    amount = "$totalTransactions items",
                    icon = Icons.Default.Receipt,
                    iconBgColor = Navy50,
                    iconTint = Navy800,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                if (allExpenses.isNotEmpty()) {
                    Row(
                        modifier = Modifier.clickable(onClick = onNavigateToAllExpenses),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = EmeraldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View All",
                            tint = EmeraldDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Recent Transactions List
        if (recentExpenses.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Expenses Logged",
                    subtitle = "Tap below to record your first expense for today.",
                    onActionClick = onNavigateToAddExpense
                )
            }
        } else {
            items(recentExpenses, key = { it.id }) { expense ->
                ExpenseItemCard(
                    expense = expense,
                    onClick = { onNavigateToEditExpense(expense.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    amount: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            )
        }
    }
}

