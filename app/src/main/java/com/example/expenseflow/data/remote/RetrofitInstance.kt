package com.example.expenseflow.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue


object RetrofitInstance {
    val api  : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://688f79a8f21ab1769f895381.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}