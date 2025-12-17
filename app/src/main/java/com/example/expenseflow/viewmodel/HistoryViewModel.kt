package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.core.utils.localDate
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

sealed class HistoryState() {
    object Loading : HistoryState()
    data class Success(val expenses: List<Expense>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

class HistoryViewModel : ViewModel() {

    private val repository = TransactionsRepository()
    private val _uiState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val uiState: StateFlow<HistoryState> = _uiState

    private val _query = MutableStateFlow<String>("")
    val query: StateFlow<String> = _query

    private val _dropDownCategories = MutableStateFlow(listOf("Food" ,"Transport","Fun","Shopping","Health","Bills","Education","Travel","Other","All Categories"))
    val dropDownCategories : StateFlow<List<String>> = _dropDownCategories

    private val _selectedCategory = MutableStateFlow("All Categories")
    val selectedCategory : StateFlow<String> = _selectedCategory


    private val _sortOptions = MutableStateFlow(listOf("Date" ,"Amount", "None"))
    val sortOptions : StateFlow<List<String>> = _sortOptions

    private val _selectedSort = MutableStateFlow("None")
    val selectedSort : StateFlow<String> = _selectedSort

    private val _totalExpense = MutableStateFlow("0")
    val totalExpense : StateFlow<String> = _totalExpense

    private val _overallExpense = MutableStateFlow<String>("")
    val overallExpense : StateFlow<String> = _overallExpense

    private val _monthExpense = MutableStateFlow<String>("")
    val monthExpense : StateFlow<String> = _monthExpense



    init {
        loadExpenses()
    }
    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = HistoryState.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = HistoryState.Success(expenses)
                expenseOverview(expenses = expenses)
            } catch (e: Exception) {
                _uiState.value = HistoryState.Error(e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }

    fun onQueryChange(input : String){
        _query.value = input
    }

    fun onExpandedChange(input : String){
        _selectedCategory.value = input
    }

    fun onSortChange(input : String){
        _selectedSort.value = input
    }

    private fun expenseOverview(expenses: List<Expense>){
        expenses.forEach { expense ->
            _totalExpense.value = expenses.size.toString()
            val sum = expenses.sumOf { it.amount }
            _overallExpense.value = sum.toFloat().toString()

            val nowYM = YearMonth.now()
            val monthSum = expenses.asSequence().filter { YearMonth.from(it.localDate()) == nowYM }.sumOf { it.amount }

            _monthExpense.value = monthSum.toFloat().toString()
        }
    }
}

