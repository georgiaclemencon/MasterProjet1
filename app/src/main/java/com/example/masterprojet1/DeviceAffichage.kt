package com.example.masterprojet1


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun DeviceDetail(
    deviceActivity: DeviceActivity,
    courseId: String,
    deviceInteraction: MutableState<DeviceComposableInteraction>,
    course: Course,
    onConnectClick: () -> Unit
) {
    var time by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isclicked by remember { mutableStateOf(false) }
    var countdownTimer by remember { mutableStateOf(3) }

    //var gatt: BluetoothGatt? = null

    lateinit var deviceAddress: String

    //val deviceActivity = DeviceActivity()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nom du périphérique : ${deviceInteraction.value.deviceTitle}")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    onConnectClick()
                    isRunning = true
                    isclicked = true
                    time = 0 // Réinitialiser le chronomètre à zéro lorsque le bouton est cliqué
                    Log.e("ici button timer","on est la")
                    // Appel à la fonction pour écrire la caractéristique du buzzer

                    //deviceActivity.connectToDevice()
                    deviceActivity.writetocharac(1, 0)   //Troisieme charac du buzzer pour l'activer trois fois pendant le timer
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Demarrer une course")
            }
            IconButton(
                onClick = {
                    val intent = Intent(deviceActivity, AnkleBraceletParametrization::class.java)
                    deviceActivity.startActivity(intent)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }

        if(isclicked){
            LaunchedEffect(isRunning) {
                while (isRunning && countdownTimer > 0) {
                    delay(1000L)
                    countdownTimer--
                }
                isRunning = true
            }
        }

        if (isclicked && countdownTimer <= 0) {
            LaunchedEffect(isRunning) {
                while (isRunning) {
                    delay(1000L)
                    time++
                }
            }
        }

        val elapsedTimeInHours = time / 3600
        val elapsedTimeInMinutes = (time % 3600) / 60
        val elapsedTimeInSeconds = time % 60
        val elapsedTimeInMilliseconds = (time * 1000) % 1000
        Text(
            text = if (countdownTimer > 0) {
                "$countdownTimer"
            } else {
                "Chronos: ${
                    String.format(
                        "%02d:%02d:%02d:%03d",
                        elapsedTimeInHours,
                        elapsedTimeInMinutes,
                        elapsedTimeInSeconds,
                        elapsedTimeInMilliseconds
                    )
                }"
            },
            style = TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Button(
            onClick = {
                val intent = Intent(deviceActivity, Historique::class.java)
                deviceActivity.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp) // Ajouter du padding pour augmenter la taille du bouton
        ) {
            Text("Voir l'historique")
        }
        Button(
            onClick = {
                val elapsedTime = String.format(
                    "%02d:%02d:%02d",
                    elapsedTimeInHours,
                    elapsedTimeInMinutes,
                    elapsedTimeInSeconds
                )
                val newCourseIntent = Intent(deviceActivity, NewCourse::class.java)
                newCourseIntent.putExtra("EXTRA_CHRONOS", elapsedTime)
                newCourseIntent.putExtra("courseId", courseId)

                deviceActivity.startActivity(newCourseIntent)
                isRunning = false
                deviceActivity.closeBluetoothGatt()
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Arreter une course")
        }

        DisplayRealTimeSpeed(course) // Affiche la vitesse en temps réel
//        DisplayAverageSpeed(deviceActivity, deviceInteraction)
//        MyComposable(course) // Call MyComposable here
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TestChart() {
    Log.d("TestChart", "TestChart called") // Log ajouté
    val testData = mutableStateOf(listOf(1, 2, 3, 4, 5))
    val lineChart = createLineChart(testData)
    lineChart()
}

fun createLineChart(accelerometerData: MutableState<List<Int>>): @Composable () -> Unit {
    return {
        val modelProducer = remember { CartesianChartModelProducer.build() }
        val listSize = remember { mutableStateOf(accelerometerData.value.size) }

        LaunchedEffect(listSize.value) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(accelerometerData.value)
                }
            }
        }

        // Update listSize whenever accelerometerData changes
        LaunchedEffect(accelerometerData.value) {
            listSize.value = accelerometerData.value.size
        }

        Column {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                ),
                modelProducer
            )

            // Your legend here


        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun MyComposable(course: Course) {
    Log.d("MyComposable", "Called with realTimeSpeed: ${course.realTimeSpeed}")
    val speedValues = if (course.speedValues.value.isEmpty()) {
        Log.d("MyComposableEEE", "speedValues is empty") // Log ajouté
        mutableStateOf(listOf(0)) // Use a list with a single zero if speedValues is empty
    } else {
        Log.d("MyComposable", "speedValues is not empty") // Log ajouté
        course.speedValues // Use speedValues if it's not empty
    }
    val lineChart = createLineChart(speedValues as MutableState<List<Int>>) // Use speedValues for the chart data //ici a change si ca couille le graphique

    lineChart()
}


@Composable
fun DisplayAverageSpeed(
    deviceActivity: DeviceActivity,
    deviceInteraction: MutableState<DeviceComposableInteraction>
) {
    var averageSpeed by remember { mutableStateOf(0f) }


    LaunchedEffect(key1 = true) {
        while (true) {
            averageSpeed = deviceActivity.calculateAverageSpeed()
            delay(5000L) // delay for 5 seconds
        }
    }

    Text("Vitesse moyenne : ${averageSpeed.toInt()}")
    Log.e("Average Speed", "Average Speed: $averageSpeed")
}

@Composable
fun DisplayRealTimeSpeed(course: Course) {
    val speed = course.realTimeSpeed.value
    LaunchedEffect(speed) {
        // This block will be recomposed whenever speed changes
    }
   Text(
    text = "Vitesse en temps réel : ${speed.toFloat()} m/s",
    style = MaterialTheme.typography.bodyLarge
)

}


//@Composable
//fun Stopwatch() {
//    var time by remember { mutableStateOf(0) }
//    var isRunning by remember { mutableStateOf(false) }
//
//    LaunchedEffect(isRunning) {
//        while (isRunning) {
//            delay(1000L)
//            time++
//        }
//    }
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(text = "Time: $time", modifier = Modifier.padding(16.dp))
//        Button(onClick = { isRunning = !isRunning }) {
//            Text(if (isRunning) "Stop" else "Start")
//        }
//    }
//
//}




data class Course(
    val id: Int = 0,
    val date: Date,
    var position: String, // Vous devez obtenir la position actuelle et la convertir en String
    var maxSpeed: Float,
    val realTimeSpeed: MutableState<Float> = mutableStateOf(0f),
    val speedValues: MutableState<List<Float>> = mutableStateOf(listOf())
)

class DeviceComposableInteraction(
    var IsConnected: Boolean = false,
    var deviceTitle: String = "",
//    var realTimeSpeed: MutableState<Float> = mutableStateOf(0f),
    // var speedValues: MutableState<List<Int>> = mutableStateOf(listOf()) // List to store all speed values as Int
)