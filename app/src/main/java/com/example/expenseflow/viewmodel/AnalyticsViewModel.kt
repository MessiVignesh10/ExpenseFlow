package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.function.DoubleUnaryOperator
import kotlin.math.exp
import kotlin.math.sign


sealed class AnalyticsUiState() {
    object Idle : AnalyticsUiState()
    object Loading : AnalyticsUiState()
    data class Success(val expense: List<Expense>) : AnalyticsUiState()
    data class Error(val message: String) : AnalyticsUiState()
}

class AnalyticsViewModel : ViewModel() {
    private val repository = TransactionsRepository()

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Idle)
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    private val _chartPoints = MutableStateFlow<List<Float>>(emptyList())
    val chartPoints: StateFlow<List<Float>> = _chartPoints

    private val _xLabel = MutableStateFlow<List<String>>(emptyList())
    val xLabel: StateFlow<List<String>> = _xLabel
// Expense Average ----------------------------------------------------------------------
    private val _monthlyExpense = MutableStateFlow(0.00)
    val monthlyExpense: StateFlow<Double> = _monthlyExpense

    private val _monthlyDailyAverage = MutableStateFlow(0.0)
    val monthlyDailyAverage: StateFlow<Double> = _monthlyDailyAverage

    private val _thisMonthExpense = MutableStateFlow(0)
    val thisMonthExpense: StateFlow<Int> = _thisMonthExpense

    private val _threeMonthExpense = MutableStateFlow(0)
    val threeMonthExpense: StateFlow<Int> = _threeMonthExpense

    private val _threeMonthAverage = MutableStateFlow(0.0)
    val threeMonthAverage: StateFlow<Double> = _threeMonthAverage

    private val _threeMonthExpenseTotal = MutableStateFlow(0.0)
    val threeMonthsExpenseTotal: StateFlow<Double> = _threeMonthExpenseTotal

    private val _yearlyExpense = MutableStateFlow(0)
    val yearlyExpense: StateFlow<Int> = _yearlyExpense

    private val _yearlyAverage = MutableStateFlow(0.0)
    val yearlyAverage: StateFlow<Double> = _yearlyAverage

    private val _yearlyExpenseTotal = MutableStateFlow(0.0)
    val yearlyExpenseTotal: StateFlow<Double> = _yearlyExpenseTotal


    private val _selectedRange = MutableStateFlow(0)
    val selectedRange : StateFlow<Int> = _selectedRange

    //----------------------------------------------------------------------//
    private val dateFormatterPattern = DateTimeFormatter.ofPattern("MMM d ,yyyy", Locale.ENGLISH)


    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = AnalyticsUiState.Loading
            try {
                val expense = repository.getExpenses()
                _uiState.value = AnalyticsUiState.Success(expense)
                chartData(expense)
                expenseCalculator(expense)
            } catch (e: Exception) {
                _uiState.value =
                    AnalyticsUiState.Error(e.localizedMessage ?: "Something went wrong on our side")
            }
        }
    }

    private fun chartData(expense: List<Expense>) {
        if (expense.isEmpty()) return

        val endMonth = YearMonth.now()
        val startMonth = endMonth.minusMonths(5)

        val months = (0L..5L).map { startMonth.plusMonths(it) }

        val byMonthTotals: Map<YearMonth, Double> = expense
            .mapNotNull { e ->
                try {
                    val d = LocalDate.parse(e.date, dateFormatterPattern)
                    YearMonth.from(d) to e.amount
                } catch (_: Exception) {
                    null // skip bad dates silently
                }
            }
            .filter { (ym, _) -> !ym.isBefore(startMonth) && !ym.isAfter(endMonth) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, amounts) -> amounts.sum() }

        val orderTotals = months.map { ym -> byMonthTotals[ym] ?: 0.0 }

        val points = orderTotals.map { total ->
            total.toFloat()
        }

        val labelFmt = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
        val labels = months.map { ym ->
            ym.atDay(1).format(labelFmt)
        }
        _chartPoints.value = points
        _xLabel.value = labels
    }

    private fun expenseCalculator(expense: List<Expense>) {

        val currentLocalDate = LocalDate.now()
        val currentMonth = currentLocalDate.month
        val currentYear = currentLocalDate.year
        val threeMonthsAgo = YearMonth.now().minusMonths(3)

        val filteredExpenseByMonth = expense.mapNotNull { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                if (date.month == currentMonth && date.year == currentYear) expense.amount
                else null
            } catch (_: Exception) {
                null
            }
        }.sum()


        val expenseCount = expense.count { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                date.month == currentMonth && date.year == currentYear
            } catch (_: Exception) {
                false
            }
        }
        _monthlyExpense.value = filteredExpenseByMonth
        _thisMonthExpense.value = expenseCount
        _monthlyDailyAverage.value = filteredExpenseByMonth / 30


        val filteredExpenseBy3Month = expense.mapNotNull { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                if (date.month == threeMonthsAgo.month && date.year == threeMonthsAgo.year) expense.amount
                else null
            } catch (_: Exception) {
                null
            }
        }.sum()

        val threeMonthExpense = expense.count { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                date.month == threeMonthsAgo.month && date.year == threeMonthsAgo.year
            } catch (_: Exception) {
                false
            }
        }

        _threeMonthExpenseTotal.value = filteredExpenseBy3Month
        _threeMonthExpense.value = threeMonthExpense
        _threeMonthAverage.value = filteredExpenseBy3Month / 92

        val filteredExpenseByYear = expense.mapNotNull { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                if (date.year == currentYear) expense.amount
                else null
            } catch (_: Exception) {
                null
            }
        }.sum()

        val oneYearExpense = expense.count{expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatterPattern)
                date.year == currentYear
            } catch (_: Exception) {
                false
            }
        }

        _yearlyExpense.value = oneYearExpense
        _yearlyExpenseTotal.value = filteredExpenseByYear
        _yearlyAverage.value = filteredExpenseByYear/365
    }

    fun updateSelectedRange(index : Int){
        _selectedRange.value = index
    }

    val activeTotal : StateFlow<Double> = selectedRange.map { range ->
        when(range){
            0 -> monthlyExpense.value
            1 -> threeMonthsExpenseTotal.value
            2 -> yearlyExpenseTotal.value
            else -> 0.0
        }
    }.stateIn(viewModelScope , SharingStarted.Eagerly,0.0)

    val activeAverage : StateFlow<Double> = selectedRange.map { range->
        when(range){
            0 -> monthlyDailyAverage.value
            1 -> threeMonthAverage.value
            2 -> yearlyAverage.value
            else -> 0.0
        }
    }.stateIn(viewModelScope , SharingStarted.Eagerly , 0.0)
    val activeCount : StateFlow<Int> = selectedRange.map { range ->
        when(range){
            0 -> thisMonthExpense.value
            1 -> threeMonthExpense.value
            2 ->yearlyExpense.value
            else -> 0
        }
    } . stateIn(viewModelScope , SharingStarted.Eagerly , 0)
}