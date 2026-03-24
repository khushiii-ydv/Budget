package com.example.expensemanager.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensemanager.ui.components.DashboardBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: com.example.expensemanager.viewmodel.InsightsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchInsights()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Insights", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001944)) },
                navigationIcon = {
                    IconButton(onClick = { /* Open Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF001944))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF001944))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFf7f9fc))
            )
        },
        containerColor = Color(0xFFf7f9fc),
        bottomBar = {
            DashboardBottomBar(
                onHomeClick = onHomeClick,
                onBudgetClick = onBudgetClick,
                onAddClick = onAddClick,
                onInsightsClick = { /* Already here */ },
                onSettingsClick = onSettingsClick,
                activeTab = "insights"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Time Period Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFeceef1), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Weekly", "Monthly", "Yearly").forEachIndexed { index, period ->
                        val isSelected = index == 0
                        Surface(
                            modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(8.dp)),
                            color = if (isSelected) Color.White else Color.Transparent,
                            onClick = { },
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    period,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color(0xFF001944) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Category Distribution Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Category Distribution", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF001944))
                                Text("Where your money goes", fontSize = 12.sp, color = Color.Gray)
                            }
                            Surface(color = Color(0xFFa0f399), shape = CircleShape) {
                                Text("-12% vs last week", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF217128))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Donut Chart
                            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    var currentAngle = 0f
                                    val totalSpent = uiState.distribution.sumOf { it.total }
                                    if (totalSpent > 0) {
                                        uiState.distribution.forEachIndexed { index, dist ->
                                            val sweep = (dist.total / totalSpent * 360f).toFloat()
                                            drawArc(
                                                color = when(index % 4) {
                                                    0 -> Color(0xFF002c6e)
                                                    1 -> Color(0xFF1b6d24)
                                                    2 -> Color(0xFFba1a1a)
                                                    else -> Color(0xFF6b95f3)
                                                },
                                                startAngle = currentAngle,
                                                sweepAngle = sweep - 2f, // Small gap
                                                useCenter = false,
                                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                            currentAngle += sweep
                                        }
                                    } else {
                                        drawArc(color = Color.LightGray, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 16.dp.toPx()))
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val total = uiState.distribution.sumOf { it.total }
                                    Text("₹${String.format("%,.0f", total)}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF001944))
                                    Text("SPENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            // Legend
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                uiState.distribution.take(4).forEachIndexed { index, dist ->
                                    LegendItem(
                                        when(index % 4) {
                                            0 -> Color(0xFF002c6e)
                                            1 -> Color(0xFF1b6d24)
                                            2 -> Color(0xFFba1a1a)
                                            else -> Color(0xFF6b95f3)
                                        },
                                        dist.category
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top Category Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001944))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Column {
                            Text("TOP CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f), letterSpacing = 1.sp)
                            Text("Dining Out", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your spending here increased by 15% this week. Consider home cooking.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        }
                        Text(
                            "₹842.00",
                            modifier = Modifier.align(Alignment.BottomStart),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Spending Trends
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Spending Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF001944))
                                Text("Daily transaction volume", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = {}, modifier = Modifier.background(Color(0xFFd9e2ff), RoundedCornerShape(12.dp))) {
                                Icon(Icons.Default.IosShare, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF001944))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (uiState.trends.isEmpty()) {
                                Text("No trend data", modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
                            } else {
                                val maxTrend = uiState.trends.maxOfOrNull { it.total } ?: 1.0
                                uiState.trends.forEachIndexed { i, trend ->
                                    val heightFactor = (trend.total / maxTrend).toFloat().coerceIn(0.1f, 1f)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(heightFactor)
                                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                .background(if (i == uiState.trends.size - 1) Color(0xFF001944) else Color(0xFFeceef1))
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(trend.day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (i == uiState.trends.size - 1) Color(0xFF001944) else Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Suggestions
            item {
                Text("Wealth Architect Suggestions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF001944))
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (uiState.suggestions.isEmpty()) {
                        Text("All good! No specific suggestions right now.", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        uiState.suggestions.forEach { suggestion ->
                            SuggestionCard(
                                title = suggestion.title,
                                description = suggestion.description,
                                icon = when(suggestion.icon) {
                                    "restaurant" -> Icons.Default.Restaurant
                                    "subscriptions" -> Icons.Default.Subscriptions
                                    else -> Icons.Default.Lightbulb
                                },
                                containerColor = if (suggestion.icon == "restaurant") Color(0xFFa0f399) else Color(0xFFffdad8),
                                contentColor = if (suggestion.icon == "restaurant") Color(0xFF1b6d24) else Color(0xFFba1a1a)
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF001944))
    }
}

@Composable
fun SuggestionCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, containerColor: Color, contentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = contentColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = contentColor)
                Text(description, fontSize = 12.sp, color = contentColor, lineHeight = 16.sp)
            }
        }
    }
}
