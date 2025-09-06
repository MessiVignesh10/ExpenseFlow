package com.example.expenseflow.presentation.navigation

sealed class NavState(val route : String){
    object DashBoardScreen : NavState("dashboard")
    object AddExpenseScreen : NavState("add_expense")
    object HistoryScreen : NavState("history")
}