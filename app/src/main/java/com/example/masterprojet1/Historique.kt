package com.example.masterprojet1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.util.Date

class Historique : ComponentActivity() {
    private lateinit var deviceInteraction: DeviceComposableInteraction

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isStateConnected = remember { mutableStateOf(false) }
            val realTimeSpeed = mutableStateOf(0f)
            val speedValues = mutableStateOf(listOf<Int>())

            deviceInteraction = DeviceComposableInteraction(
                IsConnected = isStateConnected.value,
                deviceTitle = "Device Unknown" // You need to provide a deviceTitle here
            )


            var course = Course(
                date = Date(), // You need to provide a date here
                maxSpeed = 0f, // You need to provide a maxSpeed here
                realTimeSpeed = realTimeSpeed,
                speedValues = speedValues,
                position = "0.0,0.0"
            )

            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyComposable(course)
                }
            }
        }
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
                CustomLegend(
                    colors = listOf(
                        Color.Red,
                        Color(android.graphics.Color.GREEN),
                        Color.Blue
                    ),
                    labels = listOf("Label 1", "Label 2", "Label 3")
                )

            }
        }
    }


    @Composable
    fun CustomLegend(colors: List<Color>, labels: List<String>) {
        Row {
            colors.zip(labels).forEach { (color, label) ->
                LegendItem(color = color, text = label)
            }
        }
    }


    @Composable
    fun LegendItem(color: Color, text: String) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text)
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
        val lineChart = createLineChart(speedValues) // Use speedValues for the chart data

        lineChart()
    }

    // Create a test chart with static data
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun TestChart() {
        Log.d("TestChart", "TestChart called") // Log ajouté
        val testData = mutableStateOf(listOf(1, 2, 3, 4, 5))
        val lineChart = createLineChart(testData)
        lineChart()
    }
}

