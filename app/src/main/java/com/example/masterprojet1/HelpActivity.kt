package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class HelpActivity : ComponentActivity() {
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
                                    onClick = { val intent = Intent(context, DeviceActivity_LEDs::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Help") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, HelpActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Who are we ? What is the SoundPaceRunners") },
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
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item{
                                Text(
                                    text = "Help with the application and use of our products",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item {
                                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                            }
                            item{
                                Spacer(modifier = Modifier.height(16.dp)) // Espace vertical de 16dp
                            }
                            item{
                                Text(
                                    text = "The Ankle Bracelet",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item{
                                Text(
                                    text = "Place the bracelet on your ankle, taking care to position it correctly so that the green light is against your skin. Tighten the bracelet securely.\n" + "The settings for the different devices of the ankle bracelet will be available in the application\n",
                                    //textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                            item{
                                Text(
                                    text = "The LEDs Ribbon",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item{
                                Text(
                                    text = "The LED ribbon can already be positioned on the track. If not, position it on the inside edge of the track, as close as possible to the inside edge.\n" +
                                            "The settings for the LED ribbon will be available in the application\n",
                                    //textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                            item{
                                Text(
                                    text = "The Mobile Application",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item{
                                Text(
                                    text = "Different pages are available on the application:\n\n" +
                                            "- Home page: page where you can click on the start your workout button to have the data from your workout recorded by the various sensors, and this page will display the data from your last workout.\n\n" +
                                            "- History page: this is where you will find your previous training data\n\n" +
                                            "- LEDs ribbon Settings page: this is where you can set the parameters for the led ribbon. you can set the speed at which you want to train, the distance you want to cover and/or the time you want to run for. the led ribbon will then help you to do your training as well as possible.\n\n" +
                                            "- Ankle bracelet Settings page: this is where you can set the parameters of the ankle bracelet: you can activate or deactivate the buzzer and heart rate sensor, and decide whether or not you want them to accompany you during your training session.\n\n" +
                                            "- About us page: presentation of our team, and presentation of our project and its context\n\n" +
                                            "- Contact form page: to contact us with problems, questions or recommendations\n\n",
                                    //textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}