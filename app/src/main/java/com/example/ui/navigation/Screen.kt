package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Walk : Screen("walk")
    object Explore : Screen("explore")
    object Wallet : Screen("wallet")
    object Redeem : Screen("redeem")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
}
