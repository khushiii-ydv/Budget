package com.example.expensemanager.ui.state

import com.example.expensemanager.data.model.DashboardSummary
import com.example.expensemanager.data.model.Transaction

data class HomeUiState(
    val summary: DashboardSummary = DashboardSummary(0.0, 0.0, 0.0, 0.0, emptyList()),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "Test User",
    val isLoggedOut: Boolean = false
)
