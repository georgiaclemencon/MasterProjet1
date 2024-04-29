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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class LEDsParamActivity : ComponentActivity() {
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
                        // Titre de la page
                        Text(
                            text = "LEDs Ribbon Parametrization",
                            //style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Sous-titre 1
                        Text(
                            text = "Pace",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(value = "", onValueChange = { /* Nothing to do */ })

                        // Sous-titre 2
                        Text(
                            text = "Duration",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        TextField(value = "", onValueChange = { /* Nothing to do */ })

                        Text(
                            text = "Distance",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        TextField(value = "", onValueChange = { /* Nothing to do */ })

                        // Ajoutez autant de sous-titres et de champs de texte que nécessaire de la même manière
                    }
                }
            }
        }
    }
}