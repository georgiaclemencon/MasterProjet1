package com.example.masterprojet1

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Date


class NewCourse : ComponentActivity() {
    private lateinit var database: FirebaseDatabase
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var deviceInteraction: DeviceComposableInteraction
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        database =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")

        // Initialize the course
        course = Course(
            date = Date(),
            maxSpeed = 0f,
            realTimeSpeed = mutableStateOf(0f),
            speedValues = mutableStateOf(listOf<Int>()),
            position = "0.0,0.0"
        )

        readSpeedValuesFromDatabase()


        setContent {
            setupUI()


            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
            }
        }


        Intent(this, BluetoothService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }


    }

    private var bluetoothService: BluetoothService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }


fun readSpeedValuesFromDatabase() {
    val database =
        FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
    val speedListRef = database.getReference("users/course/vitesse")

    val speedValueListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val speedValues = dataSnapshot.getValue<List<Int>>()
            speedValues?.let {
                Log.d("Database", "Speed values: $it")
                course.speedValues.value = it // Update course.speedValues

                // Calculate average speed
                val averageSpeed = it.average()
                Log.d("Database", "Average speed: $averageSpeed")

                // Calculate maximum speed
                val maxSpeed = it.maxOrNull()
                Log.d("Database", "Max speed: $maxSpeed")

                // Update course with average and max speed
                course.realTimeSpeed.value = averageSpeed.toFloat()
                course.maxSpeed = maxSpeed?.toFloat() ?: 0f
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("Database", "Failed to read speed values.", databaseError.toException())
        }
    }

    speedListRef.addValueEventListener(speedValueListener)
}

    data class CourseData(
        val id: Int,
        val date: Date,
        val position: String,
        val maxSpeed: Float,
        val realTimeSpeed: Float,
        val speedValues: List<Int>
    )

    fun storeCourseData(course: Course) {
        val courseData = CourseData(
            id = course.id,
            date = course.date,
            position = course.position,
            maxSpeed = course.maxSpeed,
            realTimeSpeed = course.realTimeSpeed.value,
            speedValues = course.speedValues.value
        )

        Log.d("NewCourse", "Storing course data: $courseData")

        val courseRef = database.getReference("courses").child(course.id.toString())
        courseRef.setValue(courseData)
    }







    override fun onStop() {
        super.onStop()
        Log.d("NewCourse", "onStop called")
        storeCourseData(course)
        closeBluetoothGatt()
    }

    @SuppressLint("MissingPermission")
    private fun closeBluetoothGatt() {
        deviceInteraction.IsConnected = false
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    fun setupUI() {
    setContent {
        MasterProjet1Theme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        val date = SimpleDateFormat("HH:mm, dd MMM yyyy").format(Date())
                        Text(text = "Nouvelle course, à $date")

                        Spacer(modifier = Modifier.height(32.dp))

                        // Display real-time speed
                        val realTimeSpeed = remember { mutableStateOf("0f") }

                        // Listen for changes in course.realTimeSpeed
                        LaunchedEffect(course.realTimeSpeed) {
                            realTimeSpeed.value = course.realTimeSpeed.value.toString()
                        }

                        Text(text = "Vitesse en temps réel: ${course.realTimeSpeed.value}")

                        // Display speed values as a chart
                        DisplaySpeedChart(speedValues = course.speedValues.value)
                    }

                    item {
                        // Display course history
                        DisplayCourseHistory(course = course)
                    }

                    // Add more UI elements as needed...
                }
            }
        }
    }
}

@Composable
fun DisplayCourseHistory(course: Course) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Historique de la course")
        Divider(color = Color.Gray, thickness = 1.dp)
        Text(text = "Date: ${course.date}")
        Divider(color = Color.Gray, thickness = 1.dp)
        Text(text = "Vitesse maximale: ${course.maxSpeed}")
        Divider(color = Color.Gray, thickness = 1.dp)
        Text(text = "Allure par km: jsp encore")
        Divider(color = Color.Gray, thickness = 1.dp)
        Text(text = "Position: ${course.position}")
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

    @Composable
    fun DisplaySpeedChart(speedValues: List<Int>) {
        val modelProducer = remember { CartesianChartModelProducer.build() }
        val listSize = remember { mutableStateOf(speedValues.size) }

        LaunchedEffect(listSize.value) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(speedValues)
                }
            }
        }

        // Update listSize whenever speedValues changes
        LaunchedEffect(speedValues) {
            listSize.value = speedValues.size
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                ),
                modelProducer
            )
        }
    }

    @Composable
    fun DisplaySpeedValues(speedValues: List<Int>) {
        LazyColumn {
            items(speedValues) { speed ->
                Text(text = speed.toString())
            }
        }
    }




}