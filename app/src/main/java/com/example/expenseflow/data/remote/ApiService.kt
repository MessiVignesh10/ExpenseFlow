package com.example.expenseflow.data.remote

import com.example.expenseflow.data.model.Expense
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("expenses")
    suspend fun getExpenses(): List<Expense>

    @POST("expenses")
    suspend fun addExpense(@Body expense: ExpenseDto) : ExpenseDto

}