package com.example.memematch.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.memematch.R
import com.example.memematch.ui.viewmodels.FavoritesViewModel
import com.example.memematch.ui.viewmodels.HistoryViewModel
import com.example.memematch.ui.viewmodels.RequestViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Composable
fun RequestScreen(
    navController: NavController,
    requestViewModel: RequestViewModel = viewModel(),
    historyViewModel: HistoryViewModel,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Enter Your Request", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = requestViewModel.query,
            onValueChange = { requestViewModel.updateQuery(it) },
            label = { Text("Please describe your need here...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DropdownSelector(
                label = "Number of Memes",
                options = listOf(10, 20, 50),
                selected = requestViewModel.topN,
                onSelectedChange = { requestViewModel.topN = it }
            )

            DropdownSelector(
                label = "Number of Meme Templates",
                options = listOf(5, 10, 20),
                selected = requestViewModel.topNTemplate,
                onSelectedChange = { requestViewModel.topNTemplate = it }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (requestViewModel.query.isBlank()) {
                    requestViewModel.updateErrorMessage("Please enter your request!")
                } else {
                    requestViewModel.fetchMemes()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Memes")
        }

        requestViewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LaunchedEffect(Unit) {
            requestViewModel.historyViewModel = historyViewModel
        }

        if (requestViewModel.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(requestViewModel.memes.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                ) {
                    Column {
                        Image(
                            painter = rememberAsyncImagePainter(requestViewModel.memes[index]),
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
                                    if (favoritesViewModel.isFavorite(requestViewModel.memes[index])) {
                                        favoritesViewModel.removeFavorite(requestViewModel.memes[index])
                                    } else {
                                        favoritesViewModel.addFavorite(requestViewModel.memes[index])
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (favoritesViewModel.isFavorite(requestViewModel.memes[index])) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Filled.FavoriteBorder
                                    },
                                    contentDescription = "Favorite",
                                    tint = Color.Red
                                )
                            }
                            IconButton(onClick = {
                                favoritesViewModel.showShareDialog(requestViewModel.memes[index])
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

@Composable
fun ShareDialog(memeUrl: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Meme", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text(
                    "Choose a platform to share this meme:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShareButton("Facebook", R.drawable.ic_facebook) {
                        shareImageToPlatform(context, memeUrl, "com.facebook.katana")
                    }
                    ShareButton("Instagram", R.drawable.ic_instagram) {
                        shareImageToPlatform(context, memeUrl, "com.instagram.android")
                    }
                    ShareButton("X", R.drawable.ic_x) {
                        shareImageToPlatform(context, memeUrl, "com.twitter.android")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ShareButton(platformName: String, iconRes: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = platformName,
            modifier = Modifier.size(48.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(platformName, style = MaterialTheme.typography.bodySmall)
    }
}

fun shareImageToPlatform(context: Context, imageUrl: String, packageName: String?) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val input = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(input)

            val file = File(context.cacheDir, "images")
            file.mkdirs()
            val imageFile = File(file, "shared_meme.png")

            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                packageName?.let { setPackage(it) }
            }

            withContext(Dispatchers.Main) {
                try {
                    context.startActivity(shareIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<Int>,
    selected: Int,
    onSelectedChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column() {
        Text(label)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = selected.toString())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value.toString()) },
                        onClick = {
                            onSelectedChange(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
