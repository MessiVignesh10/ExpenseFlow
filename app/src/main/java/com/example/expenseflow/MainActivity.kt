package com.example.expenseflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.expenseflow.presentation.navigation.AppNavScreen
import com.example.expenseflow.ui.theme.ExpenseFlowTheme
import com.example.expenseflow.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    val authViewModel : AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseFlowTheme {
                val navController = rememberNavController()
                AppNavScreen(navController = navController , authViewModel = authViewModel)
            }
        }
    }
}