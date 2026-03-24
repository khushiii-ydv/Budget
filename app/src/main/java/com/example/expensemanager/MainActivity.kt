package com.example.expensemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensemanager.ui.screens.*
import com.example.expensemanager.ui.theme.ExpenseManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseManagerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
    AuthOnboardingScreen(
        onAuthSuccess = {
            navController.navigate("home") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    )
}
        composable("home") {
            val viewModel: com.example.expensemanager.viewmodel.HomeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            LaunchedEffect(uiState.isLoggedOut) {
                if (uiState.isLoggedOut) {
                    navController.navigate("onboarding") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }

            HomeScreen(
                viewModel = viewModel,
                onAddTransactionClick = { navController.navigate("add_transaction") },
                onBudgetClick = { navController.navigate("budget") },
                onInsightsClick = { navController.navigate("insights") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("add_transaction") {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("budget") {
            BudgetScreen(
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onAddTransactionClick = { navController.navigate("add_transaction") },
                onAdjustBudgetClick = { navController.navigate("adjust_budget") },
                onInsightsClick = { navController.navigate("insights") },
                onSettingsClick = { navController.navigate("settings") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("insights") {
            InsightsScreen(
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onAddClick = { navController.navigate("add_transaction") },
                onBudgetClick = { navController.navigate("budget") },
                onSettingsClick = { navController.navigate("settings") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onAddClick = { navController.navigate("add_transaction") },
                onBudgetClick = { navController.navigate("budget") },
                onInsightsClick = { navController.navigate("insights") },
                onLogoutClick = {
                    navController.navigate("onboarding") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("adjust_budget") {
            AdjustBudgetScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
