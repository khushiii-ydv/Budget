package com.example.expensemanager.ui.state

data class RegisterUiState(
    val name: String = "Test User",
    val mobileNumber: String = "1234567890",
    val password: String = "password",
    val otp: String = "1234",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val isPasswordVisible: Boolean = false
)
