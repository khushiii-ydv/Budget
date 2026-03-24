package com.example.expensemanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.data.network.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SettingsViewModel : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    fun fetchProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val response = RetrofitClient.instance.getProfile()
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        userProfile = response.body()?.user,
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile"
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

    fun updateProfile(monthlyIncome: Double, currency: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.updateProfile(mapOf(
                    "monthly_income" to monthlyIncome,
                    "currency" to currency
                ))
                if (response.isSuccessful) {
                    fetchProfile() // Refresh
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
