package com.example.expensemanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.data.network.*
import kotlinx.coroutines.launch

data class InsightsUiState(
    val distribution: List<CategoryDistribution> = emptyList(),
    val trends: List<SpendingTrend> = emptyList(),
    val suggestions: List<SavingSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class InsightsViewModel : ViewModel() {
    var uiState by mutableStateOf(InsightsUiState())
        private set

    fun fetchInsights() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val insightsResponse = RetrofitClient.instance.getInsights()
                val suggestionsResponse = RetrofitClient.instance.getSuggestions()

                if (insightsResponse.isSuccessful && suggestionsResponse.isSuccessful) {
                    uiState = uiState.copy(
                        distribution = insightsResponse.body()?.distribution ?: emptyList(),
                        trends = insightsResponse.body()?.trends ?: emptyList(),
                        suggestions = suggestionsResponse.body()?.suggestions ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Failed to load insights"
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
}
