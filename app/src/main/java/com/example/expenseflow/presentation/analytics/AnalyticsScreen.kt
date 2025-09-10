package com.example.expenseflow.presentation.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.viewmodel.AnalyticsUi
import com.example.expenseflow.viewmodel.AnalyticsViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier.fillMaxSize()) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            OverallAnalyticsFunction()
        }
    }
}

@Composable
fun OverallAnalyticsFunction(modifier: Modifier = Modifier) {
    CategoryPieChart()
}

@Composable
fun CategoryPieChart(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {
    val pieData by viewModel.pieDate.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is AnalyticsUi.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is AnalyticsUi.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val message = (uiState as AnalyticsUi.Error).message
            Text(message, color = greenPrimary)
        }

        is AnalyticsUi.Success -> {
            if (pieData.isEmpty()) {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Data to show")
                }
            } else {
                PieChart(
                    modifier.size(200.dp),
                    data = pieData,
                    selectedScale = 1.06f,
                    spaceDegree = 2f,
                )
            }
        }
        is AnalyticsUi.Idle -> {}
    }
}