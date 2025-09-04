package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import com.example.expenseflow.core.utils.dateFormatter
import com.example.expenseflow.core.utils.formattedDescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AddScreenViewmodel : ViewModel() {

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


}