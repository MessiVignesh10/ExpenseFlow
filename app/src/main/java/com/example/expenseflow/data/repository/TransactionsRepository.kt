package com.example.expenseflow.data.repository

import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.remote.ExpenseDto
import com.example.expenseflow.data.remote.RetrofitInstance
import com.example.expenseflow.data.remote.toDomain
import com.example.expenseflow.data.remote.toDto

class TransactionsRepository {
    suspend fun getExpenses() : List<Expense>{
        return RetrofitInstance.api.getExpenses()
            .map { it.toDomain() }

    }
    suspend fun addExpense(expense: Expense) : Expense{
        val dto = expense.toDto()
        val createdDto = RetrofitInstance.api.addExpense(dto)
        return createdDto.toDomain()
    }
}