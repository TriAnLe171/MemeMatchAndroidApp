package com.example.memematch.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memematch.ui.viewmodels.UploadViewModel
import com.example.memematch.ui.viewmodels.FavoritesViewModel
import kotlinx.coroutines.launch

@Composable
fun UploadScreen(
    navHostController: NavHostController,
    viewModel: UploadViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel() // Add FavoritesViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectedImageUri = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Find Similar Memes", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Upload Image")
        }

        viewModel.selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Search Preference")
                var expanded by remember { mutableStateOf(false) }
                val contextOptions = listOf("Image-based", "Text-based")
                // Image-based = 'global' and Text-based = 'local'

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(viewModel.contextOption.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        contextOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() }) },
                                onClick = {
                                    viewModel.contextOption = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Number of Results")
                val topOptions = listOf(10,20,50)
                var topExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { topExpanded = true }) {
                        Text(viewModel.topN.toString())
                    }
                    DropdownMenu(expanded = topExpanded, onDismissRequest = { topExpanded = false }) {
                        topOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it.toString()) },
                                onClick = {
                                    viewModel.topN = it
                                    topExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.uploadImage(context) },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.selectedImageUri != null && !viewModel.isLoading
        ) {
            Text("Find similar memes")
        }

        if (viewModel.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        viewModel.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        if (viewModel.uploadedMemes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recommended Memes", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(viewModel.uploadedMemes.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        Column {
                            Image(
                                painter = rememberAsyncImagePainter(viewModel.uploadedMemes[index]),
                                contentDescription = "Meme",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        if (favoritesViewModel.isFavorite(viewModel.uploadedMemes[index])) {
                                            favoritesViewModel.removeFavorite(viewModel.uploadedMemes[index])
                                        } else {
                                            favoritesViewModel.addFavorite(viewModel.uploadedMemes[index])
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (favoritesViewModel.isFavorite(viewModel.uploadedMemes[index])) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Filled.FavoriteBorder
                                        },
                                        contentDescription = "Favorite",
                                        tint = Color.Red
                                    )
                                }
                                IconButton(onClick = {
                                    favoritesViewModel.showShareDialog(viewModel.uploadedMemes[index])
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Share",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (favoritesViewModel.isShareDialogVisible) {
            ShareDialog(
                memeUrl = favoritesViewModel.shareMemeUrl,
                onDismiss = { favoritesViewModel.hideShareDialog() }
            )
        }
    }
}
