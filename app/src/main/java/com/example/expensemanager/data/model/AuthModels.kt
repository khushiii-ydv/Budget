package com.example.expensemanager.data.model

data class LoginRequest(
    val mobileNumber: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: User? = null
)

data class RegisterRequest(
    val name: String,
    val mobileNumber: String,
    val password: String
)

data class VerifyOtpRequest(
    val mobileNumber: String,
    val otp: String
)

data class OtpResponse(
    val success: Boolean,
    val message: String
)

data class User(
    val id: Int,
    val name: String,
    val mobileNumber: String,
    val email: String? = null
)
