package com.example.masterprojet1
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

class Historique : ComponentActivity() {

    private lateinit var database: FirebaseDatabase
    private var courseId: String? = null
    private val speedValues = mutableStateOf(listOf<Float>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
        courseId = intent.getStringExtra("courseId")
        readSpeedValuesFromDatabase()
    }

    fun readSpeedValuesFromDatabase() {
        val speedListRef = database.getReference("users/courses/$courseId/vitesse")

        val speedValueListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val speedValuesFromDB = dataSnapshot.getValue<List<Float>>()
                speedValuesFromDB?.let {
                    speedValues.value = it
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Database", "Failed to read speed values.", databaseError.toException())
            }
        }

        speedListRef.addValueEventListener(speedValueListener)
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
}