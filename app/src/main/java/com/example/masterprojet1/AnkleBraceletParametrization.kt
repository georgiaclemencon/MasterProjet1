package com.example.masterprojet1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class AnkleBraceletParametrization : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showAdditionalFields by remember { mutableStateOf(false) }
                    var showAdditionalFieldstwo by remember { mutableStateOf(false) }

                    var checkedState by remember { mutableStateOf(false) }
                    var checkedStatetwo by remember { mutableStateOf(false) }

                    var textField1Value by remember { mutableStateOf("") }
                    var textField2Value by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ankle Bracelet Parametrization",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (showAdditionalFields) {
                            Text(
                                text = "Cocher si vous voulez activer le buzzer",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Checkbox(
                                checked = checkedState,
                                onCheckedChange = { checkedState = it },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        if (showAdditionalFieldstwo) {
                            Text(
                                text = "Cocher si vous voulez activer le capteur de pulsation cardiaque",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Checkbox(
                                checked = checkedStatetwo,
                                onCheckedChange = { checkedStatetwo = it },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        Button(
                            onClick = { showAdditionalFields = !showAdditionalFields },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(if (showAdditionalFields) "Parametrization buzzer possible" else "Appuyer pour parametrer le buzzer")
                        }

                        Button(
                            onClick = { showAdditionalFieldstwo = !showAdditionalFieldstwo },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(if (showAdditionalFields) "Parametrization capteur de pulsation cardiaque possible" else "Appuyer pour parametrer le capteur de pulsation cardiaque")
                        }

                        Text(
                            text = "L'accélérometre est activé par défaut, obligatoire pour les différentes mesures",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MasterProjet1Theme {
        Greeting3("Android")
    }
}