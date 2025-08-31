package com.example.expenseflow.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue


object RetrofitInstance {
    val api  : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://68b33c58c28940c9e69e7d03.mockapi.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}