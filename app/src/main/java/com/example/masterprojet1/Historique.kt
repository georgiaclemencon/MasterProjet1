package com.example.masterprojet1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        //courseId = "2d864908-4394-43aa-be28-77b0b2bb2155"
        //readSpeedValuesFromDatabase()

        setContent {
            MaterialTheme {
                HistoryScreen()
                DisplaySpeedChart()

            }
        }
    }

    @Composable
    fun FetchCourseDataOnDateSelected(courseId: String, courseData: MutableState<List<String>?>) {
        LaunchedEffect(courseId) {
            try {
                courseData.value = listOf(fetchAllCourseDataFromFirebase()).toString().split(",")
                Log.d("DATA", "Updated courseData: ${courseData.value}")
            } catch (e: Exception) {
                Log.e("DATA", "Error fetching course data: ${e.message}")
            }
        }
    }


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

//    @SuppressLint("RestrictedApi")
//    suspend fun fetchRaceDatesFromFirebase(): Map<String, String> {
//        val raceDates = mutableMapOf<String, String>()
//
//        // Get a reference to the Firebase database
//        val database =
//            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
//
//        // Get a list of all course IDs
//        val coursesRef = database.getReference("users/course")
//        val courseIdsSnapshot = coursesRef.get().await()
//        val courseIds = courseIdsSnapshot.children.map { it.key ?: "" }
//
//        // Create a SimpleDateFormat object for formatting the dates
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//        val outputFormat = SimpleDateFormat("dd MM yyyy HH:mm", Locale.getDefault())
//
//        // Iterate through all course IDs
//        for (courseId in courseIds) {
//            // Get a reference to the date of the current course
//            val dateRef = database.getReference("users/course/$courseId/donnees_course/date")
//
//            // Fetch the date from the reference
//            val dateSnapshot = dateRef.get().await()
//
//            // Check if dateSnapshot exists and is not null
//            if (dateSnapshot.exists()) {
//                // Extract the date from the snapshot
//                val dateData = dateSnapshot.getValue<Map<String, Any>>()
//
//                // Extract individual fields from the date data
//                val day = dateData?.get("date")?.let { (it as Long).toInt() }
//                val month = dateData?.get("month")?.let { (it as Long).toInt() }
//                val year = dateData?.get("year")
//                    ?.let { ((it as Long) + 1900).toInt() } // Add 1900 to get the actual year
//                val hour = dateData?.get("hours")?.let { (it as Long).toInt() }
//                val minute = dateData?.get("minutes")?.let { (it as Long).toInt() }
//
//                if (day != null && month != null && year != null && hour != null && minute != null) {
//                    val date = "$year-${month.toString().padStart(2, '0')}-$day $hour:$minute"
//                    Log.d(
//                        "fetchRaceDatesFromFirebase",
//                        "Fetched date: $date"
//                    ) // Log the fetched date
//                    val dateFormatted = outputFormat.format(inputFormat.parse(date)!!)
//                    Log.d(
//                        "fetchRaceDatesFromFirebase",
//                        "Formatted date: $dateFormatted"
//                    ) // Log the formatted date
//                    raceDates[courseId] = dateFormatted
//                } else {
//                    Log.e("fetchRaceDatesFromFirebase", "Failed to extract date fields: $dateData")
//                }
//            } else {
//                Log.e(
//                    "fetchRaceDatesFromFirebase",
//                    "DateSnapshot does not exist for courseId: $courseId"
//                )
//            }
//        }
//
//        // Sort the dates from oldest to newest before returning them
//        return raceDates.values.toList().sorted()
//            .mapIndexed { index, date -> index.toString() to date }.toMap()
//    }


    fun fetchRaceDatesFromFirebase(): Task<DataSnapshot> {
        // Get a reference to the Firebase database
        val database =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
        val coursesRef = database.getReference("users/course")


        return coursesRef.get()
    }

    @SuppressLint("RestrictedApi")
    suspend fun fetchAllCourseDataFromFirebase(): List<String> {
        val allCourseData = mutableListOf<String>()

        // Get a reference to the Firebase database
        val database =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
        val coursesRef = database.getReference("users/course")

        Log.d("DATAS", "Fetching all course IDs")

        // Fetch all course IDs
        val courseIdsSnapshot = coursesRef.get().await()
        val courseIds = courseIdsSnapshot.children.mapNotNull { it.key }

        // Iterate over all course IDs
        for (courseId in courseIds) {
            val courseDataRef = database.getReference("users/course/$courseId/donnees_course")

            Log.d("DATAS", "Fetching data for course ID: $courseId")

            try {
                // Fetch the data from the reference
                val dataSnapshot = courseDataRef.get().await()

                // Check if dataSnapshot exists and is not null
                if (dataSnapshot.exists()) {
                    Log.d("DATAS", "DataSnapshot exists for course ID: $courseId")

                    // Extract the data from the snapshot
                    val data = dataSnapshot.getValue<Map<String, Any>>()

                    // Check if data is not null
                    if (data != null) {
                        Log.d("DATAS", "Data is not null for course ID: $courseId")

                        // Extract individual fields from the data and add them to the list
                        for (key in data.keys) {
                            // Check if the key is not "courseId" or "date"
                            if (key != "id") {
                                data[key]?.let {
                                    val dataItem = "$key: $it"
                                    allCourseData.add(dataItem)
                                    Log.d(
                                        "DATAS",
                                        "Added $dataItem to allCourseData for course ID: $courseId"
                                    )
                                }
                            }
                        }
                    } else {
                        Log.e("DATAS", "Data is null for course ID: $courseId")
                    }
                } else {
                    Log.e("DATAS", "DataSnapshot does not exist for course ID: $courseId")
                }
            } catch (e: Exception) {
                Log.e(
                    "DATAS",
                    "Error fetching data from Firebase for course ID: $courseId, Error: ${e.message}"
                )
            }
        }

        Log.d("DATAS", "Returning a copy of allCourseData: ${allCourseData.toList()}")

        return allCourseData.toList()
    }


    //    @Composable
//    fun DropdownMenu(
//        items: List<String>,
//        selectedItem: String,
//        onItemSelected: (String) -> Unit
//    ) {
//        var expanded by remember { mutableStateOf(false) }
//        var selectedDate by remember { mutableStateOf(if (items.isNotEmpty()) items[0] else "") }
//
//        Box {
//            Text(
//                text = if (selectedDate.isEmpty()) "Sélectionnez une date" else selectedDate,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable(onClick = { expanded = true })
//                    .padding(16.dp),
//                style = MaterialTheme.typography.bodyLarge
//            )
//            androidx.compose.material3.DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                items.forEach { label ->
//                    CustomDropdownMenuItem(onClick = {
//                        selectedDate = label
//                        expanded = false
//
//                        onItemSelected(label)
//
//                    }, label = label)
//                }
//            }
//        }
//    }
//    @Composable
//    fun DisplayAllCourseData() {
//        val allCourseData = remember { mutableStateOf<Map<String, MutableList<String>>?>(null) }
//
//        LaunchedEffect(Unit) {
//            allCourseData.value = fetchAllCourseDataFromFirebase()
//        }
//
//        allCourseData.value?.let { data ->
//            LazyColumn {
//                items(data.entries.toList()) { entry ->
//                    val (courseId, courseData) = entry
//                    Text(text = "Course ID: $courseId")
//                    courseData.forEach { dataItem ->
//                        Text(text = dataItem)
//                    }
//                }
//            }
//        }
//    }
    @Composable
    fun HistoryScreen() {
        val allCourseData = remember { mutableStateOf<List<String>?>(null) }
        val expanded = remember { mutableStateOf(false) }
        val raceDates = remember { mutableStateOf<List<Any>>(listOf()) }

        val showAdditionalData = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            fetchRaceDatesFromFirebase().addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { childSnapshot ->
                    val value = childSnapshot.value
                    Log.d("baptiste", "Value: $value")
                    raceDates.value = raceDates.value + value!!
                    Log.d("baptiste", "raceDates: $raceDates")
                }
            }.addOnFailureListener { exception ->
                Log.e("baptiste", "Error getting data", exception)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {
                Text(
                    text = "Historique",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(raceDates.value) { raceData ->
                val data = raceData as Map<String, Any>
                val courseData = data["donnees_course"]
                if (courseData is Map<*, *>) {
                    val date = courseData["date"] as Map<*, *>

                    val timestamp = date["time"]

                    if (timestamp != null) {
                        // Construct a Date object from the timestamp
                        val date = Date(timestamp as Long)

                        // Format the Date object in the desired format
                        val outputFormat =
                            SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                        val formattedDate = outputFormat.format(date)

                        // Extract additional data
                        val averageSpeed = courseData["averageSpeed"]
                        val chronos = courseData["chronos"]
                        val maxSpeed = courseData["maxSpeed"]
                        val maxSpeedValue = courseData["maxSpeedValue"]
                        val pacePerKm = courseData["pacePerKm"]
                        val position = courseData["position"]

                        // Display date and additional data when clicked
                        Text(
                            text = "$formattedDate",
                            modifier = Modifier.clickable {
                                showAdditionalData.value = !showAdditionalData.value
                            }
                        )

                        if (showAdditionalData.value) {
                            Text(text = "Average Speed: $averageSpeed")
                            Text(text = "Chronos: $chronos")
                            Text(text = "Max Speed: $maxSpeed")
                            Text(text = "Max Speed Value: $maxSpeedValue")
                            Text(text = "Pace per Km: $pacePerKm")
                            Text(text = "Position: $position")
                        }
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                }
            }

//            item {
//                raceDates.value.let { raceData ->
//                    var data = JSONObject()
//                    data = raceData[0] as JSONObject
//                    Text(text = "Date: ${data.get("date")}")
//                    Spacer(modifier = Modifier.padding(16.dp))
//
//                }
//            }
//                allCourseData.value?.let { courseData ->
//                    Log.d("HistoryScreen", "Displaying data")
//
//                    for ((index, item) in courseData.withIndex()) {
//                        val splitItem = item.split(":")
//                        if (splitItem.size == 2) {
//                            Log.d(
//                                "HistoryScreen",
//                                "splitItem[0]: ${splitItem[0]}, splitItem[1]: ${splitItem[1]}"
//                            )
//                            if (splitItem[0].trim() == "date") { // Check if the current item is the date
//                                // Parse the date string into a Map
//                                val dateMap =
//                                    splitItem[1].trim().removeSurrounding("{", "}").split(", ")
//                                        .associate {
//                                            val (key, value) = it.split("=")
//                                            key to value.toLong() // Parse the value as a Long
//                                        }
//
//                                // Extract the timestamp from the date map
//                                val timestamp = dateMap["time"]
//
//                                if (timestamp != null) {
//                                    // Construct a Date object from the timestamp
//                                    val date = Date(timestamp)
//
//                                    // Format the Date object in the desired format
//                                    val outputFormat =
//                                        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
//                                    val formattedDate = outputFormat.format(date)
//
//                                    Box {
//                                        Text(
//                                            text = " date: $formattedDate",
//                                            style = MaterialTheme.typography.displayMedium, // Use a larger style for the date
//                                            modifier = Modifier.clickable { expanded.value = !expanded.value }
//                                        )
//                                        DropdownMenu(
//                                            expanded = expanded.value,
//                                            onDismissRequest = { expanded.value = false }
//                                        ) {
//                                            Text(
//                                                text = " ${splitItem[0]}: ${splitItem[1]}",
//                                                style = MaterialTheme.typography.titleLarge, // Use a smaller style for other data
//                                                modifier = Modifier.clickable { expanded.value = false }
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                Text(
//                                    text = " ${splitItem[0]}: ${splitItem[1]}",
//                                    style = MaterialTheme.typography.titleLarge // Use a smaller style for other data
//                                )
//                            }
//                            Log.d(
//                                "HistoryScreen",
//                                "Displaying data: $item"
//                            )
//                        }
//                    }
//                }
        }

    }
}