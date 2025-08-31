package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddScreenState(){
    object Loading: AddScreenState()
    data class Success(val expenses : List<Expense>) : AddScreenState()
    data class Error(val message : String) : AddScreenState()
}

class AddScreenViewModel : ViewModel(){

    private val repository = TransactionsRepository()
    private val _uiState = MutableStateFlow<AddScreenState>(AddScreenState.Loading)
    val uiState : StateFlow<AddScreenState> = _uiState


    init {
        loadExpenses()
    }

     fun loadExpenses(){
        viewModelScope.launch {
            _uiState.value = AddScreenState.Loading
            try {
                val expenses = repository.getExpenses()
                _uiState.value = AddScreenState.Success(expenses)
            } catch (e : Exception){
                _uiState.value = AddScreenState.Error(e.localizedMessage ?: "Something Went Wrong")
            }
        }
    }
}