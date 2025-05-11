package com.example.memematch.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memematch.ui.network.MemeResponse
import com.example.memematch.ui.network.QueryRequest
import com.example.memematch.ui.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RequestViewModel : ViewModel() {
    var query by mutableStateOf("")
    var memes by mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var topN by mutableStateOf(10)
    var topNTemplate by mutableStateOf(5)

    var historyViewModel: HistoryViewModel? = null // Reference to shared HistoryViewModel

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun fetchMemes() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRecommendations(
                    QueryRequest(query = query, top_n = topN, top_n_template = topNTemplate)
                )
                if (response.isSuccessful) {
                    memes = response.body()?.memes ?: emptyList()
                    historyViewModel?.addQuery(query) // Add query to shared HistoryViewModel

                    // Save query to Firestore
                    saveRequestToFirestore()

                    // Save memes to Firestore
                    saveMemesToFirestore(response.body())
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load memes: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun saveRequestToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(System.currentTimeMillis())

        val requestData = hashMapOf(
            "uid" to uid,
            "author" to auth.currentUser?.email,
            "request" to query,
            "topN" to topN,
            "topNTemplate" to topNTemplate,
            "requestDate" to formattedDate
        )
        firestore.collection("Request").add(requestData)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { e -> errorMessage = "Failed to save request: ${e.localizedMessage}" }
    }

    private fun saveMemesToFirestore(response: MemeResponse?) {
        val uid = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(System.currentTimeMillis())
        // Ensure details is properly serialized
        val detailsMap = response?.details?.mapValues { it.value.toString() } ?: emptyMap()

        // Prepare the meme data
        val memeData = hashMapOf(
            "uid" to uid,
            "author" to auth.currentUser?.email,
            "requestDate" to formattedDate,
            "query" to query,
            "memes" to (response?.memes ?: emptyList()), // Firestore supports lists directly
            "need_template" to (response?.need_template ?: false),
            "details" to detailsMap // Ensure details is a Map<String, String>
        )

        // Save to Firestore
        firestore.collection("Meme").add(memeData)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { e -> errorMessage = "Failed to save memes: ${e.localizedMessage}" }
    }

    fun updateQuery(newQuery: String) {
        query = newQuery
    }

    fun updateErrorMessage(message: String?) {
        errorMessage = message
    }
}
