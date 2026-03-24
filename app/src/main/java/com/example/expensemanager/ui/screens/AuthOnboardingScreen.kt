package com.example.expensemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.example.expensemanager.viewmodel.LoginViewModel
import com.example.expensemanager.viewmodel.RegisterViewModel
import com.example.expensemanager.ui.state.LoginUiState
import com.example.expensemanager.ui.state.RegisterUiState

private val AppSurface = Color(0xFFF7F9FC)
private val PrimaryBlue = Color(0xFF001944)
private val SecondaryGreen = Color(0xFF1B6D24)
private val SecondaryContainer = Color(0xFFA0F399)
private val PrimaryContainer = Color(0xFF002C6E)
private val OnPrimaryContainer = Color(0xFF6B95F3)
private val SurfaceContainerHigh = Color(0xFFE6E8EB)
private val SurfaceContainerLow = Color(0xFFF2F4F7)
private val Outline = Color(0xFF767683)
private val OnSurfaceVariant = Color(0xFF454652)
private val InputTextColor = Color.Black

@Composable
fun AuthOnboardingScreen(
    loginViewModel: LoginViewModel = viewModel(),
    registerViewModel: RegisterViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val loginUiState by loginViewModel.uiState.collectAsState()
    val registerUiState by registerViewModel.uiState.collectAsState()

    LaunchedEffect(loginUiState.isLoginSuccessful, registerUiState.isRegistrationSuccessful) {
        if (loginUiState.isLoginSuccessful || registerUiState.isRegistrationSuccessful) {
            onAuthSuccess()
        }
    }

    Scaffold(
        containerColor = AppSurface,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppSurface)
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = this.maxWidth > 840.dp

                if (isWide) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        BrandingSection(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                        AuthSection(
                            selectedTab = selectedTab,
                            onTabChange = { selectedTab = it },
                            loginViewModel = loginViewModel,
                            registerViewModel = registerViewModel,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        BrandingSection(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        AuthSection(
                            selectedTab = selectedTab,
                            onTabChange = { selectedTab = it },
                            loginViewModel = loginViewModel,
                            registerViewModel = registerViewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            SecurityBadge(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun BrandingSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        PrimaryBlue,
                        Color(0xFF062A70),
                        PrimaryBlue
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "BudgetBee",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Take control of your finances.",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Designed for clarity. Built for growth. Track income, expenses, and savings with confidence.",
                color = OnPrimaryContainer,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.widthIn(max = 420.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StatCard(
                    title = "Net Growth",
                    value = "+24.8%",
                    titleColor = SecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Portfolio Value",
                    value = "$142,500",
                    titleColor = OnPrimaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 22.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun AuthSection(
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    modifier: Modifier = Modifier
) {
    val loginUiState by loginViewModel.uiState.collectAsState()
    val registerUiState by registerViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .background(AppSurface)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppSurface,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabChange(0) },
                    text = {
                        Text(
                            text = "Register",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) PrimaryBlue else Outline
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabChange(1) },
                    text = {
                        Text(
                            text = "Login",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) PrimaryBlue else Outline
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            if (selectedTab == 0) {
                RegisterView(
                    viewModel = registerViewModel,
                    uiState = registerUiState
                )
            } else {
                LoginView(
                    viewModel = loginViewModel,
                    uiState = loginUiState
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val showOtp = if (selectedTab == 0) registerUiState.isOtpSent else loginUiState.isOtpSent
            
            if (showOtp) {
                if (selectedTab == 0) {
                    OtpVerificationCard(
                        otp = registerUiState.otp,
                        onOtpChange = registerViewModel::onOtpChange,
                        onVerifyClick = registerViewModel::onVerifyOtpClick,
                        isLoading = registerUiState.isLoading,
                        errorMessage = registerUiState.errorMessage
                    )
                } else {
                    OtpVerificationCard(
                        otp = loginUiState.otp,
                        onOtpChange = loginViewModel::onOtpChange,
                        onVerifyClick = loginViewModel::onVerifyOtpClick,
                        isLoading = loginUiState.isLoading,
                        errorMessage = loginUiState.errorMessage
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (selectedTab == 0) {
                    "Already have an account? Log in here"
                } else {
                    "New here? Create your account"
                },
                color = OnSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RegisterView(
    viewModel: RegisterViewModel,
    uiState: RegisterUiState
) {
    var agreed by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Create Account",
            color = PrimaryBlue,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Start managing your money smarter with BudgetBee.",
            color = OnSurfaceVariant,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Full Name",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        FinanceInputField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "John Doe",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Outline
                )
            },
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Mobile Number",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        FinanceInputField(
            value = uiState.mobileNumber,
            onValueChange = viewModel::onMobileNumberChange,
            placeholder = "+91 98765 43210",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = Outline
                )
            },
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Secure Password",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        FinanceInputField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "••••••••",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Outline
                )
            },
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreed,
                onCheckedChange = { agreed = it }
            )
            Text(
                text = "I agree to the Terms and Privacy Policy.",
                color = OnSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = viewModel::onRegisterClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Register Now",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DividerLabel("Security Verified")
    }
}

@Composable
private fun LoginView(
    viewModel: LoginViewModel,
    uiState: LoginUiState
) {
    Column {
        Text(
            text = "Welcome Back",
            color = PrimaryBlue,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Log in to continue with BudgetBee.",
            color = OnSurfaceVariant,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mobile Number",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        FinanceInputField(
            value = uiState.mobileNumber,
            onValueChange = viewModel::onMobileNumberChange,
            placeholder = "+91 98765 43210",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = Outline
                )
            },
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Password",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        FinanceInputField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "••••••••",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Outline
                )
            },
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot Password?",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = viewModel::onLoginClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Log In",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DividerLabel("Security Verified")
    }
}

@Composable
private fun FinanceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = leadingIcon,
        placeholder = {
            Text(
                text = placeholder,
                color = Outline,
                fontWeight = FontWeight.SemiBold
            )
        },
        singleLine = true,
        textStyle = TextStyle(
            color = InputTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = InputTextColor,
            unfocusedTextColor = InputTextColor,
            cursorColor = PrimaryBlue,
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Outline,
            focusedLeadingIconColor = PrimaryBlue,
            unfocusedLeadingIconColor = Outline
        )
    )
}

@Composable
private fun OtpVerificationCard(
    otp: String,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SecondaryContainer.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = SecondaryGreen,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Verify Identity",
                color = PrimaryBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We've sent a 4-digit code to your device.",
                color = OnSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = onOtpChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = InputTextColor
                ),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = InputTextColor,
                    unfocusedTextColor = InputTextColor,
                    cursorColor = PrimaryBlue,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = onVerifyClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Verify & Continue", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { }) {
                Text(
                    text = "Resend Code",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DividerLabel(text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceContainerHigh)
        )
        Text(
            text = text,
            color = Outline,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun SecurityBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.85f),
        shape = RoundedCornerShape(999.dp),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = SecondaryGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AES-256 Encryption Active",
                color = PrimaryBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
    }
}