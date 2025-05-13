package com.example.memematch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memematch.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_memematch),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )
        Button(onClick = { navController.navigate("request") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Enter your request")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("upload") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Find similar memes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("favorites") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Favorites")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("history") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "View History")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("about")}, modifier = Modifier.fillMaxWidth()) {
            Text(text = "About")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut() // Log the user out
                navController.navigate("login") { // Navigate back to LoginScreen
                    popUpTo("home") { inclusive = true } // Clear the back stack
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.log_out))
        }
    }
}
