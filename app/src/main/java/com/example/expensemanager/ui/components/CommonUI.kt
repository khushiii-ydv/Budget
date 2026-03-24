package com.example.expensemanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensemanager.ui.theme.*

@Composable
fun DashboardBottomBar(
    onHomeClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onInsightsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    activeTab: String = "home"
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) { 
                Icon(
                    Icons.Default.Home, 
                    contentDescription = "Home", 
                    tint = if (activeTab == "home") Color(0xFF001944) else Color(0xFF8e949a)
                ) 
            }
            IconButton(onClick = onBudgetClick) { 
                Icon(
                    Icons.Default.AccountBalanceWallet, 
                    contentDescription = "Budget", 
                    tint = if (activeTab == "budget") Color(0xFF001944) else Color(0xFF8e949a)
                ) 
            }
            IconButton(onClick = onAddClick) { 
                Icon(
                    Icons.Default.AddCircle, 
                    contentDescription = "Add", 
                    modifier = Modifier.size(36.dp), 
                    tint = Color(0xFF001944)
                ) 
            }
            IconButton(onClick = onInsightsClick) { 
                Icon(
                    Icons.Default.InsertChart, 
                    contentDescription = "Insights", 
                    tint = if (activeTab == "insights") Color(0xFF001944) else Color(0xFF8e949a)
                ) 
            }
            IconButton(onClick = onSettingsClick) { 
                Icon(
                    Icons.Default.Person, 
                    contentDescription = "Settings", 
                    tint = if (activeTab == "settings") Color(0xFF001944) else Color(0xFF8e949a)
                ) 
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(
    userName: String, 
    onHomeClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {}, 
    onLogoutClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Box(modifier = Modifier.size(64.dp).background(Color(0xFFE6E8EB), CircleShape))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DashboardPrimary)
        Spacer(modifier = Modifier.height(40.dp))
        
        TextButton(onClick = onHomeClick) {
            Icon(Icons.Default.Home, contentDescription = null, tint = DashboardPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Dashboard", color = DashboardPrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBudgetClick) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DashboardPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Budgeting", color = DashboardPrimary)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        TextButton(onClick = onLogoutClick) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = DashboardError)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Logout", color = DashboardError)
        }
    }
}

private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)
