package com.example.expensemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensemanager.data.model.Transaction
import com.example.expensemanager.viewmodel.HomeViewModel
import com.example.expensemanager.ui.theme.*
import com.example.expensemanager.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTransactionClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: com.example.expensemanager.viewmodel.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DashboardSurface,
                modifier = Modifier.width(320.dp)
            ) {
                NavigationDrawerContent(
                    userName = uiState.userName,
                    onBudgetClick = {
                        scope.launch { drawerState.close() }
                        onBudgetClick()
                    },
                    onLogoutClick = { viewModel.logout() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "BudgetBee",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DashboardPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DashboardPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DashboardPrimary)
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = DashboardPrimary)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("User: ${uiState.userName}") },
                                    onClick = { showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = { 
                                        showMenu = false
                                        viewModel.logout()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DashboardSurface.copy(alpha = 0.7f)
                    )
                )
            },
            bottomBar = {
                DashboardBottomBar(
                onHomeClick = { /* Already here */ },
                onBudgetClick = onBudgetClick,
                onAddClick = onAddTransactionClick,
                onInsightsClick = onInsightsClick,
                onSettingsClick = onSettingsClick,
                activeTab = "home"
            )
            },
            containerColor = DashboardSurface
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hero Section
                item {
                    BalanceHeroSection(
                        totalBalance = uiState.summary.totalBalance,
                        onAddIncome = onAddTransactionClick,
                        onAddExpense = onAddTransactionClick
                    )
                }

                // Stats Grid
                item {
                    StatsGrid(
                        income = uiState.summary.totalIncome,
                        expenses = uiState.summary.totalExpenses,
                        savings = uiState.summary.totalBalance
                    )
                }

                // Monthly Spending Chart
                item {
                    MonthlySpendingChart()
                }

                // Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardPrimary
                        )
                        TextButton(onClick = { /* View All */ }) {
                            Text(
                                text = "VIEW ALL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B95F3),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Transactions List
                if (uiState.summary.recentTransactions.isEmpty()) {
                    item { EmptyTransactionsView() }
                } else {
                    items(uiState.summary.recentTransactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "BudgetBee",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DashboardPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DashboardPrimary)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DashboardPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DashboardSurface.copy(alpha = 0.7f)
        )
    )
}

@Composable
fun BalanceHeroSection(
    totalBalance: Double,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DashboardPrimary, DashboardPrimaryContainer)
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TOTAL BALANCE",
                    color = Color(0xFF6B95F3),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${String.format("%,.2f", totalBalance)}",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onAddIncome,
                        colors = ButtonDefaults.buttonColors(containerColor = DashboardSecondaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF217128))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Income", color = Color(0xFF217128), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAddExpense,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Expense", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatsGrid(income: Double, expenses: Double, savings: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Income",
            value = "₹${income.toInt()}",
            iconColor = DashboardSecondary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Expenses",
            value = "₹${expenses.toInt()}",
            iconColor = DashboardError
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(8.dp).background(iconColor, CircleShape))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, color = DashboardOnSurfaceVariant, fontSize = 12.sp)
            Text(text = value, color = DashboardPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MonthlySpendingChart() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Monthly Spending", color = DashboardPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val heights = listOf(0.4f, 0.7f, 0.5f, 0.85f, 0.6f, 0.95f)
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight(heights[index])
                            .background(DashboardPrimary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(
                color = when (transaction.category) {
                    "Food & Dining" -> Color(0xFFFFEAEA)
                    "Electronics" -> Color(0xFFE8EFFF)
                    "Income" -> Color(0xFFE8FFE8)
                    "Transport" -> Color(0xFFF2F2F2)
                    else -> Color(0xFFF2F4F7)
                }, 
                shape = RoundedCornerShape(12.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (transaction.category) {
                    "Food & Dining" -> Icons.Default.Restaurant
                    "Electronics" -> Icons.Default.ShoppingBag
                    "Income" -> Icons.Default.Work
                    "Transport" -> Icons.Default.DirectionsCar
                    else -> Icons.Default.Category
                },
                contentDescription = null, 
                modifier = Modifier.size(24.dp), 
                tint = when (transaction.category) {
                    "Food & Dining" -> Color(0xFFB33A3A)
                    "Electronics" -> Color(0xFF3A5BB3)
                    "Income" -> Color(0xFF3AB33A)
                    "Transport" -> Color(0xFF666666)
                    else -> DashboardPrimary
                }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DashboardOnSurface)
            Text(text = transaction.category, fontSize = 12.sp, color = DashboardOnSurfaceVariant)
        }
        Text(
            text = if (transaction.type == "Income") "+₹${transaction.amount}" else "-₹${transaction.amount}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == "Income") DashboardSecondary else DashboardError
        )
    }
}

@Composable
fun EmptyTransactionsView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "No transactions yet", color = Color.Gray)
    }
}

// Utility extension for size
fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)
