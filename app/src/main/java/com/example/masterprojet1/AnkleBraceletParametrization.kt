package com.example.masterprojet1

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

class AnkleBraceletParametrization : ComponentActivity() {

    private var deviceConnectionService: DeviceConnectionService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DeviceConnectionService.LocalBinder
            deviceConnectionService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            deviceConnectionService = null
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DeviceConnectionService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = Intent(this, DeviceConnectionService::class.java)
        startService(intent)

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


                    var minSpeed by remember { mutableStateOf("") }
                    var maxSpeed by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ankle Bracelet Parametrization",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        TextField(
                            value = minSpeed,
                            onValueChange = { minSpeed = it },
                            label = { Text("Minimum speed") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        TextField(
                            value = maxSpeed,
                            onValueChange = { maxSpeed = it },
                            label = { Text("Maximum speed") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        var textField1Value by remember { mutableStateOf("") }
                        var textField2Value by remember { mutableStateOf("") }
                        var context = LocalContext.current
                        ModalNavigationDrawer(
                            drawerContent = {
                                ModalDrawerSheet {
                                    Text("SPR MENU", modifier = Modifier.padding(16.dp))
                                    Divider()
                                    NavigationDrawerItem(
                                        label = { Text(text = "Profile") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, ProfileActivity::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "Historique") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, DeviceActivity::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "Ankle Bracelet Parametrization") },
                                        selected = false,
                                        onClick = {
                                            val intent = Intent(
                                                context,
                                                AnkleBraceletParametrization::class.java
                                            )
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "LEDs Ribbon Parametrization") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, LEDsParamActivity::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "Help") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, HelpActivity::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "Who are we ? What is SoundPaceRunners ?") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, CreatorsActivity::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text(text = "Contact Form") },
                                        selected = false,
                                        onClick = {
                                            val intent =
                                                Intent(context, ContactForm::class.java)
                                            startActivity(intent)
                                        }
                                    )
                                }

                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                //contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = "Ankle Bracelet Parametrization",
                                        //style = MaterialTheme.typography.h6,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .fillMaxWidth(), // Remplir la largeur disponible
                                        textAlign = TextAlign.Center // Centrer horizontalement le texte
                                    )

                                    var vitesseminimale = 20
                                    var vitessemaximale = 26
                                    // Créez un Intent en utilisant le constructeur approprié pour Kotlin
                                    val intent = Intent(
                                        this@AnkleBraceletParametrization,
                                        NewCourse::class.java
                                    ).apply {
                                        // Ajoutez la valeur en tant qu'extra à l'Intent
                                        putExtra("vitesseMinimale", vitesseminimale)
                                        putExtra("vitesseMaximale", vitessemaximale)
                                    }

                                    Divider(
                                        color = Color.White,
                                        thickness = 2.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(30.dp))

                                Button(
                                    onClick = { showAdditionalFields = !showAdditionalFields },
                                    modifier = Modifier.padding(top = 16.dp)
                                        .align(Alignment.CenterHorizontally)
                                    //textAlign = TextAlign.Center
                                ) {
                                    Text(
                                        text = if (showAdditionalFields) "Buzzer Parametrization available" else "Click to configure the buzzer",
                                        textAlign = TextAlign.Center // Centrer le texte
                                    )
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                if (showAdditionalFields) {
                                    Text(
                                        text = "Tick if you want to activate the buzzer",
                                        //style = MaterialTheme.typography.h6,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .fillMaxWidth(), // Remplir la largeur disponible
                                        textAlign = TextAlign.Center // Centrer horizontalement le texte
                                    )
                                    Checkbox(
                                        checked = checkedState,
                                        onCheckedChange = { checkedState = it },
                                        modifier = Modifier
                                            .padding(bottom = 16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }

                                Spacer(modifier = Modifier.height(30.dp))


                        Button(
    onClick = {
        finish()
    },
    modifier = Modifier.padding(top = 16.dp)
) {
    Text("OK, faire la course")
}

                                    Text(
                                        text = "L'accélérometre est activé par défaut, obligatoire pour les différentes mesures",
                                        //style = MaterialTheme.typography.h6,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            showAdditionalFieldstwo = !showAdditionalFieldstwo
                                        },
                                        modifier = Modifier
                                            .padding(top = 16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text = if (showAdditionalFieldstwo) "Heart Beat sensor Parametrization available" else "Click to configure the heart beat sensor",
                                            textAlign = TextAlign.Center // Centrer le texte
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    if (showAdditionalFieldstwo) {
                                        Text(
                                            text = "Tick if you want to activate the heart beat sensor",
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = TextAlign.Center // Centrer le texte
                                        )
                                        Checkbox(
                                            checked = checkedState,
                                            onCheckedChange = { checkedState = it },
                                            modifier = Modifier
                                                .padding(bottom = 16.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Divider(
                                        color = Color.White,
                                        thickness = 2.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "L'accéléromètre est activé par défaut, obligatoire pour les différentes mesures",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center // Centrer le texte
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                applicationContext,
                                                "Data sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(0.8f) // Utilisation du poids pour spécifier la largeur
                                            .align(Alignment.CenterHorizontally) // Centrer horizontalement
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
    }
}