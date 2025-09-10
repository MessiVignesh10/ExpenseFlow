package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    init {
        expenseData()
    }

    fun expenseData(){
        viewModelScope.launch {
            _uiState.value = AnalyticsUi.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = AnalyticsUi.Success(expenses)
                getExpense(expense = expenses)
            } catch (e : Exception){
                _uiState.value = AnalyticsUi.Error(e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }


    fun getExpense(expense: List<Expense>){
        
    }
}