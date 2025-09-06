package com.example.expenseflow.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.presentation.dashboard.ExpenseDetailRow
import com.example.expenseflow.presentation.dashboard.ExpenseRow
import com.example.expenseflow.viewmodel.AddScreenState
import com.example.expenseflow.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 6.dp , end = 6.dp), contentAlignment = Alignment.Center
    ) {
        when (val s = state) {
            is AddScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is AddScreenState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap to retry",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Transparent)
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable { viewModel.loadExpenses() }
                    )
                }
            }

            is AddScreenState.Success -> {
                Card(
                    modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    LazyColumn(modifier.fillMaxSize()) {
                        items(s.expenses) { expense ->
                            ExpenseRow(expense)
                        }

                    }

                }
            }
        }
    }
}


@Composable
fun ExpenseRow(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                expense.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(expense.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = "$" + "%.2f".format(expense.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}