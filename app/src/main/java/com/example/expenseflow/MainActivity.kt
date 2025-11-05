package com.example.expenseflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.expenseflow.presentation.navigation.AppNavScreen
import com.example.expenseflow.ui.theme.ExpenseFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseFlowTheme {
                val navController = rememberNavController()
                AppNavScreen(navController = navController)
            }
        }
    }
}