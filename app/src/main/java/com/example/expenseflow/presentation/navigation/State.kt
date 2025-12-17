package com.example.expenseflow.presentation.navigation

sealed class NavState(val route : String){

    object LoginScreen : NavState("login")
    object DashBoardScreen : NavState("dashboard")
    object AddExpenseScreen : NavState("add_expense")
    object HistoryScreen : NavState("history")
    object AnalyticsScreen : NavState("analytics")
}