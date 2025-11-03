package com.example.expenseflow.data.model

import androidx.annotation.DrawableRes
import com.example.expenseflow.R

enum class Category(val label : String , @DrawableRes val icon : Int) {
    FOOD("food", R.drawable.cutlery),
    TRANSPORT("transport",R.drawable.sportcar),
    FUN("fun",R.drawable.gamecontroller),
    SHOPPING("shopping",R.drawable.onlineshopping),
    HEALTH("health",R.drawable.healthcare),
    BILLS("bills",R.drawable.bolt),
    EDUCATION("education",R.drawable.graduation),
    TRAVEL("travel",R.drawable.plane),
    OTHER("other",R.drawable.threedots);

    companion object{
        fun fromLabel(label: String) : Category{
            return entries.find {it.label == label }?:OTHER
        }

    }
}

enum class PaymentMethod(val label : String) {
    CASH("cash"),
    CARD("card"),
    DIGITAL("digital");

    companion object{
        fun fromLabel(label: String): PaymentMethod{
            return entries.find { it.label == label } ?: CASH
        }
    }
}
data class Expense(
    val id : String ?= null,
    val amount : Double,
    val category : Category,
    val date : String,
    val description : String,
    val paymentMethod : PaymentMethod,
    val createdAt : Number ?= System.currentTimeMillis()
)