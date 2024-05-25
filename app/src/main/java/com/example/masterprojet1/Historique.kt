package com.example.masterprojet1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.tasks.await

class Historique : ComponentActivity() {

    private lateinit var database: FirebaseDatabase
    private var courseId: String? = null
    private val speedValues = mutableStateOf(listOf<Float>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
//   courseId = intent.getStringExtra("courseId")
//Log.d("Historique", "courseId: $courseId")
        courseId = "2d864908-4394-43aa-be28-77b0b2bb2155"
        //readSpeedValuesFromDatabase()

        setContent {
            MaterialTheme {
                HistoryScreen()
                DisplaySpeedChart()
                LaunchedEffect(Unit) {
                    fetchRaceDatesFromFirebase()
                }
            }
        }
    }
//    fun readSpeedValuesFromDatabase() {
//        val speedListRef = database.getReference("users/courses")
//
//        val speedValueListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val speedValuesFromDB = dataSnapshot.getValue<List<Float>>()
//                speedValuesFromDB?.let {
//                    speedValues.value = it
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w("Database", "Failed to read speed values.", databaseError.toException())
//            }
//        }
//
//        speedListRef.addValueEventListener(speedValueListener)
//    }

    @Composable
    fun DisplaySpeedChart() {
        val modelProducer = remember { CartesianChartModelProducer.build() }
        val listSize = remember { mutableStateOf(speedValues.value.size) }

        LaunchedEffect(listSize.value) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(speedValues.value)
                }
            }
        }

        // Update listSize whenever speedValues changes
        LaunchedEffect(speedValues.value) {
            listSize.value = speedValues.value.size
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            modelProducer
        )
    }
@SuppressLint("RestrictedApi")
suspend fun fetchRaceDatesFromFirebase(): List<String> {
    val raceDates = mutableListOf<String>()

    // Get a reference to the Firebase database
    val database = FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
    val courseRef = database.getReference("users/course/2d864908-4394-43aa-be28-77b0b2bb2155/donnees_course/date")

    Log.d("fetchRaceDatesFromFirebase", "Fetching data from Firebase")

    // Fetch the data from the reference
    val data = courseRef.get().await()

    Log.d("fetchRaceDatesFromFirebase", "Data fetched from Firebase: $data")

    // Extract the data from the snapshot
    val dateData = data.getValue<Map<String, Any>>()

    // Extract individual fields from the data
    val day = dateData?.get("date")?.let { (it as Long).toInt() }
    val month = dateData?.get("month")?.let { (it as Long).toInt() }
    val year = dateData?.get("year")?.let { ((it as Long) + 1900).toInt() } // Add 1900 to get the actual year

    Log.d("fetchRaceDatesFromFirebase", "Fetched date components: day=$day, month=$month, year=$year")

    if (day != null && month != null && year != null) {
        val raceDate = "$year-${month.toString().padStart(2, '0')}-$day"
        raceDates.add(raceDate)
        Log.d("fetchRaceDatesFromFirebase", "Added date: $raceDate")
    }

    Log.d("fetchRaceDatesFromFirebase", "Returning race dates: $raceDates")

    return raceDates
}

@SuppressLint("RestrictedApi")
suspend fun fetchCourseDataFromFirebase(date: String): MutableMap<String, Any?> {
    val courseData = mutableMapOf<String, Any?>()

    // Get a reference to the Firebase database
    val database = FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
    val courseDataRef = database.getReference("users/courses/2d864908-4394-43aa-be28-77b0b2bb2155/donnees_course")
    Log.e("courseDataRef", courseDataRef.toString())

    Log.d("fetchCourseDataFromFirebase", "Fetching data from Firebase")

    // Fetch the data from the reference
    val dataSnapshot = courseDataRef.get().await()

    Log.d("fetchCourseDataFromFirebase", "Data fetched from Firebase: $dataSnapshot")

    // Extract the data from the snapshot
    val data = dataSnapshot.getValue<Map<String, Any>>()

    // Extract individual fields from the data
    courseData["averageSpeed"] = data?.get("averageSpeed")
    courseData["chronos"] = data?.get("chronos")
    courseData["id"] = data?.get("id")
    courseData["maxSpeed"] = data?.get("maxSpeed")
    courseData["maxSpeedValue"] = data?.get("maxSpeedValue")
    courseData["pacePerKm"] = data?.get("pacePerKm")
    courseData["position"] = data?.get("position")
    courseData["pulseValue"] = data?.get("pulseValue")
    courseData["realTimeSpeed"] = data?.get("realTimeSpeed")
    courseData["speedValues"] = data?.get("speedValues")
    courseData["vheartBeat"] = data?.get("vheartBeat")

    Log.d("fetchCourseDataFromFirebase", "Returning course data: $courseData")

    return courseData
}
    @Composable
    fun DropdownMenu(
        items: List<String>,
        selectedItem: String,
        onItemSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Text(
                text = if (selectedItem.isEmpty()) "Sélectionnez une date" else selectedItem,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true })
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEach { label ->
                    CustomDropdownMenuItem(onClick = {
                        onItemSelected(label)
                        expanded = false
                    }, label = label)
                }
            }
        }
    }
    @Composable
    fun HistoryScreen() {
        var selectedDate by remember { mutableStateOf("") }
        val raceDates = remember { mutableStateOf<List<String>?>(null) }
        val courseData = remember { mutableStateOf<List<Any>?>(null) } // Initialize with null

        LaunchedEffect(Unit) {
            raceDates.value = fetchRaceDatesFromFirebase() // Fetch race dates from Firebase
            Log.d("HistoryScreen", "Updated raceDates: ${raceDates.value}") // Log the updated raceDates
        }

        LaunchedEffect(selectedDate) {
            if (selectedDate.isNotEmpty()) {
                courseData.value =
                    listOf(fetchCourseDataFromFirebase(selectedDate)) // Fetch course data from Firebase
                Log.d("HistoryScreen", "Updated courseData: ${courseData.value}") // Log the updated courseData
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Historique",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp)
            )

            // Only display DropdownMenu if raceDates is not null
            raceDates.value?.let { dates ->
                DropdownMenu(
                    items = dates,
                    selectedItem = selectedDate,
                    onItemSelected = { date ->
                        selectedDate = date
                        courseData.value =
                            listOf("Données de course pour $date") // Update course data when a date is selected
                    }
                )
            }

            // Afficher les données de course
            Text(text = courseData.value?.joinToString(", ") ?: "")
        }
    }

    @Composable
    private fun CustomDropdownMenuItem(onClick: () -> Unit, label: String) {
        DropdownMenuItem(
            text = { Text(text = label) },
            onClick = onClick
        )
    }
}