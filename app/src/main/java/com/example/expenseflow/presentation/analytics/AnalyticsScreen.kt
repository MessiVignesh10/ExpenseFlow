package com.example.expenseflow.presentation.analytics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.viewmodel.AnalyticsViewModel
import com.example.expenseflow.viewmodel.DonutSlice

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = viewModel()) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        OverallScreenModules(viewModel = viewModel)
    }
}


@Composable
fun OverallScreenModules(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel) {

    val selectedRange by viewModel.selectedRange.collectAsState()

    val slices = when (selectedRange) {
        2 -> viewModel.yearlySlice.collectAsState().value
        else -> null
    }

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        HeadingSection()
        Spacer(modifier.height(24.dp))
        TriToggle(selectedIndex = selectedRange, onSelected = { viewModel.selectedRange(it) })
        Spacer(modifier.height(24.dp))
        TriCards(viewModel = viewModel)
        Spacer(modifier.height(24.dp))
        if (slices != null) {
            DonutChart(slices = slices)
            Spacer(modifier.height(24.dp))
        }
        BarchartSection()
        Spacer(modifier.height(24.dp))
    }
}

@Composable
fun DonutChart(
    slices: List<DonutSlice>?,
    modifier: Modifier = Modifier,
    thickness: Float = 80f
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(slices) {
        if (slices != null) {
            startAnimation = true
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            delayMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "Donut Chart"
    )
    if (slices != null) {
        ElevatedCard(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Spending by Category",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier.height(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val animateTotalSweep = 360f * animatedProgress
                    val total = slices.sumOf { it.amount.toDouble() }.toFloat()
                    var startAngle = -90f

                    slices.sortedByDescending { it.amount }.forEach { slice ->
                        val sliceSweep = (slice.amount / total) * animateTotalSweep
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sliceSweep,
                            useCenter = false,
                            style = Stroke(
                                width = thickness,
                                cap = StrokeCap.Butt
                            )
                        )
                        startAngle += sliceSweep
                    }
                }
                Spacer(modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    slices.forEach { slice ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(color = slice.color, shape = CircleShape)
                                )
                                Text(
                                    slice.category.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                String.format("$%.2f (%.1f%%)", slice.amount, slice.percentage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TriCards(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel) {

    val total by viewModel.activeTotal.collectAsState(initial = 0)
    val average by viewModel.activeAverage.collectAsState(initial = 0.0)
    val count by viewModel.activeCount.collectAsState(initial = 0)


    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFE8F5E9), // A light green color
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Total Icon",
                        tint = Color(0xFF4CAF50) // A darker green color
                    )
                }
                Text("Total", style = MaterialTheme.typography.labelMedium)
                Text(
                    "$${total.toFloat()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        ElevatedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFFFF3E0), // A light orange color
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Daily Avg Icon",
                        tint = Color(0xFFFF9800) // A darker orange color
                    )
                }
                Text("Daily Avg", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = String.format("$%.2f", average),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        ElevatedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFF3E5F5), // A light purple color
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Expenses Icon",
                        tint = Color(0xFF9C27B0) // A darker purple color
                    )
                }
                Text("Expenses", style = MaterialTheme.typography.labelMedium)
                Text(
                    "$count",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
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
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .clip(CircleShape)
    ) {
        headers.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        Text(
            "Analytics",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Understand your spending patterns",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BarchartSection(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(300.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier.padding(16.dp)) {
            Text(
                "6-Month Trend",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier.height(16.dp))
            CustomBarChart()
        }
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

        LaunchedEffect(data) {
            startAnimation = true
        }
        val animatedHeight = data.mapIndexed { idx, value ->
            animateFloatAsState(
                targetValue = if (startAnimation) value else 0f,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = idx * 120,
                    easing = FastOutSlowInEasing
                )
            ).value
        }

        val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.isEmpty()) return@Canvas
            val barCount = data.size

            val bottomPadding = 60f
            val availableHeight = size.height - bottomPadding
            val maxValue = data.maxOrNull() ?: 0f

            val totalWidth = size.width
            val segmentWidth = totalWidth / barCount
            val barWidth = segmentWidth * 0.6f
            val barSpacing = segmentWidth * 0.4f
            var currentX = barSpacing / 2f

            animatedHeight.forEachIndexed { idx, height ->
                val barHeight = (height / maxValue) * availableHeight
                val barX = currentX

                val radius = 8.dp.toPx()
                val cornerRadius = CornerRadius(radius)

                drawRoundRect(
                    color = Color(0xFF4CAF50),
                    topLeft = Offset(barX, availableHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = cornerRadius
                )

                if (labels.size > idx) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = onSurfaceColor
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        drawText(
                            labels[idx],
                            barX + barWidth / 2f,   // center of bar
                            size.height - 20f,
                            paint
                        )
                    }
                }
                currentX += segmentWidth
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Pri() {
    AnalyticsScreen()
}
