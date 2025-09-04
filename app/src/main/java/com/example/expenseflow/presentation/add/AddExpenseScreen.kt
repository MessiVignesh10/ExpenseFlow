package com.example.expenseflow.presentation.add

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.R
import com.example.expenseflow.ui.theme.greenPrimary
import com.example.expenseflow.viewmodel.AddScreenViewmodel

data class Categories(
    val picture: Int, val categoryName: String
)

data class PaymentItems(
    val paymentIcon: Painter, val paymentName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddExpenseScreen(modifier: Modifier = Modifier) {


    Scaffold(topBar = {
        TopAppBar(title = {
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                Text("Add Expense", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(
                    "Track your spending",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }, modifier = Modifier.padding(10.dp), navigationIcon = {
            Card(
                modifier
                    .size(36.dp)
                    .clickable(onClick = {}),
                shape = CircleShape,
                elevation = CardDefaults.elevatedCardElevation(pressedElevation = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier.size(20.dp)
                    )
                }
            }
        })
    }) { innerpadding ->
        Column(
            modifier
                .padding(innerpadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            OverallAddScreen()
        }
    }
}

@Composable
fun OverallAddScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        AmountSection()
        Spacer(modifier.height(20.dp))
        CategorySection()
        Spacer(modifier.height(20.dp))
        DateSection()
        Spacer(modifier.height(20.dp))
        DescriptionSection()
        Spacer(modifier.height(20.dp))
        PaymentMethodSection()
        Spacer(modifier.height(20.dp))
        AddExpenseButton()
    }
}

@Composable
fun AddExpenseButton(modifier: Modifier = Modifier, viewmodel: AddScreenViewmodel = viewModel()) {
    val validation by viewmodel.validation.collectAsState()
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {},
            modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = validation,
            colors = ButtonDefaults.buttonColors(containerColor = greenPrimary)
        ) {
            Text("✓ Add Expense")
        }
    }
}

@Composable
fun PaymentMethodSection(
    modifier: Modifier = Modifier,
    viewmodel: AddScreenViewmodel = viewModel()
) {

    val selectedPaymentMethod by viewmodel.selectedPaymentMethod.collectAsState()

    val paymentItems = listOf(
        PaymentItems(painterResource(id = R.drawable.dollars), "Cash"),
        PaymentItems(painterResource(id = R.drawable.atmcard), "Card"),
        PaymentItems(painterResource(id = R.drawable.mobilebanking), "Digital"),
    )
    ElevatedCard(
        modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 30.dp)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text("Payment Method", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(modifier.height(30.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier
                    .fillMaxWidth()
                    .size(width = 400.dp, height = 100.dp)
            ) {
                items(paymentItems) { item ->
                    val isSelected = selectedPaymentMethod == item.paymentName
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) greenPrimary else Color.White),
                        modifier = Modifier.clickable(onClick = {
                            viewmodel.onPaymentSelection(item.paymentName)
                        })
                    ) {
                        Column(
                            modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = item.paymentIcon,
                                contentDescription = item.paymentName,
                                modifier.size(50.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            Text(
                                item.paymentName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DescriptionSection(modifier: Modifier = Modifier, viewmodel: AddScreenViewmodel = viewModel()) {

    val description by viewmodel.description.collectAsState()


    ElevatedCard(
        modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 30.dp)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text("Description (Optional)", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
        BasicTextField(
            value = description,
            onValueChange = viewmodel::onDescriptionChange,
            modifier
                .padding(top = 10.dp, start = 30.dp)
                .fillMaxWidth()
                .size(width = 300.dp, height = 100.dp),
            decorationBox = { innerTextField ->
                if (description.isBlank()) {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(tween(durationMillis = 200)),
                        exit = slideOutVertically(tween(durationMillis = 200))
                    ) {
                        Text(
                            "What did you spend on?",
                            color = Color.Gray,
                        )
                    }
                } else innerTextField()
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSection(modifier: Modifier = Modifier, viewmodel: AddScreenViewmodel = viewModel()) {

    val date by viewmodel.date.collectAsState()
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }


    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                val selected = datePickerState.selectedDateMillis
                if (selected != null) {
                    viewmodel.onDatePicking(selected)
                }
                showDatePicker = false
            }) {
                Text("OK")
            }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) {
                Text("Cancel")
            }
        }, tonalElevation = DatePickerDefaults.TonalElevation) {
            DatePicker(state = datePickerState)
        }
    }
    ElevatedCard(
        modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 30.dp),
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Date", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            TextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier.clickable { showDatePicker = true })
                },
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
fun CategorySection(modifier: Modifier = Modifier, viewmodel: AddScreenViewmodel = viewModel()) {

    val categories = listOf(
        Categories(picture = R.drawable.cutlery, categoryName = "Food"),
        Categories(picture = R.drawable.sportcar, categoryName = "Transport"),
        Categories(picture = R.drawable.gamecontroller, categoryName = "Fun"),
        Categories(picture = R.drawable.onlineshopping, categoryName = "Shopping"),
        Categories(picture = R.drawable.healthcare, categoryName = "Health"),
        Categories(picture = R.drawable.bolt, categoryName = "Bills"),
        Categories(picture = R.drawable.graduation, categoryName = "Education"),
        Categories(picture = R.drawable.plane, categoryName = "Travel"),
        Categories(picture = R.drawable.threedots, categoryName = "Other"),
    )


    val selectedCategory by viewmodel.selectedCategory.collectAsState()

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Text(
            "Category",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .size(width = 400.dp, height = 380.dp)
        ) {
            items(categories) { item ->
                val isSelected = selectedCategory == item.categoryName
                Card(
                    modifier
                        .padding(10.dp)
                        .clickable(onClick = { viewmodel.onCategorySelection(item.categoryName) }),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) greenPrimary else Color.White)
                ) {
                    CategorySectionContent(
                        picture = item.picture,
                        categoryName = item.categoryName,
                        isSelected = isSelected
                    )
                }

            }
        }
    }
}

@Composable
fun CategorySectionContent(
    modifier: Modifier = Modifier, picture: Int, categoryName: String, isSelected: Boolean
) {

    Column(
        modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(picture),
            contentDescription = categoryName,
            modifier.size(40.dp)
        )
        Spacer(modifier.height(8.dp))
        Text(categoryName, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp)
    }
}

@Composable
fun AmountSection(modifier: Modifier = Modifier, viewmodel: AddScreenViewmodel = viewModel()) {
    val amount by viewmodel.amount.collectAsState()
    ElevatedCard(
        onClick = {},
        modifier
            .fillMaxWidth()
            .size(width = 300.dp, height = 100.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 30.dp, pressedElevation = 50.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier
                .fillMaxSize()
                .padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            Text("Amount", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            BasicTextField(
                value = amount,
                onValueChange = viewmodel::onAmountChange,
                modifier
                    .padding(top = 10.dp, start = 20.dp)
                    .size(width = 300.dp, height = 50.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                decorationBox = { innerTextField ->
                    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.dollarcurrencysymbol),
                            contentDescription = null,
                            modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        if (amount.isBlank()) {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(tween(durationMillis = 200)),
                                exit = slideOutVertically(tween(durationMillis = 200))
                            ) {
                                Text("0.00", color = Color.Gray)
                            }
                        } else innerTextField()
                    }
                })
        }
    }
}

@Preview
@Composable
private fun Pri() {
    AddExpenseButton()
}