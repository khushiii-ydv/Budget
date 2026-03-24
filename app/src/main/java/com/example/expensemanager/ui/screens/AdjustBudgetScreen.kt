package com.example.expensemanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensemanager.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustBudgetScreen(
    onBackClick: () -> Unit,
    viewModel: BudgetViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val budgetLimits = remember { mutableStateMapOf<String, String>() }

    // Initialize limits and fetch data
    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    LaunchedEffect(uiState.categories) {
        uiState.categories.forEach { category ->
            budgetLimits[category.category] = category.limit.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adjust Budgets", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001944)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF001944))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFf7f9fc))
            )
        },
        containerColor = Color(0xFFf7f9fc),
        bottomBar = {
            Button(
                onClick = {
                    var successCount = 0
                    val totalToUpdate = budgetLimits.size
                    if (totalToUpdate == 0) return@Button

                    budgetLimits.forEach { (category, limitStr) ->
                        val limit = limitStr.toDoubleOrNull() ?: 0.0
                        viewModel.updateBudget(category, limit) { success ->
                            if (success) successCount++
                            if (successCount == totalToUpdate) {
                                Toast.makeText(context, "All budgets updated!", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001944)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save All Changes", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading && uiState.categories.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF001944)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    items(uiState.categories) { category ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(category.category, fontWeight = FontWeight.Bold, color = Color(0xFF001944), fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = budgetLimits[category.category] ?: "",
                                    onValueChange = { budgetLimits[category.category] = it },
                                    label = { Text("Limit Amount (₹)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}
