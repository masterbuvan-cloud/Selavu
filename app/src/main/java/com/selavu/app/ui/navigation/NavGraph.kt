package com.selavu.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.selavu.app.ui.screens.addedit.AddEditExpenseScreen
import com.selavu.app.ui.screens.auth.LoginScreen
import com.selavu.app.ui.screens.auth.RegisterScreen
import com.selavu.app.ui.screens.main.MainScaffold
import com.selavu.app.ui.screens.welcome.WelcomeScreen
import com.selavu.app.viewmodel.AuthViewModel
import com.selavu.app.viewmodel.ExpenseViewModel

@Composable
fun SelavuNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val startDestination = if (authState.isLoggedIn) Screen.Main.route else Screen.Welcome.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Welcome Screen
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userId ->
                    expenseViewModel.setCurrentUserId(userId)
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Main Dashboard Scaffold (Home, Expenses, Profile)
        composable(Screen.Main.route) {
            val currentUserId = authState.currentUserId
            MainScaffold(
                authViewModel = authViewModel,
                expenseViewModel = expenseViewModel,
                userId = currentUserId,
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToEditExpense = { expenseId ->
                    navController.navigate(Screen.EditExpense.createRoute(expenseId))
                },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Add Expense Screen
        composable(Screen.AddExpense.route) {
            val currentUserId = authState.currentUserId
            AddEditExpenseScreen(
                expenseViewModel = expenseViewModel,
                userId = currentUserId,
                expenseId = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Expense Screen
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
            val currentUserId = authState.currentUserId
            AddEditExpenseScreen(
                expenseViewModel = expenseViewModel,
                userId = currentUserId,
                expenseId = expenseId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

