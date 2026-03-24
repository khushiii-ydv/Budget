package com.example.expensemanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.data.model.*
import com.example.expensemanager.data.network.RetrofitClient
import kotlinx.coroutines.launch

data class BudgetUiState(
    val isLoading: Boolean = false,
    val summary: BudgetSummary? = null,
    val categories: List<CategoryBudget> = emptyList(),
    val goals: List<SavingsGoal> = emptyList(),
    val userName: String = "User",
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null
)

class BudgetViewModel : ViewModel() {
    var uiState by mutableStateOf(BudgetUiState())
        private set

    fun fetchData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val budgetResponse = RetrofitClient.instance.getBudgetSummary()
                val goalsResponse = RetrofitClient.instance.getGoals()

                if (budgetResponse.isSuccessful && goalsResponse.isSuccessful) {
                    val body = budgetResponse.body()
                    uiState = uiState.copy(
                        isLoading = false,
                        summary = body?.summary,
                        categories = body?.categories ?: emptyList(),
                        goals = goalsResponse.body()?.goals ?: emptyList(),
                        userName = body?.userName ?: "User"
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Failed to fetch budget data"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateBudget(category: String, limit: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.updateBudget(mapOf("category" to category, "limit" to limit))
                if (response.isSuccessful) {
                    fetchData() // Refresh
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun addFundToGoal(goalId: Int, amount: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.addFundToGoal(goalId, mapOf("amount" to amount))
                if (response.isSuccessful) {
                    fetchData() // Refresh
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun logout() {
        // In a real app, clear tokens here
        uiState = uiState.copy(isLoggedOut = true)
    }
}
