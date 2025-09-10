package com.example.expenseflow.presentation.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun CategoryPieChart(modifier: Modifier = Modifier) {
    PieChart(modifier.size(200.dp) ,
        data = Pie())
}

@Preview
@Composable
private fun Pri() {
    AnalyticsScreen()
}