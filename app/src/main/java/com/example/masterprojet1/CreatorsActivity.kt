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
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = "Presentation of our team and of our project",
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
                            item {
                                Text(
                                    text = "Who are we ?",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item {
                                Text(
                                    text = "We are four Master 1 students at the ISEN engineering school in Toulon: Georgia, Jade, Jules and Titouan.\n" +
                                            "Our academic year consists of a technical project that we have to complete from start to finish, from September to May. We've decided to do a project based around sport, an area we're all passionate about, which we've called SoundPace Runners.\n",
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                            item {
                                Text(
                                    text = "What SoundPace Runners is ?",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item {
                                Text(
                                    text = "Our equipment can be used by sportsmen and women who want support in their training, whether they are amateurs or professionals. \n" +
                                            "Trainers will also be able to use it to visualise the performance of their athletes, so that they can be helped with future training, and adapt it more effectively to their shape and capacity.\n" +
                                            "Our equipment can be used and implemented in all sports where there is a need to measure speed, acceleration or position, or even athlete’s impact and heart rate.\n" + "As we said earlier, our equipment will consist of three different devices. The two main ones will be the wristband that athletes will attach to their ankle, and the mobile application. The third will be an LED device, a ribbon of LEDs, connected to the application on the mobile phone via Bluetooth with an ESP32 electronic card.\n" +
                                            "The bracelet will then be made up of various sensors like one accelerometer/gyrometer, one buzzer, and one heartbeat sensor, connected to a microcontroller PCB to receive the information and send it to the phone using BLE technology.\n" +
                                            "Finally, the application will contain information from all the sensors, and will be able to control how the LEDs light up and how the buzzer will works. Finally, an Artificial Intelligence will also be implemented in the application, to give advice to the sportsperson, based on the data received by the microcontrollers and on the data written in the application by the user.\n",
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                            item {
                                Text(
                                    text = "Our ambitions and objectives",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                            item {
                                Text(
                                    text = "Today we all know that sport is important for our health, we hear it from our families, on TV, everywhere. So many people want to do sport, but don't have the tools to see their progress, sometimes because they're not accessible, and so athletes can't see their results and performances, and quickly get discouraged. In fact, the tools that exist today to see their performance are watches in particular, which are often expensive and therefore not accessible to everyone.\n" +
                                            "\n" +
                                            "The aim of our project is therefore to offer an alternative way of analysing athletes' training, which is less expensive but just as effective and easy to use, with a piece of equipment to be placed on the athlete's body, another to be placed in the stadium, wherever he or she happens to be, and finally an application to be downloaded onto the athlete's phone.\n",
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
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