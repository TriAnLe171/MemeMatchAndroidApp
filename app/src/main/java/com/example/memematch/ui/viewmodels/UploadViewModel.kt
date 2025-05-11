package com.example.memematch.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memematch.ui.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class UploadViewModel : ViewModel() {
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var contextOption by mutableStateOf("Image-based")
    var topN by mutableStateOf(10)
    var uploadedMemes by mutableStateOf<List<String>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var contextOptionReal by mutableStateOf("global")

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun uploadImage(context: Context) {
        val uri = selectedImageUri ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    contextOptionReal = when (contextOption) {
                        "Image-based" -> "global"
                        "Text-based" -> "local"
                        else -> "global"
                    }

                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
                    val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
                    val contextPart = RequestBody.create("text/plain".toMediaTypeOrNull(), contextOptionReal)
                    val topNPart = RequestBody.create("text/plain".toMediaTypeOrNull(), topN.toString())

                    val response = RetrofitInstance.api.uploadMeme(contextPart, body, topNPart)
                    if (response.isSuccessful) {
                        uploadedMemes = response.body()?.memes ?: emptyList()

                        // Save upload data to Firestore
                        saveUploadToFirestore()
                    } else {
                        errorMessage = "Upload failed: ${response.code()}"
                    }
                } else {
                    errorMessage = "Failed to read the selected image."
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    private fun saveUploadToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(System.currentTimeMillis())

        val uploadData = hashMapOf(
            "uid" to uid,
            "author" to auth.currentUser?.email,
            "uploadDate" to formattedDate,
            "context" to contextOptionReal,
            "topN" to topN
        )
        firestore.collection("Upload").add(uploadData)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { e -> errorMessage = "Failed to save upload: ${e.localizedMessage}" }
    }
}