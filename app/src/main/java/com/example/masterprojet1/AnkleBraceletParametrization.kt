package com.example.masterprojet1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
                            label = { Text("Vitesse minimale") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        TextField(
                            value = maxSpeed,
                            onValueChange = { maxSpeed = it },
                            label = { Text("Vitesse maximale") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    }
                }
            }
        }


    }
//    fun writeData() {
//        val minSpeed = minSpeed.toInt()
//        val maxSpeed = maxSpeed.toInt()
//        val buzzer = checkedState
//        val heartRateSensor = checkedStatetwo
//
//        deviceConnectionService?.writeData(minSpeed, maxSpeed, buzzer, heartRateSensor)
//    }
}