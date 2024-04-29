package com.example.masterprojet1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class CreatorsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Présentation de notre équipe et explication de la raison de notre projet",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Qui sommes-nous ?",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Pourquoi ce projet ?",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Nos ambitions et objectifs",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}