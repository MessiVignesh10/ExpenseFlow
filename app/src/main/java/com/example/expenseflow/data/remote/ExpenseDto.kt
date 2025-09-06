package com.example.expenseflow.data.remote

import com.example.expenseflow.data.model.Category
import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.model.PaymentMethod

data class ExpenseDto(
    val id : String ?= null,
    val amount : Double,
    val category : String,
    val date: String,
    val description : String,
    val paymentMethod : String
)

fun ExpenseDto.toDomain(): Expense = Expense(
    id = id,
    amount = amount,
    category = category.toCategory(),
    date = date,
    description = description,
    paymentMethod = paymentMethod.toPaymentMethod()
)

fun Expense.toDto(): ExpenseDto = ExpenseDto(
    id = id,
    amount = amount,
    category = category.label,
    date = date,
    description = description,
    paymentMethod = paymentMethod.label
)


fun String.toCategory() : Category = Category.entries.firstOrNull {
    it.label == this
} ?: Category.OTHER

fun String.toPaymentMethod() : PaymentMethod = PaymentMethod.entries.firstOrNull() {
    it.label == this
} ?: PaymentMethod.CASH