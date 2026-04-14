package com.abood.wateredition.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abood.wateredition.presentation.ui.screens.CustomerDetailScreen
import com.abood.wateredition.presentation.ui.screens.CustomerListScreen
import com.abood.wateredition.presentation.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    object Splash         : Screen("splash")
    object CustomerList   : Screen("customer_list")
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
}

@Composable
fun WaterNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToList = {
                    navController.navigate(Screen.CustomerList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CustomerList.route) {
            CustomerListScreen(
                onCustomerClick = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                }
            )
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(
                navArgument("customerId") { type = NavType.LongType }
            )
        ) {
            CustomerDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
