package com.example.expenseflow.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
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
    CustomBarChart()
}

@Composable
fun CustomBarChart(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {
    val data = listOf(100f, 80f, 150f, 180f)
    val labels = listOf("June", "Jul", "Aug", "Sep")

    Canvas(modifier = Modifier.size(300.dp)) {
        val barWidth = 40f
        val spaceBetween = 40f
        var currentX = 30f

        data.forEachIndexed { idx, d ->
            val barHeight = d
            drawRect(
                color = Color.Green,
                topLeft = Offset(currentX, size.height - 50f - barHeight),
                size = Size(barWidth, barHeight)
            )
            drawContext.canvas.nativeCanvas.drawText(
                labels[idx],
                currentX,
                size.height - 20f, // bottom
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24f
                }
            )
            currentX += barWidth + spaceBetween
        }
    }
}

@Preview
@Composable
private fun pri() {
    AnalyticsScreen()
}