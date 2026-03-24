package com.example.expensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.ui.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, errorMessage = null) }
    }

    fun onMobileNumberChange(newMobile: String) {
        _uiState.update { it.copy(mobileNumber = newMobile, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onOtpChange(newOtp: String) {
        if (newOtp.length <= 4) {
            _uiState.update { it.copy(otp = newOtp, errorMessage = null) }
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRegisterClick() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || currentState.mobileNumber.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val response = com.example.expensemanager.data.network.RetrofitClient.instance.register(
                    com.example.expensemanager.data.model.RegisterRequest(
                        name = currentState.name,
                        mobileNumber = currentState.mobileNumber,
                        password = currentState.password
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isOtpSent = true,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.body()?.message ?: "Registration failed"
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

    fun onVerifyOtpClick() {
        val currentState = _uiState.value
        if (currentState.otp.length < 4) {
            _uiState.update { it.copy(errorMessage = "Please enter 4-digit OTP") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val response = com.example.expensemanager.data.network.RetrofitClient.instance.verifyOtp(
                    com.example.expensemanager.data.model.VerifyOtpRequest(
                        mobileNumber = currentState.mobileNumber,
                        otp = currentState.otp
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token
                    if (token != null) {
                        com.example.expensemanager.data.network.RetrofitClient.setToken(token)
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.body()?.message ?: "Invalid OTP"
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
}
