package com.example.expensemanager.data.network

import com.example.expensemanager.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<OtpResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<LoginResponse>

    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(): Response<DashboardSummaryResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("api/auth/profile/update")
    suspend fun updateProfile(@Body updateData: Map<String, Any>): Response<Void>

    @POST("api/transactions")
    suspend fun addTransaction(@Body request: TransactionRequest): Response<TransactionResponse>

    @GET("api/budgets/summary")
    suspend fun getBudgetSummary(): Response<BudgetSummaryResponse>

    @GET("api/budgets/goals")
    suspend fun getGoals(): Response<GoalsResponse>

    @POST("api/budgets/update")
    suspend fun updateBudget(@Body updateData: Map<String, Any>): Response<Void>

    @POST("api/budgets/goals/{id}/add-fund")
    suspend fun addFundToGoal(@Path("id") goalId: Int, @Body fundData: Map<String, Double>): Response<Void>

    @GET("api/insights")
    suspend fun getInsights(): Response<InsightsResponse>

    @GET("api/insights/suggestions")
    suspend fun getSuggestions(): Response<SuggestionsResponse>
}

data class InsightsResponse(
    val success: Boolean,
    val distribution: List<CategoryDistribution>,
    val trends: List<SpendingTrend>
)

data class CategoryDistribution(
    val category: String,
    val total: Double
)

data class SpendingTrend(
    val day: String,
    val total: Double
)

data class SuggestionsResponse(
    val success: Boolean,
    val suggestions: List<SavingSuggestion>
)

data class ProfileResponse(
    val success: Boolean,
    val user: UserProfile
)

data class UserProfile(
    val id: Int,
    val name: String,
    val mobile_number: String,
    val monthly_income: Double,
    val currency: String,
    val created_at: String
)

data class SavingSuggestion(
    val title: String,
    val description: String,
    val icon: String
)

data class DashboardSummaryResponse(
    val success: Boolean,
    val summary: DashboardSummary,
    val userName: String? = null
)

data class TransactionRequest(
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val date: String
)

data class TransactionResponse(
    val success: Boolean,
    val message: String,
    val transaction: Transaction
)
