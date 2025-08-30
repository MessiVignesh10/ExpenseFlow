package com.example.expenseflow.data.model

enum class Category(val label : String) {
    FOOD("food"),
    TRANSPORT("transport"),
    FUN("fun"),
    SHOPPING("shopping"),
    HEALTH("health"),
    BILLS("bills"),
    EDUCATION("education"),
    TRAVEL("travel"),
    OTHER("other"),
}

enum class PaymentMethod(val label : String) {
    CASH("cash"),
    CARD("card"),
    DIGITAL("digital")
}
data class Expense(
    val id : String ?= null,
    val amount : Double,
    val category : Category,
    val date : String,
    val description : String,
    val paymentMethod : PaymentMethod,
)