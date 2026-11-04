package com.selavu.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")
    data object AddExpense : Screen("add_expense")
    data object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: String): String = "edit_expense/$expenseId"
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object Home : BottomNavItem(
        route = "bottom_home",
        title = "Home",
        icon = Icons.Default.Home,
    )
    data object Expenses : BottomNavItem(
        route = "bottom_expenses",
        title = "Expenses",
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
    )
    data object Profile : BottomNavItem(
        route = "bottom_profile",
        title = "Profile",
        icon = Icons.Default.Person,
    )
}
