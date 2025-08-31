package com.example.expenseflow.presentation.dashboard

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.R
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.viewmodel.AddScreenState
import com.example.expenseflow.viewmodel.AddScreenViewModel


data class BottomNavItem(
    val icon: Painter,
    val label: String,
    val route: String
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
    var selectedIndex by remember {mutableStateOf(0)}


    Scaffold(modifier.fillMaxSize(), bottomBar = {
        BottomAppBar(
            modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
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
    }) {
        Column {
            SampleHistory()
        }
    }
}


@Composable
fun SampleHistory(
    modifier: Modifier = Modifier,
    viewModel: AddScreenViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(s.expenses.size) { idx ->
                        ExpenseRow(s.expenses[idx])
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
            Text(expense.description, style = MaterialTheme.typography.titleMedium)
            Text(expense.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = "$" + "%.2f".format(expense.amount),
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Preview
@Composable
private fun Pri() {
    DashboardScreen()
}