package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userInput = intent.getStringExtra("userInput")

                    ProfileScreen(
                        navigateFunction = { destinationActivity ->
                            navigateToNextScreen(destinationActivity)
                        },
                        text = userInput ?: ""
                    )
                }
            }
        }
    }
    private fun navigateToNextScreen(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
        finish()
    }
}


@Composable
fun ProfileScreen(navigateFunction: (Class<*>) -> Unit, text: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp)
            )
            ClickableText(
                text = AnnotatedString("Edit Profile"),
                onClick = {
                    navigateFunction(ModifyProfileActivity::class.java)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}