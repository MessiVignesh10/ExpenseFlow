package com.example.expenseflow.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("expenses")
    suspend fun getExpenses(
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("order") sortOrder: String = "desc"
    ): List<ExpenseDto>

    @POST("expenses")
    suspend fun addExpense(@Body expense: ExpenseDto): ExpenseDto
}