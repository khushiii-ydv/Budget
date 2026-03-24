package com.example.expensemanager.ui.state

import com.example.expensemanager.data.model.Transaction

data class TransactionUiState(
    val title: String = "",
    val amount: String = "",
    val type: String = "Expense", // "Income" or "Expense"
    val category: String = "Food",
    val date: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
