package com.selavu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.selavu.app.ui.navigation.SelavuNavGraph
import com.selavu.app.ui.theme.SelavuTheme
import com.selavu.app.viewmodel.AuthViewModel
import com.selavu.app.viewmodel.ExpenseViewModel
import com.selavu.app.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        val app = application as SelavuApplication
        ViewModelFactory(app.userRepository, app.expenseRepository)
    }

    private val expenseViewModel: ExpenseViewModel by viewModels {
        val app = application as SelavuApplication
        ViewModelFactory(app.userRepository, app.expenseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SelavuTheme {
                val navController = rememberNavController()
                SelavuNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    expenseViewModel = expenseViewModel
                )
            }
        }
    }
}

