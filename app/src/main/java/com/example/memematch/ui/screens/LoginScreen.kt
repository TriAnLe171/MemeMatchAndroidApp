package com.example.memematch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memematch.R
import com.example.memematch.ui.viewmodels.LoginUiState
import com.example.memematch.ui.viewmodels.LoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("demo@gmail.com") }
    var password by rememberSaveable { mutableStateOf("abcdefg") }
    val coroutineScope = rememberCoroutineScope()

    Box {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_memematch_highres),
                contentDescription = "Logo",
                modifier = Modifier.size(55.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MEMEMATCH",
                fontSize = 45.sp
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = { Text(text = "E-mail") },
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = { Text(text = "Password") },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Info, null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.Add, null)
                        } else {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        val result = viewModel.loginUser(email, password)
                        if (result?.user != null) {
                            onLoginSuccess()
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }) {
                    Text(text = "Login")
                }
                OutlinedButton(onClick = {
                    viewModel.registerUser(email, password)
                }) {
                    Text(text = "Register")
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = viewModel.loginUiState) {
                is LoginUiState.Init -> {}
                is LoginUiState.Error -> {
                    Text(
                        text = state.errorMessage ?: "An unknown error occurred",
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.LoginSuccess -> {
                    Text(
                        "Login successful!",
                        color = androidx.compose.ui.graphics.Color.Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                is LoginUiState.RegisterSuccess -> {
                    Text(
                        "Registration successful! You can now log in.",
                        color = androidx.compose.ui.graphics.Color.Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
