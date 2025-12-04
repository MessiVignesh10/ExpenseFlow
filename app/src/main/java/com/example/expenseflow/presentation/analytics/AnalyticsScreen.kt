package com.example.expenseflow.presentation.analytics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import com.example.expenseflow.R
import com.example.expenseflow.viewmodel.AnalyticsUiState
import com.example.expenseflow.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {
    Column {
        OverallScreenModules(viewModel = viewModel)

    }
}


@Composable
fun OverallScreenModules(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel) {

    val selectedRange by viewModel.selectedRange.collectAsState()
    Column {
        HeadingSection()
        Spacer(modifier.height(20.dp))
        TriToggle(selectedIndex = selectedRange, onSelected = { viewModel.updateSelectedRange(it) })
        Spacer(modifier.height(20.dp))
        TriCards(viewModel = viewModel)
        Spacer(modifier.height(20.dp))
        BarchartSection()
    }
}

@Composable
fun TriCards(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel) {

    val total by viewModel.activeTotal.collectAsState(initial = 0)
    val average by viewModel.activeAverage.collectAsState(initial = 0.0)
    val count by viewModel.activeCount.collectAsState(initial = 0)


    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(modifier.size(120.dp)) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.grossprofit),
                    contentDescription = null,
                    modifier.size(50.dp)
                )
                Spacer(modifier.height(8.dp))
                Text("Total")
                Spacer(modifier.height(10.dp))
                Text("$$total", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)

            }
        }
        ElevatedCard(modifier.size(120.dp)) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.grossprofit),
                    contentDescription = null,
                    modifier.size(50.dp)
                )
                Spacer(modifier.height(8.dp))
                Text("Daily Avg")
                Spacer(modifier.height(10.dp))
                Text("$$average", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)

            }
        }
        ElevatedCard(modifier.size(120.dp)) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.grossprofit),
                    contentDescription = null,
                    modifier.size(50.dp)
                )
                Spacer(modifier.height(8.dp))
                Text("Expenses")
                Spacer(modifier.height(10.dp))
                Text("$count", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)

            }
        }
    }
}

@Composable
fun TriToggle(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val headers = listOf("This Month", "3 Months", "This year")
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            headers.forEachIndexed { idx, header ->
                val isSelected = idx == selectedIndex
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color.White.copy(
                            alpha = 0.6f
                        ) else Color.Transparent,
                        contentColor = if (isSelected) Color.Black else Color.Gray
                    ), onClick = { onSelected(idx) }
                ) {
                    Text(
                        header,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeadingSection(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Analytics", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier.height(20.dp))
        Text(
            "Understand your spending patterns",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
fun BarchartSection(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(200.dp)
    ) {
        CustomBarChart()
    }
}


@Composable
fun CustomBarChart(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {

    val data by viewModel.chartPoints.collectAsState()
    val labels by viewModel.xLabel.collectAsState()

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        var startAnimation by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            startAnimation = true
        }
        val animatedHeight = data.mapIndexed { idx, value ->
            animateFloatAsState(
                targetValue = if (startAnimation) value else 0f,
                animationSpec = tween(
                    durationMillis = 2000,
                    delayMillis = idx * 120,
                    easing = FastOutSlowInEasing
                )
            ).value
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barCount = data.size

            val bottomPadding = 60f
            val totalWidth = size.width
            val spaceRatio = 0.2f
            val segmentWidth = totalWidth / barCount
            val barWidth = segmentWidth * (1f - spaceRatio)
            val spaceBetween = segmentWidth * spaceRatio

            var currentX = 0f

            data.forEachIndexed { idx, value ->

                val height = animatedHeight[idx]
                val barX = currentX + (segmentWidth - barWidth) / 2f
                val radius = 20f

// Create a round-rect but only round the top corners
                val barRect = androidx.compose.ui.geometry.Rect(
                    barX,
                    size.height - bottomPadding - height,
                    barX + barWidth,
                    size.height - bottomPadding
                )

                val roundRect = RoundRect(
                    rect = barRect,
                    topLeft = CornerRadius(radius, radius),
                    topRight = CornerRadius(radius, radius),
                    bottomLeft = CornerRadius(0f, 0f),
                    bottomRight = CornerRadius(0f, 0f)
                )

                val path = Path().apply {
                    addRoundRect(roundRect)
                }

// Clip to the path and draw inside it
                clipPath(path) {
                    drawRect(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(barRect.left, barRect.top),
                        size = Size(barWidth, height)
                    )
                }

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    drawText(
                        labels[idx],
                        barX + barWidth / 2f,   // center of bar
                        size.height - 20f,
                        paint
                    )
                }
                currentX += segmentWidth
            }
        }
    }
}

@Preview
@Composable
private fun pri() {
    AnalyticsScreen()
}