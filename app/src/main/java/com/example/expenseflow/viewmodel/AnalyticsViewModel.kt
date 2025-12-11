package com.example.expenseflow.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expenseflow.data.model.Category
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.exp


data class DonutSlice(
    val category: Category,
    val amount: Float,
    val color: Color
)

sealed class AnalyticsUiState {
    object Idle : AnalyticsUiState()
    object Loading : AnalyticsUiState()
    data class Success(val expenses: List<Expense>) : AnalyticsUiState()
    data class Error(val message: String) : AnalyticsUiState()
}

class AnalyticsViewModel : ViewModel() {

    private val repository = TransactionsRepository()
    private val formatter = DateTimeFormatter.ofPattern("MMM d ,yyyy", Locale.ENGLISH)

    // UI State
    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Idle)
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    // Chart data (6-month line chart)
    private val _chartPoints = MutableStateFlow<List<Float>>(emptyList())
    val chartPoints: StateFlow<List<Float>> = _chartPoints

    private val _xLabel = MutableStateFlow<List<String>>(emptyList())
    val xLabel: StateFlow<List<String>> = _xLabel

    // Monthly
    private val _monthlyTotal = MutableStateFlow(0.0)
    val monthlyTotal: StateFlow<Double> = _monthlyTotal

    private val _monthlyAverage = MutableStateFlow(0.0)
    val monthlyAverage: StateFlow<Double> = _monthlyAverage

    private val _monthlyCount = MutableStateFlow(0)
    val monthlyCount: StateFlow<Int> = _monthlyCount

    // Last 3 months
    private val _threeMonthTotal = MutableStateFlow(0.0)
    val threeMonthTotal: StateFlow<Double> = _threeMonthTotal

    private val _threeMonthAverage = MutableStateFlow(0.0)
    val threeMonthAverage: StateFlow<Double> = _threeMonthAverage

    private val _threeMonthCount = MutableStateFlow(0)
    val threeMonthCount: StateFlow<Int> = _threeMonthCount

    // Yearly
    private val _yearlyTotal = MutableStateFlow(0.0)
    val yearlyTotal: StateFlow<Double> = _yearlyTotal

    private val _yearlyAverage = MutableStateFlow(0.0)
    val yearlyAverage: StateFlow<Double> = _yearlyAverage

    private val _yearlyCount = MutableStateFlow(0)
    val yearlyCount: StateFlow<Int> = _yearlyCount

    // Selected Range
    private val _selectedRange = MutableStateFlow(0)
    val selectedRange: StateFlow<Int> = _selectedRange

    init {
        loadExpenses()
    }

    // -----------------------------------------------------
    // LOAD EXPENSES
    // -----------------------------------------------------

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = AnalyticsUiState.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = AnalyticsUiState.Success(expenses)
                computeChart(expenses = expenses)
                computeAnalysis(expenses = expenses)
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error(
                    e.localizedMessage ?: "Something went wrong"
                )
            }
        }
    }

    private fun parseDate(expenses: Expense): LocalDate? {
        return try {
            LocalDate.parse(expenses.date, formatter)
        } catch (_: Exception) {
            null
        }
    }

    private fun filterByRange(expenses: List<Expense>, range: Int): List<Expense> {
        val currentDate = LocalDate.now()
        val threeMonthsAgo = currentDate.minusMonths(3)

        return expenses.filter { expense ->
            val date = parseDate(expenses = expense) ?: return@filter false

            when (range) {
                0 -> date.month == currentDate.month && date.year == currentDate.year
                1 -> !date.isBefore(threeMonthsAgo)
                2 -> date.year == currentDate.year
                else -> false
            }
        }
    }

    private fun calculateTotals(expenses: List<Expense>): Pair<Double, Int> {
        return expenses.sumOf { it.amount } to expenses.size
    }

    private fun computeAnalysis(expenses: List<Expense>) {
        val monthList = filterByRange(expenses = expenses, range = 0)
        val (monthlyTotal, monthlyCount) = calculateTotals(expenses = monthList)
        _monthlyTotal.value = monthlyTotal
        _monthlyCount.value = monthlyCount
        _monthlyAverage.value = monthlyTotal / 30.0

        val threeMonthList = filterByRange(expenses = expenses, range = 1)
        val (threeMonthsTotal, threeMonthsCount) = calculateTotals(threeMonthList)
        _threeMonthTotal.value = threeMonthsTotal
        _threeMonthCount.value = threeMonthsCount
        _threeMonthAverage.value = threeMonthsTotal / 92.0

        val yearlyList = filterByRange(expenses = expenses, range = 2)
        val (yearlyTotal, yearlyCount) = calculateTotals(expenses = yearlyList)
        _yearlyTotal.value = yearlyTotal
        _yearlyCount.value = yearlyCount
        _yearlyAverage.value = yearlyTotal / 365.0
    }

    private fun computeChart(expenses: List<Expense>) {
        if (expenses.isEmpty()) return

        val endMonth = YearMonth.now()
        val startMonth = endMonth.minusMonths(5)
        val months = (0L..5L).map { startMonth.plusMonths(it) }

        val totals = months.map { ym ->
            expenses
                .mapNotNull { e -> parseDate(e)?.let { date -> YearMonth.from(date) to e.amount } }
                .filter { (ym2, _) -> ym2 == ym }
                .sumOf { it.second }
        }

        _chartPoints.value = totals.map { it.toFloat() }

        val labelFormatter = DateTimeFormatter.ofPattern("MMM")
        _xLabel.value = months.map { it.atDay(1).format(labelFormatter) }
    }

    fun selectedRange(index: Int) {
        _selectedRange.value = index
        val expenses = (uiState.value as? AnalyticsUiState.Success)?.expenses ?: return
        computeAnalysis(expenses = expenses)
    }

    val activeTotal = selectedRange.map {
        when (it) {
            0 -> monthlyTotal.value
            1 -> threeMonthTotal.value
            2 -> yearlyTotal.value
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val activeAverage = selectedRange.map {
        when (it) {
            0 -> monthlyAverage.value
            1 -> threeMonthAverage.value
            2 -> yearlyAverage.value
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val activeCount = selectedRange.map {
        when (it) {
            0 -> monthlyCount.value
            1 -> threeMonthCount.value
            2 -> yearlyCount.value
            else -> 0
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val monthlySlice = uiState.map { state ->
        if (state is AnalyticsUiState.Success)
            getMonthlySlices(expenses = state.expenses)
        else emptyList()
    }.stateIn(viewModelScope , SharingStarted.Eagerly , initialValue = emptyList())

    val threeMonthSlice = uiState.map { state ->
        if (state is AnalyticsUiState.Success)
            getThreeMonthSlices(expenses = state.expenses)
        else emptyList()
    }.stateIn(viewModelScope, SharingStarted.Eagerly,emptyList())

    val yearlySlice = uiState.map { state ->
        if (state is AnalyticsUiState.Success)
            getYearlySlice(expenses = state.expenses)
        else emptyList()
    }.stateIn(viewModelScope, SharingStarted.Eagerly,emptyList())



    fun getMonthlySlices(expenses: List<Expense>): List<DonutSlice> {

        val monthList = filterByRange(expenses, 0)
        return monthList
            .groupBy { it.category }
            .map { (category, items) ->
                DonutSlice(
                    category = category,
                    items.sumOf { it.amount }.toFloat(),
                    color = pickColorForCategory(category)
                )
            }
    }

    fun getThreeMonthSlices(expenses: List<Expense>): List<DonutSlice> {

        val threeMonthsList = filterByRange(expenses, 1)

        return threeMonthsList
            .groupBy { it.category }
            .map { (category, items) ->
                DonutSlice(
                    category = category,
                    amount = items.sumOf { it.amount }.toFloat(),
                    color = pickColorForCategory(category)
                )
            }
    }
    fun getYearlySlice(expenses: List<Expense>): List<DonutSlice> {

        val yearlyList = filterByRange(expenses, 2)

        return yearlyList
            .groupBy { it.category }
            .map { (category, items) ->
                DonutSlice(
                    category = category,
                    amount = items.sumOf { it.amount }.toFloat(),
                    color = pickColorForCategory(category)
                )
            }
    }

    private fun pickColorForCategory(category: Category): Color {
        return when (category) {
            Category.FOOD -> Color(0xFFE91E63)        // Pink
            Category.TRANSPORT -> Color(0xFF3F51B5)   // Indigo
            Category.FUN -> Color(0xFF9C27B0)         // Purple
            Category.SHOPPING -> Color(0xFFFFC107)    // Amber
            Category.HEALTH -> Color(0xFF4CAF50)      // Green
            Category.BILLS -> Color(0xFFFF5722)       // Deep Orange
            Category.EDUCATION -> Color(0xFF009688)   // Teal
            Category.TRAVEL -> Color(0xFF03A9F4)      // Light Blue
            Category.OTHER -> Color(0xFF9E9E9E)       // Grey
        }
    }
}
