package com.example.masterprojet1


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.Date
import java.util.UUID

@SuppressLint("MissingPermission")
class DeviceActivity : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var deviceInteraction: DeviceComposableInteraction
    private lateinit var course: Course

    private var bluetoothService: BluetoothService? = null
    private var device: BluetoothDevice? = null // Define device as a property of DeviceActivity

    private var courseId: String? = null

    var isRunning = true
    private lateinit var newCourseIntent: Intent


    //private var currentLEDStateEnum = LEDStateEnum.NONE

    private var realSpeedBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null


    private var deviceConnectionService: DeviceConnectionService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DeviceConnectionService.LocalBinder
            deviceConnectionService = binder.getService()
            Log.e("DeviceActivity", "Service connected")
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

    // Generate a unique identifier for the course
    val database = Firebase.database

// In your method
courseId = generateUUID()
newCourseIntent = Intent(this, NewCourse::class.java)
newCourseIntent.putExtra("courseId", courseId) // Replace "your_course_id" with the actual course id
    Log.e("DeviceActivity", "Putting courseId in intent: $courseId")
}

    fun isDeviceConnected(): Boolean {
        return deviceConnectionService?.isDeviceConnected() ?: false
    }


    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<BluetoothDevice?>("device")






        setContent {
            val isStateConnected = remember { mutableStateOf(false) }
            val realTimeSpeed = mutableStateOf(0f)
            val speedValues = mutableStateOf(listOf<Int>())

            deviceInteraction = DeviceComposableInteraction(
                IsConnected = isStateConnected.value,
                deviceTitle = device?.name ?: "Device Unknown",
//                realTimeSpeed = mutableStateOf(0f) // Initialize realTimeSpeed with 0f
            )


            val floatSpeedValues = mutableStateOf(speedValues.value.map { it.toFloat() })

course = Course(
    id = 0,
    date = Date(),
    position = "0.0,0.0",
    maxSpeed = 0f,
    realTimeSpeed = realTimeSpeed,
    speedValues = floatSpeedValues
)

            courseId?.let {
                DeviceDetail(this, it, mutableStateOf(deviceInteraction), course){
                    connectToDevice(device)
                }
            }
        }

        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private fun connectToDevice(device: BluetoothDevice?) {
        bluetoothGatt = device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt?,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(gatt, status, newState)
                connectionStateChange(gatt, newState)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                val serviceUUID = UUID.fromString("00000000-cc7a-482a-984a-7f2ed5b3e58f")
                val characteristicUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")
                val cccDescriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

                val service = gatt?.getService(serviceUUID)
                val characteristic = service?.getCharacteristic(characteristicUUID)

                if (characteristic != null) {
                    realSpeedBluetoothGattCharacteristic =
                        characteristic // Assign the characteristic to realSpeedBluetoothGattCharacteristic

                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(cccDescriptorUUID)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    characteristic?.let {
                        val intValue = it.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        Log.d("CharacteristicValue", "UUID: ${it.uuid}, Value: $intValue")
                    }
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                val intValue =
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                Log.d("CharacteristicValue", "UUID: ${characteristic.uuid}, Value: $intValue")

                if (characteristic.uuid == realSpeedBluetoothGattCharacteristic?.uuid) {
                    val newSpeed = convertLittleEndianToFloat(characteristic.value)

                    course.realTimeSpeed.value = newSpeed // Update the value
                    course.speedValues.value = course.speedValues.value + newSpeed // Add the new speed to speedValues

                    Log.e("RealTimeSpeed", "Real Time Speed: $newSpeed") // Log the real time speed

                    // Only update speedValues in Firebase if isRunning is true
                    if (isRunning) {
                        // Get a reference to the speed list in Firebase
                        val database = Firebase.database
                        // Use the unique id in the reference path
                        val speedListRef =
                            database.getReference("users/course/${this@DeviceActivity.courseId}/vitesse")

                        // Add the new speed value to the list
                        speedListRef.setValue(course.speedValues.value)
                        Log.e(
                            "DeviceActivity",
                            "Speed values added to Firebase: ${course.speedValues.value}"
                        )



                    } else {
                        Log.d(
                            "onCharacteristicChanged",
                            "isRunning is false, not adding speed value to Firebase"
                        ) // Log when isRunning is false
                    }
                } else {
                    Log.e("RealTimeSpeed", "UUID does not match: ${characteristic.uuid}")
                }
            }
        })
        bluetoothGatt?.connect()
    }

    private fun connectionStateChange(gatt: BluetoothGatt?, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt?.discoverServices()
        }
        runOnUiThread {
            if (::deviceInteraction.isInitialized) {
                deviceInteraction.IsConnected = newState == BluetoothProfile.STATE_CONNECTED
                if (deviceInteraction.IsConnected) {
                    Toast.makeText(this, "Connecté", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun logServices(gatt: BluetoothGatt?) {
//        gatt?.services?.forEach { service ->
//            Log.d("logfun", "Service UUID: ${service.uuid}")
//            service.characteristics.forEach { characteristic ->
//                Log.d("logfun", "Characteristic UUID: ${characteristic.uuid}")
//            }
//        }
//    }


    fun calculateAverageSpeed(): Float {
        return if (course.speedValues.value.isNotEmpty()) { // Check if speedValues is not empty
            val averageSpeed = course.speedValues.value.average()
            Log.d("AverageSpeed", "Average Speed: $averageSpeed")
            // envoie de la valeur de la vitesse moyenne à la base de données


            averageSpeed.toFloat()
        } else {
            Log.d("AverageSpeed", "No speed values available")
            0f
        }
    }


//    override fun onStop() {
//        super.onStop()
//        closeBluetoothGatt()
//    }


    fun convertLittleEndianToFloat(bytes: ByteArray): Float {
    val intBits = bytes[0].toInt() and 0xFF or
            ((bytes[1].toInt() and 0xFF) shl 8) or
            ((bytes[2].toInt() and 0xFF) shl 16) or
            ((bytes[3].toInt() and 0xFF) shl 24)
    return Float.fromBits(intBits)
}

    fun closeBluetoothGatt() {
        deviceInteraction.IsConnected = false
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }




}


