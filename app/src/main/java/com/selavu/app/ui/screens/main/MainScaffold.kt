package com.selavu.app.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.selavu.app.ui.components.SelavuTopBar
import com.selavu.app.ui.navigation.BottomNavItem
import com.selavu.app.ui.screens.dashboard.DashboardScreen
import com.selavu.app.ui.screens.history.ExpenseHistoryScreen
import com.selavu.app.ui.screens.profile.ProfileScreen
import com.selavu.app.ui.theme.EmeraldGreen
import com.selavu.app.ui.theme.Navy800
import com.selavu.app.ui.theme.Navy50
import com.selavu.app.ui.theme.TextOnNavy
import com.selavu.app.ui.theme.TextSecondary
import com.selavu.app.viewmodel.AuthViewModel
import com.selavu.app.viewmodel.ExpenseViewModel

@Composable
fun MainScaffold(
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel,
    userId: String,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Home.route) }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            expenseViewModel.setCurrentUserId(userId)
        }
    }

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Expenses,
        BottomNavItem.Profile
    )

    val currentTitle = when (selectedTab) {
        BottomNavItem.Home.route -> "Selavu Dashboard"
        BottomNavItem.Expenses.route -> "Expense History"
        BottomNavItem.Profile.route -> "My Profile"
        else -> "Selavu"
    }

    Scaffold(
        topBar = {
            SelavuTopBar(title = currentTitle)
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = selectedTab == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = item.route },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy800,
                            selectedTextColor = Navy800,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Navy50
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != BottomNavItem.Profile.route) {
                FloatingActionButton(
                    onClick = onNavigateToAddExpense,
                    containerColor = EmeraldGreen,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                BottomNavItem.Home.route -> DashboardScreen(
                    authViewModel = authViewModel,
                    expenseViewModel = expenseViewModel,
                    onNavigateToAddExpense = onNavigateToAddExpense,
                    onNavigateToEditExpense = onNavigateToEditExpense,
                    onNavigateToAllExpenses = { selectedTab = BottomNavItem.Expenses.route }
                )
                BottomNavItem.Expenses.route -> ExpenseHistoryScreen(
                    expenseViewModel = expenseViewModel,
                    onNavigateToAddExpense = onNavigateToAddExpense,
                    onNavigateToEditExpense = onNavigateToEditExpense
                )
                BottomNavItem.Profile.route -> ProfileScreen(
                    authViewModel = authViewModel,
                    expenseViewModel = expenseViewModel,
                    onLogout = onLogout
                )
            }
        }
    }
}

