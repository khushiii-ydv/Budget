package com.example.expensemanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensemanager.data.model.BudgetSummary
import com.example.expensemanager.data.model.CategoryBudget
import com.example.expensemanager.data.model.SavingsGoal
import com.example.expensemanager.ui.components.DashboardBottomBar
import com.example.expensemanager.ui.components.NavigationDrawerContent
import com.example.expensemanager.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onHomeClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onAdjustBudgetClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: BudgetViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var selectedGoalForFund by remember { mutableStateOf<SavingsGoal?>(null) }
    var fundAmount by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    if (selectedGoalForFund != null) {
        AlertDialog(
            onDismissRequest = { selectedGoalForFund = null },
            title = { Text("Add Funds to ${selectedGoalForFund?.name}") },
            text = {
                Column {
                    Text("How much would you like to add?")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fundAmount,
                        onValueChange = { fundAmount = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = fundAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.addFundToGoal(selectedGoalForFund!!.id, amount) { success ->
                                if (success) {
                                    Toast.makeText(context, "Funds added successfully!", Toast.LENGTH_SHORT).show()
                                    selectedGoalForFund = null
                                    fundAmount = ""
                                } else {
                                    Toast.makeText(context, "Failed to add funds", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001944))
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedGoalForFund = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onBackClick()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFf7f9fc),
                modifier = Modifier.width(320.dp)
            ) {
                NavigationDrawerContent(
                    userName = uiState.userName,
                    onHomeClick = {
                        scope.launch { drawerState.close() }
                        onHomeClick()
                    },
                    onBudgetClick = {
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = { viewModel.logout() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Wealth Architect", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001944)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF001944))
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            Toast.makeText(context, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF001944))
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF001944))
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
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFf7f9fc))
                )
            },
            bottomBar = {
                DashboardBottomBar(
                onHomeClick = onHomeClick,
                onBudgetClick = { /* Already here */ },
                onAddClick = onAddTransactionClick,
                onInsightsClick = onInsightsClick,
                onSettingsClick = onSettingsClick,
                activeTab = "budget"
            )
            },
            containerColor = Color(0xFFf7f9fc)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Heading
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                "MONTHLY OVERVIEW",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Wealth Architect",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001944)
                            )
                        }
                        Button(
                            onClick = onAdjustBudgetClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001944)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adjust Budget", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                // Hero Card
                uiState.summary?.let { summary ->
                    item {
                        MonthlyOverviewCard(summary)
                    }
                }

                // Categories
                item {
                    Text(
                        "Expense Categories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001944)
                    )
                }

                items(uiState.categories) { category ->
                    CategoryBudgetCard(category)
                }

                // Savings Goals
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Savings Goals",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001944)
                        )
                        TextButton(onClick = { }) {
                            Text("View All", color = Color(0xFF001944), fontWeight = FontWeight.Bold)
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF001944))
                        }
                    }
                }

                items(uiState.goals) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        onAddFund = { selectedGoalForFund = goal }
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun MonthlyOverviewCard(summary: BudgetSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Monthly Spending", color = Color.Gray, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "₹${String.format("%,.0f", summary.totalSpent)}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF001944)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "/ ₹${String.format("%,.0f", summary.totalLimit)}",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFf2f4f7))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(summary.percentage.toFloat() / 100f)
                        .fillMaxHeight()
                        .background(Color(0xFF001944))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildString {
                    append("You have ")
                    append("₹${String.format("%,.0f", summary.remaining)} ")
                    append("remaining for the next ${summary.daysRemaining} days.")
                },
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFf2f4f7), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("DAILY AVG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("₹${String.format("%.2f", summary.dailyAvg)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001944))
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFf2f4f7), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(
                            if (summary.percentage > 90) "At Risk" else "On Track",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (summary.percentage > 90) Color.Red else Color(0xFF1b6d24)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBudgetCard(category: CategoryBudget) {
    val isWarning = category.percentage >= 90
    val isOver = category.percentage >= 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isOver) Color(0xFFFFDAD6) else if (isWarning) Color(0xFFFFDAD8) else Color(0xFFD9E2FF),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = when (category.category) {
                            "Food & Dining" -> Icons.Default.Restaurant
                            "Travel" -> Icons.Default.Flight
                            "Rent" -> Icons.Default.MapsHomeWork
                            "Fun" -> Icons.Default.Movie
                            else -> Icons.Default.Category
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isOver) Color(0xFFBA1A1A) else if (isWarning) Color(0xFF80272B) else Color(0xFF001944)
                    )
                }
                Text(
                    "${category.percentage.toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) Color.Red else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                if (category.category == "Food & Dining") "Food" else category.category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF001944)
            )
            Text("₹${category.spent.toInt()} of ₹${category.limit.toInt()}", fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFeceef1))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(kotlin.math.min(1.0f, category.percentage.toFloat() / 100f))
                        .fillMaxHeight()
                        .background(if (isWarning) Color.Red else Color(0xFF001944))
                )
            }

            if (isWarning) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isOver) Icons.Default.PriorityHigh else Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isOver) "OVER BUDGET" else "NEAR LIMIT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsGoalCard(goal: SavingsGoal, onAddFund: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Circular Progress
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFF1b6d24).copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        color = Color(0xFF1b6d24),
                        startAngle = -90f,
                        sweepAngle = (goal.percentage.toFloat() / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Icon(
                    imageVector = when (goal.icon) {
                        "directions_car" -> Icons.Default.DirectionsCar
                        "beach_access" -> Icons.Default.BeachAccess
                        else -> Icons.Default.Savings
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF1b6d24)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(goal.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001944))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFe8f5e9), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("${goal.percentage.toInt()}% SAVED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2e7d32))
                    }
                }
                Text("Target: ₹${String.format("%,.0f", goal.target)} • ${goal.deadline}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("₹${String.format("%,.0f", goal.current)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001944))
                    Button(
                        onClick = onAddFund,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf0f4ff)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Add Funds", fontSize = 11.sp, color = Color(0xFF001944), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
