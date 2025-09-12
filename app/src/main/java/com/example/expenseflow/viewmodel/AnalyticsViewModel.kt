package com.example.expenseflow.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.exp

sealed class AnalyticsUi {
    data object Idle : AnalyticsUi()
    data object Loading : AnalyticsUi()
    data class Success(val expense: List<Expense>) : AnalyticsUi()
    data class Error(val message: String) : AnalyticsUi()
}

enum class TimeRange {
    THIS_MONTH, LAST_6_MONTHS, THIS_YEAR
}

data class CategoryBar(
    val name :String,
    val amount : Double,
    val percent : Double,
    val color: Color
)
class AnalyticsViewModel : ViewModel() {

    private val repository = TransactionsRepository()

    private val _uiState = MutableStateFlow<AnalyticsUi>(AnalyticsUi.Idle)
    val uiState: StateFlow<AnalyticsUi> = _uiState

    private val _range = MutableStateFlow<TimeRange>(TimeRange.THIS_MONTH)
    val range: StateFlow<TimeRange> = _range

    private val _allExpenses = MutableStateFlow<List<Expense>>(emptyList())

    private val apiDateFormat = DateTimeFormatter.ofPattern("MMM d ,yyyy", Locale.ENGLISH)

    val categoryBars : StateFlow<List<CategoryBar>> = combine(_allExpenses,_range){all,range ->
        val filtered = all.filterBy(range)
        toCategoryBars(expense = filtered)
    }.stateIn(scope = viewModelScope , started = SharingStarted.Eagerly , emptyList())


    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = AnalyticsUi.Loading
            try {
                val expenses = repository.getExpenses()
                _allExpenses.value = expenses
                _uiState.value = AnalyticsUi.Success(expense = expenses)
            } catch (e: Exception) {
                _uiState.value =
                    AnalyticsUi.Error(message = e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }

    fun setRange(newRange: TimeRange) {
        _range.value = newRange
    }

    private fun List<Expense>.filterBy(range : TimeRange) : List<Expense>{
        if (isEmpty()) return this
        val zone = ZoneId.systemDefault()
        val nowYM = YearMonth.now()
        val (start,end) = when(range){
            TimeRange.THIS_MONTH ->{
                val start = nowYM.atDay(1).atStartOfDay(zone).toInstant()
                val end = nowYM.atEndOfMonth().atTime(23,59,59).atZone(zone).toInstant()

                start to end
            }
            TimeRange.LAST_6_MONTHS ->{
                val startYM = nowYM.minusMonths(5)
                val start = startYM.atDay(1).atStartOfDay(zone).toInstant()
                val end = nowYM.atEndOfMonth().atTime(23,59,59).atZone(zone).toInstant()

                start to end
            }
            TimeRange.THIS_YEAR -> {
                val year = Year.now()
                val start = year.atMonth(1).atDay(1).atStartOfDay(zone).toInstant()
                val end = year.atMonth(12).atEndOfMonth().atTime(23,59,59).atZone(zone).toInstant()

                start to end
            }
        }

        return this.filter {t ->
            val inst = t.dateInstant()
            !inst.isBefore(start) && !inst.isAfter(end)
        }
    }

    private fun Expense.dateInstant():Instant{
        val d = LocalDate.parse(this.date.trim(),apiDateFormat)
        return d.atStartOfDay(ZoneId.systemDefault()).toInstant()
    }

    private fun toCategoryBars(expense: List<Expense>) : List<CategoryBar>{
        if (expense.isEmpty()) return emptyList()

        val totals = expense
            .groupBy { it.category.name }
            .mapValues { (_,list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        val totalAll = totals.sumOf { it.second }
        if (totalAll <=0.0) return emptyList()

        val palette = defaultPalette()

        return totals.mapIndexed { index, (name,total) ->
            CategoryBar(
                name = name,
                amount = total,
                percent = total/totalAll,
                color = palette[index %palette.size]
            )
        }
    }

    private fun defaultPalette() = listOf(
        Color(0xFF3B82F6), // blue
        Color(0xFF10B981), // green
        Color(0xFFF59E0B), // amber
        Color(0xFF8B5CF6), // violet
        Color(0xFFEF4444), // red
        Color(0xFF06B6D4), // cyan
        Color(0xFF84CC16), // lime
        Color(0xFFEC4899), // pink
        Color(0xFF6B7280)  // gray
    )

}