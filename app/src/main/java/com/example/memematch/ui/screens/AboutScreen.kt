package com.example.memematch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.memematch.R

@Composable
fun AboutScreen(
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About MemeMatch",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "MemeMatch is designed to feel fun, fast, and effortless — like chatting with a meme-savvy friend. " +
                    "The app uses natural input to recommend memes that match your mood, usage context, or topic of interest. " +
                    "Whether you're looking for humor, motivation, or just a meme to share with a friend, MemeMatch helps you find the perfect one for every moment.",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Divider()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MemeMatch Developer",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        AboutCardView()
    }}

@Composable
fun AboutCardView() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title and subtitle
            Text(
                text = "Tri An Le",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Data Science & AI Enthusiast",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Info
            ContactRow("Email:", "triandole@gmail.com")
            ContactRow("Contact:", "+1 765 350 9132")
            ContactRow("LinkedIn:", "linkedin.com/in/trianle")

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Version 1.0.0",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Created: April 2025",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "© 2025 MEMEMATCH. All rights reserved.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ContactRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            color = Color(0xFF42A5F5),
            modifier = Modifier.clickable(enabled = label == "LinkedIn:") {
                // handle link click
            }
        )
    }
}
