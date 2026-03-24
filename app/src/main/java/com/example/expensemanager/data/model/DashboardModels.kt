package com.example.expensemanager.data.model

data class DashboardSummary(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpenses: Double,
    val savings: Double,
    val recentTransactions: List<Transaction>
)

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String, // "Income" or "Expense"
    val date: String,
    val paymentMethod: String
)
