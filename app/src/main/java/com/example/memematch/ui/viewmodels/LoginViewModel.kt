package com.example.memematch.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)

    private val auth: FirebaseAuth = Firebase.auth

    fun registerUser(email: String, password: String) {
        loginUiState = LoginUiState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loginUiState = LoginUiState.RegisterSuccess
            }
            .addOnFailureListener { exception ->
                val errorMessage = when {
                    exception.localizedMessage?.contains("email address is already in use") == true ->
                        "The provided email address is already in use. Please sign in or register with a different email address."
                    exception.localizedMessage?.contains("weak-password") == true ->
                        "Password is too weak. Please use a stronger password."
                    exception.localizedMessage?.contains("A network error") == true ->
                        "Network error. Please check your internet connection."
                    else -> exception.localizedMessage ?: "Registration failed. Please try again or contact: triandole@gmail.com."
                }
                loginUiState = LoginUiState.Error(errorMessage)
            }
    }

    suspend fun loginUser(email: String, password: String): AuthResult? {
        loginUiState = LoginUiState.Loading
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                loginUiState = LoginUiState.LoginSuccess
            } else {
                loginUiState = LoginUiState.Error("Login failed. Please check your credentials.")
            }
            result
        } catch (exception: Exception) {
            val errorMessage = when {
                exception.localizedMessage?.contains("The supplied auth credential is incorrect") == true ->
                    "No account found with this email. Please register first."
                exception.localizedMessage?.contains("password is invalid") == true ->
                    "Incorrect password. Please try again."
                exception.localizedMessage?.contains("Given String is empty or null") == true ->
                    "Please enter your password!"
                exception.localizedMessage?.contains("A network error") == true ->
                    "Network error. Please check your internet connection."
                else -> exception.localizedMessage ?: "Login failed. Please try again or contact: triandole@gmail.com."
            }
            loginUiState = LoginUiState.Error(errorMessage)
            null
        }
    }
}

sealed interface LoginUiState {
    object Init: LoginUiState
    object Loading: LoginUiState
    object RegisterSuccess: LoginUiState
    object LoginSuccess: LoginUiState
    data class Error(val errorMessage: String?): LoginUiState
}

