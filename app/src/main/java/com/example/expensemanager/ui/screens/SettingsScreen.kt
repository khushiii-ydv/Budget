package com.example.expensemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensemanager.ui.components.DashboardBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: com.example.expensemanager.viewmodel.SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState = viewModel.uiState
    var showIncomeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var tempIncome by remember { mutableStateOf("") }
    var isDarkTheme by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wealth Architect", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001944)) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF001944))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF001944))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFf7f9fc),
        bottomBar = {
            DashboardBottomBar(
                onHomeClick = onHomeClick,
                onBudgetClick = onBudgetClick,
                onAddClick = onAddClick,
                onInsightsClick = onInsightsClick,
                onSettingsClick = { /* Already here */ },
                activeTab = "settings"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(20.dp),
                                    tint = Color.Gray
                                )
                            }
                            
                            // Added another icon as requested
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .offset(x = (-68).dp, y = 0.dp)
                                    .background(Color(0xFFE6E8EB), CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF001944), modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF001944), CircleShape)
                                    .padding(4.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(uiState.userProfile?.name ?: "Khushi Yadav", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001944))
                        Text("Wealth Curation Mode", fontSize = 14.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        ContactItem(Icons.Default.Phone, uiState.userProfile?.mobile_number ?: "+91 9307136478")
                        Spacer(modifier = Modifier.height(8.dp))
                        ContactItem(Icons.Default.Email, "khushi@gmail.com")
                    }
                }
            }

            // Wealth Parameters
            item {
                Text("WEALTH PARAMETERS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ParameterCard(
                        label = "Monthly Income",
                        value = "₹${String.format("%,.2f", uiState.userProfile?.monthly_income ?: 0.0)}",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = {
                            tempIncome = (uiState.userProfile?.monthly_income ?: 0.0).toString()
                            showIncomeDialog = true
                        }
                    )
                    
                    ParameterCard(
                        label = "Currency Preference",
                        value = "${uiState.userProfile?.currency ?: "INR"} (₹)",
                        icon = Icons.Default.Sync,
                        isDropdown = true,
                        onClick = { showCurrencyDialog = true }
                    )
                }
            }

            // App Configuration
            item {
                Text("APP CONFIGURATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        ConfigListItem(
                            icon = Icons.Outlined.DarkMode,
                            title = "Interface Theme",
                            subtitle = "Switch between light and dark mode",
                            hasSwitch = true,
                            switchState = isDarkTheme,
                            onSwitchChange = { isDarkTheme = it }
                        )
                        ConfigListItem(
                            icon = Icons.Outlined.Security,
                            title = "Security Access",
                            subtitle = "Update your account password"
                        )
                        ConfigListItem(
                            icon = Icons.Outlined.CloudUpload,
                            title = "Vault Backup",
                            subtitle = "Synchronize data with secure cloud"
                        )
                        ConfigListItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Notifications",
                            subtitle = "Manage alerts and news updates"
                        )
                        ConfigListItem(
                            icon = Icons.Outlined.Language,
                            title = "Regional Language",
                            subtitle = "English (United States)",
                            isLast = true
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFDAD6))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout from Architect", fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Wealth Architect v4.2.0 • Secured by Financial Guardian Protocol", fontSize = 10.sp, color = Color.Gray)
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }

        // Dialogs
        if (showIncomeDialog) {
            AlertDialog(
                onDismissRequest = { showIncomeDialog = false },
                title = { Text("Update Monthly Income", color = Color(0xFF001944), fontWeight = FontWeight.Bold) },
                text = {
                    TextField(
                        value = tempIncome,
                        onValueChange = { tempIncome = it },
                        label = { Text("Income Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF001944),
                            focusedLabelColor = Color(0xFF001944)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val income = tempIncome.toDoubleOrNull() ?: 0.0
                            viewModel.updateProfile(income, uiState.userProfile?.currency ?: "INR") { success ->
                                if (success) showIncomeDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001944), contentColor = Color.White)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showIncomeDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (showCurrencyDialog) {
            val currencies = listOf("INR", "USD", "EUR", "GBP", "JPY")
            AlertDialog(
                onDismissRequest = { showCurrencyDialog = false },
                title = { Text("Select Currency", color = Color(0xFF001944), fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        currencies.forEach { currency ->
                            TextButton(
                                onClick = {
                                    viewModel.updateProfile(uiState.userProfile?.monthly_income ?: 0.0, currency) { success ->
                                        if (success) showCurrencyDialog = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(currency, color = Color(0xFF001944))
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showCurrencyDialog = false }) {
                        Text("Close", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun ParameterCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDropdown: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFE0E3E6), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = Color(0xFF001944))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001944))
                }
                if (isDropdown) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ConfigListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    hasSwitch: Boolean = false,
    switchState: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(48.dp).background(Color(0xFFF7F9FC), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF001944))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF001944), fontSize = 16.sp)
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
            }
            if (hasSwitch) {
                Switch(
                    checked = switchState, 
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF001944),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE6E8EB)
                    )
                )
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
        if (!isLast) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFECEEF1))
        }
    }
}
