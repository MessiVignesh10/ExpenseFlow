package com.example.expenseflow.presentation.analytics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import com.example.expenseflow.viewmodel.AnalyticsUiState
import com.example.expenseflow.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    BarChart()
}

@Composable
fun BarChart(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val chartPoints by viewModel.chartPoints.collectAsState()
    val xLabels by viewModel.xLabel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    when (uiState) {
        is AnalyticsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is AnalyticsUiState.Error -> {
            Text(
                text = (uiState as AnalyticsUiState.Error).message,
                modifier = Modifier.padding(16.dp)
            )
        }

        is AnalyticsUiState.Success -> {
            BarChartSection(points = chartPoints, xLabels = xLabels)
        }

        else -> Unit
    }

}

@Composable
fun BarChartSection(points: List<Point>, xLabels: List<String>) {
    if (points.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Expense Date Available")
        }
        return
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .startPadding(16.dp)
        .labelData { i ->
            xLabels.getOrNull(i).orEmpty()
        }
        .axisLabelAngle(45f)
        .build()

    val maxY = points.maxOf { it.y }
    val steps = 5

    val yAxisDate = AxisData.Builder()
        .labelData { i ->
            val value = (maxY/steps)*i
            value.toInt().toString()
        }
        .steps(steps)
        .startDrawPadding(50.dp)
        .build()

    val bars = points.map { point ->
        BarData(
            point = point, color = Color.Green, gradientColorList = listOf(
                Color.Blue, Color.Black,
                Color.Red
            )
        )
    }

    val barChartData = BarChartData(
        chartData = bars,
        xAxisData = xAxisData,
        yAxisData = yAxisDate,
        backgroundColor = Color.White,
        showXAxis = true,
        showYAxis = false, barChartType = BarChartType.VERTICAL,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        BarChart(
            modifier = Modifier
                .width((points.size * 56).dp)
                .height(300.dp),
            barChartData = barChartData
        )
    }
}