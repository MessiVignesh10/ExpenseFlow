package com.example.expenseflow.presentation.history

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.presentation.dashboard.ExpenseDetailRow
import com.example.expenseflow.presentation.dashboard.ExpenseRow
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.ui.theme.greenSecondary
import com.example.expenseflow.viewmodel.HistoryState
import com.example.expenseflow.viewmodel.HistoryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {


    Scaffold(modifier.fillMaxSize()) { innerpadding ->
        Column(
            modifier
                .padding(innerpadding)
                .fillMaxWidth()
        )
        {
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Title()
                Spacer(modifier.height(10.dp))
                SearchBar(viewModel = viewModel)
                Spacer(modifier.height(10.dp))
                CombinedSortFunction()
                Spacer(modifier.height(10.dp))
                DetailedHistory()
            }

        }
    }
}

@Composable
fun CombinedSortFunction(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoriesDropDown()
        SortSection()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSection(modifier: Modifier = Modifier, viewModel: HistoryViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    val selectedSort by viewModel.selectedSort.collectAsState()
    val sortOptions by viewModel.sortOptions.collectAsState()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(130.dp)
    ) {
        OutlinedTextField(
            value = selectedSort,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = greenPrimary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            sortOptions.forEach { sortOption ->
                DropdownMenuItem(
                    text = { Text(sortOption) },
                    onClick = { viewModel.onSortChange(input = sortOption); expanded = false },
                    modifier.background(color = Color.White))

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDropDown(modifier: Modifier = Modifier, viewModel: HistoryViewModel = viewModel()) {

    var expanded by remember { mutableStateOf(false) }
    val options by viewModel.dropDownCategories.collectAsState()
    val selected by viewModel.selectedCategory.collectAsState()


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier
            .width(200.dp)
            .padding(start = 20.dp)
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = greenPrimary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { viewModel.onExpandedChange(input = option);expanded = false },
                    modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, viewModel: HistoryViewModel) {
    val query by viewModel.query.collectAsState()


    OutlinedTextField(
        value = query,
        onValueChange = { viewModel.onQueryChange(input = it) },
        modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = greenPrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "search"
            )
        },
        placeholder = { Text("Search Expenses..") }
    )

}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Expense History", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier.height(10.dp))
            Text(
                "All your expenses in one place",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun DetailedHistory(modifier: Modifier = Modifier, viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()

    Box(
        modifier
            .fillMaxSize()
            .padding(start = 6.dp, end = 6.dp)
    ) {
        when (state) {
            is HistoryState.Loading -> CircularProgressIndicator()
            is HistoryState.Success -> {
                val expenses = (state as HistoryState.Success).expenses
                val filtered = remember(expenses, query, selectedCategory) {
                    val q = query.trim()
                    val isAll = selectedCategory.equals("All Categories", ignoreCase = true)
                    expenses.filter { e ->
                        val desc = e.description.lowercase()
                        val categoryName = e.category.name.lowercase()

                        val qOk = q.isBlank() || desc.contains(q) || categoryName.contains(q)
                        val cOk = isAll || categoryName == selectedCategory.lowercase()

                        qOk && cOk
                    }
                }

                if (filtered.isEmpty()) {
                    Text(
                        "No expense Match your search",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn {
                        items(filtered) { expense ->
                            Column(
                                modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Text(
                                    expense.date,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            ElevatedCard(
                                modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .padding(20.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 30.dp,
                                    pressedElevation = 50.dp
                                ),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    ExpenseRowOnHistoryPage(expense = expense)
                                }
                            }
                        }
                    }
                }
            }

            is HistoryState.Error -> {
                Text(text = (state as HistoryState.Error).message)
            }
        }
    }
}


@Composable
fun ExpenseRowOnHistoryPage(expense: Expense, modifier: Modifier = Modifier) {
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Row(

                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryName = expense.category.label.take(1)
                    .uppercase() + expense.category.label.substring(1..expense.category.label.length - 1)
                val paymentName = expense.paymentMethod.label.take(1)
                    .uppercase() + expense.paymentMethod.label.substring(1..expense.paymentMethod.label.length - 1)
                Text(categoryName, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier.width(40.dp))
                Text(paymentName, color = greenPrimary, fontSize = 12.sp)
            }
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
    HistoryScreen()
}
