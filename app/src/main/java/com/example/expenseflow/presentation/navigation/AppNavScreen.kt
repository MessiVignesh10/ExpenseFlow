package com.example.expenseflow.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expenseflow.R
import com.example.expenseflow.presentation.add.AddExpenseScreen
import com.example.expenseflow.presentation.dashboard.DashboardScreen
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
                NavState.AddExpenseScreen.route
            ),
            BottomNavItem(
                painterResource(id = R.drawable.history),
                "Recents",
                NavState.AddExpenseScreen.route
            )
        )
    val currentDestination =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination


    Scaffold(modifier.fillMaxSize(), bottomBar = {
        BottomAppBar(
            modifier.fillMaxWidth(),
            containerColor = Color.White,
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.route == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(painter = item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    alwaysShowLabel = true,
                    modifier = Modifier.size(30.dp),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = greenPrimary
                    )
                )
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavState.DashBoardScreen.route,
            modifier.padding(innerPadding)
        ) {
            composable(NavState.DashBoardScreen.route) {
                DashboardScreen(navController = navController)
            }
            composable(NavState.AddExpenseScreen.route) {
                AddExpenseScreen()
            }
        }
    }
}