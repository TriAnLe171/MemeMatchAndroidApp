package com.example.memematch.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class ForgotPasswordViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun sendPasswordResetEmail(email: String): String? {
        return try {
            auth.sendPasswordResetEmail(email).await()
            null // Success
        } catch (e: Exception) {
            e.localizedMessage ?: "An unexpected error occurred while trying to reset your password."
        }
    }
}
