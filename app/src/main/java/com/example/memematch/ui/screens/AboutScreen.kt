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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.memematch.R

@Composable
fun AboutScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.about_memematch),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.memematch_is_designed_to_feel_fun_fast_and_effortless_like_chatting_with_a_meme_savvy_friend) +
                    stringResource(R.string.the_app_uses_natural_input_to_recommend_memes_that_match_your_mood_usage_context_or_topic_of_interest) +
                    stringResource(R.string.whether_you_re_looking_for_humor_motivation_or_just_a_meme_to_share_with_a_friend_memematch_helps_you_find_the_perfect_one_for_every_moment),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row {
            Text(
                text = stringResource(R.string.to_access_the_web_version_please_follow_this),
                fontSize = 14.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.link),
                fontSize = 14.sp,
                color = Color(0xFF42A5F5),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hugely-climbing-moray.ngrok-free.app/"))
                    context.startActivity(intent)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.memematch_developer),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        AboutCardView()
    }
}

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
                text = stringResource(R.string.tri_an_le),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.data_science_ai_enthusiast),
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Info
            ContactRow(stringResource(R.string.email), stringResource(R.string.triandole_gmail_com))
            ContactRow(stringResource(R.string.contact), stringResource(R.string._1_765_350_9132))
            ContactRow(stringResource(R.string.linkedin), stringResource(R.string.linkedin_com_in_trianle))

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
                    text = stringResource(R.string.version_1_0_0),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string.created_april_2025),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string._2025_memematch_all_rights_reserved),
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
