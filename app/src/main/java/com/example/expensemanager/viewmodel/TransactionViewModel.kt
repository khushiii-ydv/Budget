package com.example.expensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.ui.state.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState(date = getCurrentDate()))
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(amount = newAmount, errorMessage = null) }
        }
    }

    fun onTypeChange(newType: String) {
        _uiState.update { it.copy(type = newType) }
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onSaveClick() {
        val currentState = _uiState.value
        if (currentState.title.isBlank() || currentState.amount.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter title and amount") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val response = com.`example`.expensemanager.data.network.RetrofitClient.instance.addTransaction(
                    com.example.expensemanager.data.network.TransactionRequest(
                        title = currentState.title,
                        amount = currentState.amount.toDouble(),
                        category = currentState.category,
                        type = currentState.type,
                        date = currentState.date
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.body()?.message ?: "Failed to save transaction"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { TransactionUiState(date = getCurrentDate()) }
    }
}
