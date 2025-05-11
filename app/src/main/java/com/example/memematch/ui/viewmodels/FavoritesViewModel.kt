package com.example.memematch.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesViewModel : ViewModel() {
    var favorites by mutableStateOf<List<String>>(emptyList())
        private set

    var isShareDialogVisible by mutableStateOf(false)
        private set

    var shareMemeUrl by mutableStateOf("")

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadFavoritesFromFirestore()
    }

    fun addFavorite(memeUrl: String) {
        favorites = favorites + memeUrl
        saveFavoriteToFirestore(memeUrl)
    }

    fun removeFavorite(memeUrl: String) {
        favorites = favorites - memeUrl
        removeFavoriteFromFirestore(memeUrl)
    }

    fun isFavorite(memeUrl: String): Boolean {
        return favorites.contains(memeUrl)
    }

    fun clearFavorites() {
        favorites = emptyList()
        clearFavoritesFromFirestore()
    }

    private fun loadFavoritesFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users")
            .document(uid)
            .collection("Favorites")
            .get()
            .addOnSuccessListener { documents ->
                favorites = documents.mapNotNull { it.getString("memeUrl") }
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    private fun saveFavoriteToFirestore(memeUrl: String) {
        val uid = auth.currentUser?.uid ?: return
        val favoriteData = hashMapOf("memeUrl" to memeUrl)
        firestore.collection("Users")
            .document(uid)
            .collection("Favorites")
            .add(favoriteData)
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    private fun removeFavoriteFromFirestore(memeUrl: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users")
            .document(uid)
            .collection("Favorites")
            .whereEqualTo("memeUrl", memeUrl)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    private fun clearFavoritesFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users")
            .document(uid)
            .collection("Favorites")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    fun showShareDialog(memeUrl: String) {
        shareMemeUrl = memeUrl
        isShareDialogVisible = true
    }

    fun hideShareDialog() {
        isShareDialogVisible = false
    }
}