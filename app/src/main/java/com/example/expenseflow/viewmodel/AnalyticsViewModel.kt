package com.example.expenseflow.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp

sealed class AnalyticsUi(){
    object Idle : AnalyticsUi()
    object Loading : AnalyticsUi()
    data class Success (val expense: List<Expense>) : AnalyticsUi()
    data class Error (val message : String) : AnalyticsUi()
}



class AnalyticsViewModel : ViewModel(){

    private val repository = TransactionsRepository()

    private val _uiState = MutableStateFlow<AnalyticsUi>(AnalyticsUi.Idle)
    val uiState : StateFlow<AnalyticsUi> = _uiState

    private val _pieData = MutableStateFlow<List<Pie>>(emptyList())
    val pieDate : StateFlow<List<Pie>> = _pieData

    init {
        expenseData()
    }

    fun expenseData(){
        viewModelScope.launch {
            _uiState.value = AnalyticsUi.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = AnalyticsUi.Success(expenses)
                computePie(expense = expenses)
            } catch (e : Exception){
                _uiState.value = AnalyticsUi.Error(e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }


    private fun computePie(expense: List<Expense>){

        if (expense.isEmpty()) {
            _pieData.value = emptyList()
            return
        }

        val totalByCategory : Map<String , Double> =
            expense.groupBy { it.category.name }
                .mapValues { (_,list) -> list.sumOf { abs(it.amount) }}

        val totalAll = totalByCategory.values.sum()

        if (totalAll <= 0.0){
            _pieData.value = emptyList()
            return
        }

        val pallette = defaultPallette()

        val pies = totalByCategory.entries
            .sortedByDescending { it.value }
            .mapIndexed { index, (name , total) ->
                val ptc = (total/totalAll)*100.0
                val color = pallette[index % pallette.size]
                Pie(label = name , data = total , color = color , selectedColor = color , selected = false)
            }
        _pieData.value = pies

    }

    private fun defaultPallette() = listOf(
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