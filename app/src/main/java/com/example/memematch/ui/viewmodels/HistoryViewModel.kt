package com.example.memematch.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser

class HistoryViewModel : ViewModel() {
    var history by mutableStateOf<List<String>>(emptyList())
        private set

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                loadHistoryFromFirestore(currentUser)
            } else {
                clearLocalHistory()
            }
        }
    }

    fun addQuery(query: String) {
        history = history + query
        saveQueryToFirestore(query)
    }

    fun clearHistory() {
        history = emptyList()
        clearHistoryFromFirestore()
    }

    private fun loadHistoryFromFirestore(user: FirebaseUser) {
        val uid = user.uid
        firestore.collection("Users")
            .document(uid)
            .collection("History")
            .get()
            .addOnSuccessListener { documents ->
                history = documents.mapNotNull { it.getString("query") }
            }
            .addOnFailureListener { e ->
                // Handle failure (e.g., log the error)
            }
    }

    private fun saveQueryToFirestore(query: String) {
        val uid = auth.currentUser?.uid ?: return
        val queryData = hashMapOf("query" to query)
        firestore.collection("Users")
            .document(uid)
            .collection("History")
            .add(queryData)
            .addOnFailureListener { e ->
                // Handle failure (e.g., log the error)
            }
    }

    private fun clearHistoryFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users")
            .document(uid)
            .collection("History")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure (e.g., log the error)
            }
    }

    private fun clearLocalHistory() {
        history = emptyList()
    }
}
