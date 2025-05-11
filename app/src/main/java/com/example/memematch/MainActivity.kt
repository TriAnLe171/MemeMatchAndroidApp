package com.example.memematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeMatchTheme {
                var showSplashScreen by remember { mutableStateOf(true) }
                if (showSplashScreen) {
                    SplashScreen { showSplashScreen = false }
                } else {
                    // Main content after splash screen
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
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    val fullText = "Your AI Meme Recommendation Assistant"
    var displayedText by remember { mutableStateOf("") }

    // Scale animation for logo
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        isVisible = true
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = androidx.compose.animation.core.FastOutSlowInEasing // Use a valid easing function
            )
        )
        for (i in fullText.indices) {
            displayedText = fullText.substring(0, i + 1)
            delay(50) // Animate text appearance
        }
        delay(1000) // Wait before navigating
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_memematch),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale.value)
                )
                Text(
                    text = displayedText,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .offset(x = 0.dp, y = (-55).dp)
                )
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