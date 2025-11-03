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
import java.time.format.DateTimeFormatter

sealed class AnalyticsUi() {

    object Idle : AnalyticsUi()
    object Loading : AnalyticsUi()
    data class Success(val expense: List<Expense>) : AnalyticsUi()
    data class Error(val message: String) : AnalyticsUi()
}

class AnalyticsViewModel : ViewModel() {

    private val repository = TransactionsRepository()
    private val _uiState = MutableStateFlow<AnalyticsUi>(AnalyticsUi.Idle)
    val uiState: StateFlow<AnalyticsUi> = _uiState

    private val _chartPoints = MutableStateFlow<List<Point>>(emptyList())
    val chartPoints: StateFlow<List<Point>> = _chartPoints

    private val _xLabels = MutableStateFlow<List<String>>(emptyList())
    val xLabel: StateFlow<List<String>> = _xLabels


    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.ENGLISH)

    init {
        loadExpense()
    }

    fun loadExpense() {
        viewModelScope.launch {
            _uiState.value = AnalyticsUi.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = AnalyticsUi.Success(expenses)
                mapToChart(expenses)
            } catch (e: Exception) {
                _uiState.value =
                    AnalyticsUi.Error(message = e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }

    private fun mapToChart(expense: List<Expense>){
        if (expense.isEmpty()){
            _chartPoints.value = emptyList()
            _xLabels.value = emptyList()
            return
        }

        val sorted = expense.sortedBy { parseLocalDate(it.date)}

        val labels = sorted.map { normalizeApiDate(it.date) }

        val points = sorted.mapIndexed {idx,expense ->
            Point(x = idx.toFloat() , y = expense.amount.toFloat())
        }

        _xLabels.value = labels
        _chartPoints.value = points
    }

    private fun normalizeApiDate(raw: String) : String {
        return  raw.replace("\s*,\s*".toRegex(), ", ").replace("\s+".toRegex(), " ").trim()
    }

    private fun parseLocalDate(raw : String) : LocalDate{
       return LocalDate.parse(normalizeApiDate(raw),dateFormatter)
    }

}
