package com.example.expenseflow.data.repository

import com.example.expenseflow.data.model.Expense
import com.example.expenseflow.data.remote.RetrofitInstance

class TransactionsRepository {
    suspend fun getExpenses() : List<Expense>{
        return RetrofitInstance.api.getExpenses()
    }
}