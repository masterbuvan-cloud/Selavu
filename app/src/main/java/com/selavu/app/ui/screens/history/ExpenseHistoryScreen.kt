package com.selavu.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selavu.app.model.DateFilterOption
import com.selavu.app.model.ExpenseCategory
import com.selavu.app.model.SortOption
import com.selavu.app.ui.components.CategoryChip
import com.selavu.app.ui.components.EmptyStateView
import com.selavu.app.ui.components.ExpenseItemCard
import com.selavu.app.ui.theme.BackgroundLight
import com.selavu.app.ui.theme.DividerColor
import com.selavu.app.ui.theme.EmeraldDark
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.Navy50
import com.selavu.app.ui.theme.TextPrimary
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseHistoryScreen(
    expenseViewModel: ExpenseViewModel,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit
) {
    val searchQuery by expenseViewModel.searchQuery.collectAsState()
    val selectedCategory by expenseViewModel.selectedCategory.collectAsState()
    val selectedDateFilter by expenseViewModel.selectedDateFilter.collectAsState()
    val selectedSortOption by expenseViewModel.selectedSortOption.collectAsState()
    val filteredExpenses by expenseViewModel.filteredExpenses.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showDateFilterMenu by remember { mutableStateOf(false) }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
    }
    val filteredSum = filteredExpenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Search & Filters Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Search Input Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { expenseViewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by title, category, notes...", color = TextSecondary, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Navy800)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { expenseViewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Navy800,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sort & Date Filter Dropdowns Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date Filter Menu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Navy50)
                                .clickable { showDateFilterMenu = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Navy800,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedDateFilter.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy800
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = showDateFilterMenu,
                            onDismissRequest = { showDateFilterMenu = false }
                        ) {
                            DateFilterOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            fontWeight = if (option == selectedDateFilter) FontWeight.Bold else FontWeight.Normal,
                                            color = if (option == selectedDateFilter) EmeraldDark else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        expenseViewModel.onDateFilterChange(option)
                                        showDateFilterMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sort Order Menu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Navy50)
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null,
                                tint = Navy800,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedSortOption.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy800
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            fontWeight = if (option == selectedSortOption) FontWeight.Bold else FontWeight.Normal,
                                            color = if (option == selectedSortOption) EmeraldDark else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        expenseViewModel.onSortOptionChange(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips (Horizontal Scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "All" chip
                    val isAllSelected = selectedCategory == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isAllSelected) Navy800 else Color.White)
                            .clickable { expenseViewModel.onCategoryFilterChange(null) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) Color.White else TextPrimary
                            )
                        )
                    }

                    // Individual Categories
                    ExpenseCategory.entries.forEach { cat ->
                        CategoryChip(
                            category = cat,
                            isSelected = selectedCategory == cat,
                            onClick = { expenseViewModel.onCategoryFilterChange(cat) }
                        )
                    }
                }
            }
        }

        // Subheader showing total matching count and sum
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredExpenses.size} ${if (filteredExpenses.size == 1) "transaction" else "transactions"}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Medium)
            )

            if (filteredExpenses.isNotEmpty()) {
                Text(
                    text = "Total: ${try { currencyFormat.format(filteredSum) } catch (e: Exception) { "₹${"%.2f".format(filteredSum)}" }}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Navy800,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // List of Expenses
        if (filteredExpenses.isEmpty()) {
            EmptyStateView(
                title = if (searchQuery.isNotEmpty() || selectedCategory != null || selectedDateFilter != DateFilterOption.ALL)
                    "No Matching Expenses"
                else "No Expenses Yet",
                subtitle = if (searchQuery.isNotEmpty() || selectedCategory != null || selectedDateFilter != DateFilterOption.ALL)
                    "Try adjusting your filters or search keywords to find what you're looking for."
                else "Start adding your daily expenses to see them organized here.",
                actionButtonText = if (searchQuery.isNotEmpty() || selectedCategory != null) "Clear Filters" else "Add Expense",
                onActionClick = {
                    if (searchQuery.isNotEmpty() || selectedCategory != null || selectedDateFilter != DateFilterOption.ALL) {
                        expenseViewModel.onSearchQueryChange("")
                        expenseViewModel.onCategoryFilterChange(null)
                        expenseViewModel.onDateFilterChange(DateFilterOption.ALL)
                    } else {
                        onNavigateToAddExpense()
                    }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredExpenses, key = { it.id }) { expense ->
                    ExpenseItemCard(
                        expense = expense,
                        onClick = { onNavigateToEditExpense(expense.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

