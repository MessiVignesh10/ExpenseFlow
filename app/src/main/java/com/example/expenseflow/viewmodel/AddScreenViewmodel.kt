package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.core.utils.dateFormatter
import com.example.expenseflow.core.utils.formattedDescription
import com.example.expenseflow.data.model.Category
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.model.PaymentMethod
import com.example.expenseflow.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class AddScreenUiState(){
    object Idle : AddScreenUiState()
    object Loading : AddScreenUiState()
    data class Success (val expense : Expense) : AddScreenUiState()
    data class Error (val message : String) : AddScreenUiState()
}

class AddScreenViewmodel : ViewModel() {

    private val repository = TransactionsRepository()
    private val _uiState = MutableStateFlow<AddScreenUiState>(AddScreenUiState.Idle)
    val uiState : StateFlow<AddScreenUiState> = _uiState

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount


    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> = _description

    private val _date = MutableStateFlow<String>("")
    val date: StateFlow<String> = _date

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _selectedPaymentMethod = MutableStateFlow<String?>(null)
    val selectedPaymentMethod: StateFlow<String?> = _selectedPaymentMethod

    private val _validation = MutableStateFlow<Boolean>(false)
    val validation: StateFlow<Boolean> = _validation

    private fun validate() {
        _validation.value =
            _amount.value.isNotBlank() && _selectedCategory.value != null && _date.value.isNotBlank() && _selectedPaymentMethod.value !=null
    }

    fun onAmountChange(input: String) {
        _amount.value = input
        validate()
    }

    fun onDescriptionChange(input: String) {
        _description.value = formattedDescription(input = input)
    }

    fun onDatePicking(mills: Long) {
        _date.value = dateFormatter(input = mills)
        validate()
    }

    fun onCategorySelection(input: String) {
        _selectedCategory.value = input
        validate()
        println(input)

    }

    fun onPaymentSelection(input: String) {
        _selectedPaymentMethod.value = input
        validate()
        println(_selectedPaymentMethod.value)
    }

    fun onAddExpense(){
        _uiState.value = AddScreenUiState.Loading
        viewModelScope.launch {
            try {
                val expense = Expense(
                    id = "",
                    description = description.value,
                    amount = amount.value.toDouble(),
                    category = Category.fromLabel(selectedCategory.value ?: "Other"),
                    date = date.value,
                    paymentMethod = PaymentMethod.fromLabel(selectedPaymentMethod.value ?: "Cash")
                )
                println(expense)
                val created =repository.addExpense(expense = expense)
                println(created)
                _uiState.value = AddScreenUiState.Success(created)
                println(_uiState.value)
            }catch (e : Exception){
                _uiState.value = AddScreenUiState.Error(message = e.localizedMessage ?: "Unknown Error")
            }
        }
    }


}