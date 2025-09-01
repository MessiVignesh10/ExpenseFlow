package com.example.expenseflow.presentation.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.R
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.ui.theme.greenSecondary
import com.example.expenseflow.viewmodel.AddScreenState
import com.example.expenseflow.viewmodel.AddScreenViewModel


data class BottomNavItem(
    val icon: Painter,
    val label: String,
    val route: String
)

data class SummaryCards(
    val image: Int,
    val title: String,
    val value: String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {

    val bottomNavItems =
        listOf(
            BottomNavItem(painterResource(id = R.drawable.home), "Home", "home"),
            BottomNavItem(painterResource(id = R.drawable.add), "Add", "add"),
            BottomNavItem(painterResource(id = R.drawable.analysis), "Analysis", "analytics"),
            BottomNavItem(painterResource(id = R.drawable.history), "Recents", "history")
        )
    var selectedIndex by remember { mutableStateOf(0) }


    Scaffold(modifier.fillMaxSize(), bottomBar = {
        BottomAppBar(
            modifier.fillMaxWidth(),
            containerColor = Color.White,
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    icon = { Icon(painter = item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    alwaysShowLabel = true,
                    modifier = Modifier.size(30.dp),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = greenPrimary
                    )
                )
            }
        }
    }) { innerpadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(innerpadding)
        ) {
            OverallDashboardMain()
        }
    }
}

@Composable
fun OverallDashboardMain(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardOverview()
        Spacer(modifier.height(16.dp))
        DashBoardSummary()
        Spacer(modifier.height(16.dp))
        AddNewExpenseButton(modifier.padding(top = 50.dp))
        Spacer(modifier.height(16.dp))
        SampleHistory()
        Spacer(modifier.height(16.dp))
        AnalyticsAndHistoryOnDashboard()
    }
}

@Composable
fun DashBoardSummary(modifier: Modifier = Modifier) {

    val gridItems = listOf(
        SummaryCards(image = R.drawable.calendar, title = "This Month", value = "10.00"),
        SummaryCards(image = R.drawable.dollar, title = "This Week", value = "0.00"),
        SummaryCards(image = R.drawable.grossprofit, title = "Total Expenses", value = "9"),
        SummaryCards(image = R.drawable.loss, title = "Avg per Day", value = "0.32")
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().heightIn(0.dp,360.dp).background(Color.Transparent)
    ) {
        items(gridItems) { item ->
            Card(
                modifier.clickable(onClick = {}),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 30.dp, pressedElevation = 50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                DashboardSummaryContent(image = item.image, title = item.title, value = item.value)
            }
        }
    }
}

@Composable
fun DashboardSummaryContent(
    modifier: Modifier = Modifier,
    image: Int,
    title: String,
    value: String
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier.size(50.dp)
        )
        Spacer(modifier.height(10.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier.height(10.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
    }
}

@Composable
fun DashboardOverview(modifier: Modifier = Modifier) {

    val overviewText = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = greenPrimary
            )
        ) {
            append("$0.00 ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Color.Black)) {
            append("spent today")
        }
    }


    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Today's Overivew", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Spacer(modifier.height(10.dp))
        Card(
            modifier.size(200.dp, 50.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = greenSecondary)
        ) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(overviewText)
            }
        }
    }
}

@Composable
fun AnalyticsAndHistoryOnDashboard(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnalyticsOnDashboard()
        HistoryOnDashboard()
    }
}

@Composable
fun HistoryOnDashboard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp)
            .size(width = 150.dp, height = 60.dp)
            .clickable(onClick = {}),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painterResource(id = R.drawable.loss),
                contentDescription = "Analytics",
                modifier.size(30.dp)
            )
            Spacer(modifier.width(10.dp))
            Column {
                Text("History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("All Expenses", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AnalyticsOnDashboard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp)
            .size(width = 150.dp, height = 60.dp)
            .clickable(onClick = {}),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painterResource(id = R.drawable.svg),
                contentDescription = "Analytics",
                modifier.size(30.dp)
            )
            Spacer(modifier.width(10.dp))
            Column {
                Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("View Insights", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AddNewExpenseButton(modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = Modifier
            .padding(top = 20.dp)
            .size(height = 50.dp, width = 200.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 20.dp,
            focusedElevation = 14.dp,
            hoveredElevation = 12.dp
        ),
        colors = ButtonDefaults.elevatedButtonColors(containerColor = greenPrimary),
        border = BorderStroke(width = 2.dp, color = greenPrimary),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text("Add New Expense", color = Color.White, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun SampleHistory(
    modifier: Modifier = Modifier,
    viewModel: AddScreenViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                        .size(500.dp)
                        .padding(start = 10.dp, end = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        ExpenseDetailRow()
                        // Replace LazyColumn with a simple Column so content just grows
                        Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            s.expenses.forEach { expense ->
                                ExpenseRow(expense)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ExpenseDetailRow(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Recent Expenses", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(
            "View All \u2192",
            fontWeight = FontWeight.Bold,
            color = greenPrimary,
            fontSize = 20.sp,
            modifier = Modifier.clickable(onClick = {})
        )
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


@Preview
@Composable
private fun Pri() {
    DashboardScreen()
}