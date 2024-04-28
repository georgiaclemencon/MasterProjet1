package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.R

@Composable
fun Masterprojet1Theme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Masterprojet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.soundpacerunnerslogo),
                            contentDescription = "App Icon",
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(44.dp))
                        Text(text = "Andoid Smart Device")
                        Spacer(modifier = Modifier.height(46.dp))

                        Text(
                            text = "Welcome to the MASTER PROJET APP. Click the button below to start scanning for nearby devices",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { startActivity() }) {
                            Text(text = "Start Scan")

                        }
                    }
                }
            }
        }
    }

    fun startActivity() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}