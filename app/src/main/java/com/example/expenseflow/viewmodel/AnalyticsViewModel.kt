package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


sealed class AnalyticsUiState(){
    object Idle : AnalyticsUiState()
    object Loading : AnalyticsUiState()
    data class Success(val expense : List<Expense>) : AnalyticsUiState()
    data class Error(val message : String) : AnalyticsUiState()
}

class AnalyticsViewModel : ViewModel() {
    private val repository = TransactionsRepository()

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Idle)
    val uiState : StateFlow<AnalyticsUiState> = _uiState

    private val _chartPoints = MutableStateFlow<List<Point>>(emptyList())
    val chartPoints : StateFlow<List<Point>> = _chartPoints

    private val _xLabel = MutableStateFlow<List<String>>(emptyList())
    val xLabel : StateFlow<List<String>> = _xLabel

    private val dateFormatterPattern = DateTimeFormatter.ofPattern("MMM d ,yyyy", Locale.ENGLISH)


    init {

    }

    fun loadExpenses(){
        viewModelScope.launch {
            _uiState.value = AnalyticsUiState.Loading
            try {
                val expense = repository.getExpenses()
                _uiState.value = AnalyticsUiState.Success(expense)
                chartData(expense)
            }catch (e : Exception){
                _uiState.value = AnalyticsUiState.Error(e.localizedMessage ?: "Something went wrong on our side")
            }
        }
    }

    private fun chartData(expense: List<Expense>){
        if (expense.isEmpty()) return

        val endMonth = YearMonth.now()
        val startMonth = endMonth.minusMonths(5)

        val months = (0L .. 5L).map { startMonth.plusMonths(it) }

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

        val orderTotals = months.map { ym -> byMonthTotals[ym] ?:0.0 }

        val points = orderTotals.mapIndexed { idx,total ->
            Point(x=idx.toFloat(),y=total.toFloat())
        }

        val labelFmt = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
        val labels = months.map { ym ->
            ym.atDay(1).format(labelFmt)
        }
        _chartPoints.value = points
        _xLabel.value = labels
    }
}