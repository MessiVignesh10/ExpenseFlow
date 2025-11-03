package com.example.expenseflow.presentation.analytics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.expenseflow.viewmodel.AnalyticsViewModel


@Composable
fun AnalyticsScreen() {
    AnalyticsLineChart()
}
@Composable
fun AnalyticsLineChart() {
    val viewModel: AnalyticsViewModel = viewModel()
    val points by viewModel.chartPoints.collectAsState()
    val xLabels by viewModel.xLabel.collectAsState()

    val stepsX = points.size - 1.coerceAtLeast(0)

    if (points.isEmpty()) {
        return
    }


    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .backgroundColor(Color.Transparent)
        .steps(stepsX)
        .labelAndAxisLinePadding(10.dp)
        .labelData { index -> xLabels.getOrNull(index) ?: "" }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(10.dp)
        .labelData { i ->
            val max = points.maxOf { it.y }
            val step = if (i == 0) 0f else max / 5f * i
            String.format(java.util.Locale.US, "%.1f", step)
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            plotType = PlotType.Line,
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(),
                    intersectionPoint = IntersectionPoint(),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )

    LineChart(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp), lineChartData = lineChartData)
}
