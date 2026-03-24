package com.example.expensemanager.ui.state

data class LoginUiState(
    val mobileNumber: String = "1234567890",
    val password: String = "password",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val otp: String = "1234",
    val isOtpSent: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isPasswordVisible: Boolean = false
)
