package com.example.masterprojet1

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Geocoder
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.lifecycleScope
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NewCourse : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var startTime = 0L
    private var courseData: CourseData? = null

    private lateinit var database: FirebaseDatabase
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var deviceInteraction: DeviceComposableInteraction
    private lateinit var course: Course
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var elapsedTime: String? = null


    private var courseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getStringExtra("courseId")


        Log.d("NewCourse", "Got courseId from intent: $courseId")


        elapsedTime = intent.getStringExtra("EXTRA_CHRONOS")

        Intent(this, BluetoothService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }

        startTime = System.currentTimeMillis()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        database =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")

        // Initialize the course
        course = Course(
            date = Date(),
            maxSpeed = 0f,
            realTimeSpeed = mutableStateOf(0f),
            speedValues = mutableStateOf(listOf<Float>()),
            position = "0.0,0.0"
        )

        deviceInteraction = DeviceComposableInteraction()

        lifecycleScope.launch {
            readSpeedValuesFromDatabase()
            getLastKnownLocation()
            setupUI()
        }
        readSpeedValuesFromDatabase()
//        storeCourseData(course, elapsedTime)

        getLastKnownLocation() // Now it's safe to call this method


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
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted
                    getLastKnownLocation()

                } else {
                    // Permission was denied. Disable the functionality that depends on this permission.
                    Log.d("NewCourse", "Location permission was denied.")
                }
                return
            }
            // Handle other permission results
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Get the city name
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cityName = addresses?.get(0)?.locality
                        course.position =
                            cityName ?: "Unknown location" // Set course.position to the city name
                        Log.d("NewCourse", "City: $cityName") // Log the city name
                    } catch (e: IOException) {
                        Log.e("NewCourse", "Failed to get city name: $e")
                    }
                } else {
                    Log.d("NewCourse", "Location is null") // Log when location is null
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NewCourse", "Failed to get location: $exception") // Log any exceptions
            }
    }


    fun readSpeedValuesFromDatabase() {
        Log.e("testtt", "Course ID: $courseId")


        val speedListRef = database.getReference("users/course/$courseId/vitesse")

        val speedValueListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val speedValues = dataSnapshot.getValue<List<Float>>()
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
        val chronos: String,
        val realTimeSpeed: Float,
        val speedValues: List<Float>
    ) {
        val averageSpeed: Float
            get() = if (speedValues.isNotEmpty()) speedValues.average().toFloat() else 0f

        val pacePerKm: Float
            get() = if (averageSpeed != 0f) 60 / averageSpeed else 0f

        val maxSpeedValue: Float
            get() = speedValues.maxOrNull()?.toFloat() ?: 0f
    }

    fun storeCourseData(course: Course, elapsedTime: String?) {
        var courseId = intent.getStringExtra("courseId")
        val courseData = CourseData(
            id = course.id,
            date = course.date,
            position = course.position,
            maxSpeed = course.maxSpeed,
            chronos = elapsedTime ?: "00:00:00",
            realTimeSpeed = course.realTimeSpeed.value,
            speedValues = course.speedValues.value.map { it.toFloat() }
        )

        // Utilisez courseId pour créer une référence unique pour chaque course
        val courseRef = database.getReference("users/course/$courseId/donnees_course")

        courseRef.setValue(courseData)
            .addOnSuccessListener {
                Log.d("NewCourse", "Course data stored successfully")
            }
            .addOnFailureListener {
                Log.e("NewCourse", "Failed to store course data: $it")
            }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
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

// Display the chronos of the course
                            Text(
                                text = "Chronos: ${elapsedTime ?: "00:00:00"}",
                                style = MaterialTheme.typography.headlineMedium
                            )

// Display real-time speed
                            val realTimeSpeed = remember { mutableStateOf("0f") }

// Listen for changes in course.realTimeSpeed
                            LaunchedEffect(course.realTimeSpeed) {
                                realTimeSpeed.value = course.realTimeSpeed.value.toString()
                            }

                            Text(text = "Vitesse moyenne: ${course.realTimeSpeed.value}")

                            // Display speed values as a chart
                            DisplaySpeedChart(speedValues = course.speedValues.value.map { it.toInt() })
                        }

                        item {
                            // Display course history

                            DisplayCourseHistory(
                                courseData = CourseData(
                                    id = course.id,
                                    date = course.date,
                                    position = course.position,
                                    maxSpeed = course.maxSpeed,
                                    chronos = elapsedTime ?: "00:00:00",
                                    realTimeSpeed = course.realTimeSpeed.value,
                                    speedValues = course.speedValues.value.map { it.toFloat() }
                                )
                            )

                        }

                        // Add more UI elements as needed...
                    }
                }
            }
        }
    }

    @Composable
    fun DisplayCourseHistory(courseData: CourseData) {
        Log.d("DisplayCourseHistory", "Displaying course history for courseData: $courseData")
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Historique de la course")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Text(text = "Date: ${courseData.date}")
            Log.d("DisplayCourseHistory", "Date: ${courseData.date}")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Text(text = "Vitesse maximale: ${courseData.maxSpeedValue} m/s")
            Log.d("DisplayCourseHistory", "Vitesse maximale: ${courseData.maxSpeedValue} m/s")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Text(text = "Vitesse moyenne: ${courseData.averageSpeed} m/s")
            Log.d("DisplayCourseHistory", "Vitesse moyenne: ${courseData.averageSpeed} m/s")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Text(text = "Allure par km: ${courseData.pacePerKm} min/km")
            Log.d("DisplayCourseHistory", "Allure par km: ${courseData.pacePerKm} min/km")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Text(text = "Position: ${courseData.position}")
            Log.d("DisplayCourseHistory", "Position: ${courseData.position}")
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        }
        storeCourseData(course, elapsedTime)
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