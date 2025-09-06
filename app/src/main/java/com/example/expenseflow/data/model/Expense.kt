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
    OTHER("other");

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
)