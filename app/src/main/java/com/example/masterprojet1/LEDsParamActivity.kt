package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
                    var context = LocalContext.current
                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Text("SPR MENU", modifier = Modifier.padding(16.dp))
                                Divider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Profile") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ProfileActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Historique") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, DeviceActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Ankle Bracelet Parametrization") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, AnkleBraceletParametrization::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "LEDs Ribbon Parametrization") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, LEDsParamActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Help") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, HelpActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Who are we ? What is SoundPaceRunners ?") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, CreatorsActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Contact Form") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ContactForm::class.java)
                                        startActivity(intent) }
                                )
                            }
                        }
                    ){
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

                            Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxWidth())

                            Spacer(modifier = Modifier.height(110.dp))

                            // Sous-titre 1
                            Text(
                                text = "Pace",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            TextField(
                                value = "",
                                onValueChange = { },
                                label = { Text("Pace") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            // Sous-titre 2
                            Text(
                                text = "Duration",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            TextField(
                                value = "",
                                onValueChange = { },
                                label = { Text("Duration") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            Text(
                                text = "Distance",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            TextField(
                                value = "",
                                onValueChange = { },
                                label = { Text("Distance") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(110.dp))

                            Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    Toast.makeText(
                                        applicationContext,"Data sent",Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(0.8f) // Utilisation du poids pour spécifier la largeur
                            ) {
                                Text(text = "Send parameters")
                            }
                        }
                    }

                }
            }
        }
    }
}