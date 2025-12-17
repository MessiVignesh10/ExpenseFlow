package com.example.expenseflow.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.expenseflow.R
import com.example.expenseflow.presentation.add.AddExpenseScreen
import com.example.expenseflow.presentation.analytics.AnalyticsScreen
import com.example.expenseflow.presentation.dashboard.DashboardScreen
import com.example.expenseflow.presentation.history.HistoryScreen
import com.example.expenseflow.presentation.login.LoginPage
import com.example.expenseflow.ui.theme.greenPrimary

data class BottomNavItem(
    val icon: Painter,
    val label: String,
    val route: String
)

@Composable
fun AppNavScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val bottomNavItems =
        listOf(
            BottomNavItem(
                painterResource(id = R.drawable.home),
                "Home",
                NavState.DashBoardScreen.route
            ),
            BottomNavItem(
                painterResource(id = R.drawable.add),
                "Add",
                NavState.AddExpenseScreen.route
            ),
            BottomNavItem(
                painterResource(id = R.drawable.analysis),
                "Analysis",
                NavState.AnalyticsScreen.route
            ),
            BottomNavItem(
                painterResource(id = R.drawable.history),
                "Recents",
                NavState.HistoryScreen.route
            )
        )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }


    Scaffold(modifier = modifier.fillMaxSize(), bottomBar = {
        if (showBottomBar) {
            BottomAppBar(
                containerColor = Color.White,
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.route == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = greenPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavState.LoginScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavState.LoginScreen.route) {
                LoginPage(onLoginSuccess = { navController.navigate(NavState.DashBoardScreen.route) })
            }
            composable(NavState.DashBoardScreen.route) {
                DashboardScreen(navController = navController)
            }
            composable(NavState.AddExpenseScreen.route) {
                AddExpenseScreen(navController = navController)
            }
            composable(NavState.HistoryScreen.route) {
                HistoryScreen()
            }
            composable(NavState.AnalyticsScreen.route) {
                AnalyticsScreen()
            }
        }
    }
}