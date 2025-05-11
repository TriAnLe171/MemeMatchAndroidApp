package com.example.memematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memematch.ui.screens.*
import com.example.memematch.ui.theme.MemeMatchTheme
import com.example.memematch.ui.viewmodels.HistoryViewModel
import com.example.memematch.ui.viewmodels.RequestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeMatchTheme {
                val navController = rememberNavController()
                val historyViewModel: HistoryViewModel = viewModel() // Create shared HistoryViewModel
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavigation(
                        navController = navController,
                        historyViewModel = historyViewModel, // Pass shared HistoryViewModel
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    historyViewModel: HistoryViewModel, // Accept shared HistoryViewModel
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("request") {
            val requestViewModel: RequestViewModel = viewModel() // Create RequestViewModel
            RequestScreen(
                navController = navController,
                requestViewModel = requestViewModel, // Pass RequestViewModel
                historyViewModel = historyViewModel // Pass HistoryViewModel
            )
        }
        composable("upload") { UploadScreen(navController) }
        composable("history") { HistoryScreen(navController, historyViewModel) }
        composable("about") { AboutScreen(navController) }
        composable("favorites") { FavoritesScreen(navController) }
    }
}
