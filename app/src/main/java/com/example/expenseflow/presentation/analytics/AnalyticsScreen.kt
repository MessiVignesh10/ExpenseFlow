package com.example.expenseflow.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.viewmodel.AnalyticsUi
import com.example.expenseflow.viewmodel.AnalyticsViewModel
import com.example.expenseflow.viewmodel.CategoryBar
import com.example.expenseflow.viewmodel.TimeRange
import kotlinx.coroutines.launch

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
    ProgressBarAndRangeToggle()
}

@Composable
fun ProgressBarAndRangeToggle(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val range by viewModel.range.collectAsState()
    val bars by viewModel.categoryBars.collectAsState()

    Column(
        modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier.height(20.dp))
        RangeToggle(
            selected = range,
            onSelected = viewModel::setRange
        )

        when (uiState) {
            is AnalyticsUi.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is AnalyticsUi.Error -> {
                val msg = (uiState as AnalyticsUi.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(msg) }
            }

            is AnalyticsUi.Success, AnalyticsUi.Idle -> {
                if (bars.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data to show")
                    }
                } else {
                    // Card with donut + legend
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Spending by Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Donut (tap to play 1% → 100%)
                                SegmentedDonut(
                                    data = bars,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 12.dp)
                                        .aspectRatio(1f)
                                )

                                // Legend
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    bars.forEach {
                                        LegendRow(
                                            color = it.color,
                                            name = it.name,
                                            amount = it.amount,
                                            percent = it.percent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RangeToggle(
    selected: TimeRange,
    onSelected: (TimeRange) -> Unit
) {
    val options = listOf(
        TimeRange.THIS_MONTH to "This Month",
        TimeRange.LAST_6_MONTHS to "6 Months",
        TimeRange.THIS_YEAR to "This Year"
    )
    val selectedIndex = options.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    val trackShape = RoundedCornerShape(999.dp)
    val thumbShape = RoundedCornerShape(999.dp)
    var trackWidth by remember { mutableStateOf(0) }

    val thumbX = remember { Animatable(0f) }
    val segmentWidthPx = if (trackWidth == 0) 0f else trackWidth.toFloat() / options.size

    LaunchedEffect(selectedIndex, segmentWidthPx) {
        if (segmentWidthPx > 0f) {
            thumbX.animateTo(
                targetValue = selectedIndex * segmentWidthPx,
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(trackShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
            .onGloballyPositioned { trackWidth = it.size.width }
    ) {
        if (segmentWidthPx > 0f) {
            Box(
                Modifier
                    .offset { IntOffset(thumbX.value.toInt(), 0) }
                    .width((segmentWidthPx).dp)
                    .fillMaxHeight()
                    .clip(thumbShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { idx, (value, label) ->
                val isSel = idx == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 28.dp)
                        ) { onSelected(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

/* -------------------- Donut (tap to animate) -------------------- */

@Composable
private fun SegmentedDonut(
    data: List<CategoryBar>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Float = 18f
) {
    val play = remember { Animatable(0.01f) }      // 0..1
    var running by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()           // ← use this, not LaunchedEffect inside onClick

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 120.dp)
            ) {
                if (!running) {
                    running = true
                    scope.launch {
                        play.snapTo(0.01f)
                        play.animateTo(
                            1f,
                            animationSpec = tween(1400, easing = FastOutSlowInEasing)
                        )
                        running = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

            // track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                size = Size(size.width, size.height)
            )

            // segments filled by global progress
            val sweeps = data.map { 360f * it.percent.toFloat() }
            var acc = -90f
            sweeps.forEachIndexed { i, seg ->
                val segSweep = play.value * seg
                drawArc(
                    color = data[i].color,
                    startAngle = acc,
                    sweepAngle = segSweep,
                    useCenter = false,
                    style = stroke,
                    size = Size(size.width, size.height)
                )
                acc += seg // advance by full segment size (visual growth still controlled by play)
            }
        }

        Text(
            text = "${(play.value * 100).toInt()}%",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
/* -------------------- Legend -------------------- */

@Composable
private fun LegendRow(
    color: Color,
    name: String,
    amount: Double,
    percent: Double
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            "${formatCurrency(amount)}   ${"%.1f".format(percent * 100)}%",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatCurrency(value: Double): String =
    java.text.NumberFormat.getCurrencyInstance().format(value)
@Preview
@Composable
private fun Pri() {
    AnalyticsScreen()
}