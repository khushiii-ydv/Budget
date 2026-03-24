package com.example.expensemanager.data.model

data class BudgetSummary(
    val totalLimit: Double,
    val totalSpent: Double,
    val percentage: Double,
    val remaining: Double,
    val daysRemaining: Int,
    val dailyAvg: Double
)

data class CategoryBudget(
    val id: Int,
    val category: String,
    val limit: Double,
    val spent: Double,
    val percentage: Double
)

data class SavingsGoal(
    val id: Int,
    val name: String,
    val target: Double,
    val current: Double,
    val deadline: String,
    val icon: String,
    val percentage: Double
)

data class BudgetSummaryResponse(
    val success: Boolean,
    val summary: BudgetSummary,
    val categories: List<CategoryBudget>,
    val userName: String? = null
)

data class GoalsResponse(
    val success: Boolean,
    val goals: List<SavingsGoal>
)
