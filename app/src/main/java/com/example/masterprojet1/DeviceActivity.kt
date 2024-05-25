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
import java.util.LinkedList
import java.util.Queue
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
    val CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"

    //private var currentLEDStateEnum = LEDStateEnum.NONE

    private var realSpeedBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var pulseBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null

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
    val characteristicsQueue: Queue<BluetoothGattCharacteristic> = LinkedList()
    fun BluetoothGattCharacteristic.isReadable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DeviceConnectionService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Generate a unique identifier for the course
        val database = Firebase.database

        courseId = generateUUID()
        if (::course.isInitialized) {
            course.id = courseId as String
        }
        Log.e("COURSEID", "Putting courseId in intent: $courseId")
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
            val pulseValue = mutableStateOf(0f)


            course = Course(
                id = courseId as String,
                date = Date(),
                position = "0.0,0.0",
                maxSpeed = 0f,
                realTimeSpeed = realTimeSpeed,
                speedValues = floatSpeedValues,
                pulse = pulseValue,
            )

            courseId?.let {
                DeviceDetail(this, it, mutableStateOf(deviceInteraction), course) {
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


    @OptIn(ExperimentalStdlibApi::class)
    private fun connectToDevice(device: BluetoothDevice?) {
        val descriptorWriteQueue: Queue<BluetoothGattDescriptor> = LinkedList()
        bluetoothGatt = device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt?,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(gatt, status, newState)
                connectionStateChange(gatt, newState)
            }

            override fun onDescriptorWrite(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int
            ) {
                super.onDescriptorWrite(gatt, descriptor, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    descriptorWriteQueue.remove()
                    if (descriptorWriteQueue.size > 0) {
                        gatt?.writeDescriptor(descriptorWriteQueue.element())
                    }
                } else {
                    Log.e("DescriptorError", "Failed to write descriptor")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(
                        "onServicesDiscovered",
                        "Device service discovery failed, status: $status"
                    )
                    return
                }

                val serviceUUID_acc = UUID.fromString("0000AAAA-cc7a-482a-984a-7f2ed5b3e58f")
                val characteristicUUID_acc = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")
                val cccDescriptorUUID_acc = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")


                var service = gatt?.getService(serviceUUID_acc)
                var characteristic = service?.getCharacteristic(characteristicUUID_acc)

                if (characteristic != null) {
                    realSpeedBluetoothGattCharacteristic = characteristic
                    val notificationSet = gatt?.setCharacteristicNotification(characteristic, true)
                    Log.d(
                        "AccNotification",
                        "Notification set: $notificationSet, je suis bien abonnée à la notification de acc"
                    )
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@DeviceActivity,
//                            "Abonné à la caractéristique : ${characteristic!!.uuid}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
                    val descriptor = characteristic.getDescriptor(cccDescriptorUUID_acc)
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        descriptorWriteQueue.add(it)
                        if (descriptorWriteQueue.size == 1) {
                            var test = gatt?.writeDescriptor(descriptorWriteQueue.element())
                            Log.d("test Acc", "test: $test")
                        }
                    } ?: Log.e("DescriptorError", "Descriptor is null")
                }

                val characteristicUUID_pulse =
                    UUID.fromString("0000eeee-8e22-4541-9d4c-21edae82ed19")

                service = gatt?.getService(serviceUUID_acc)
                characteristic = service?.getCharacteristic(characteristicUUID_pulse)

                if (characteristic != null) {
                    pulseBluetoothGattCharacteristic = characteristic
                    val notificationSet = gatt?.setCharacteristicNotification(characteristic, true)
                    Log.d(
                        "PulseNotification",
                        "Notification set: $notificationSet, je suis bien abonnée à la notification de pulse"
                    )
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@DeviceActivity,
//                            "Abonné à la caractéristique : ${characteristic.uuid}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
                    val descriptor = characteristic.getDescriptor(cccDescriptorUUID_acc)
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        descriptorWriteQueue.add(it)
                        if (descriptorWriteQueue.size == 1) {
                            var test = gatt?.writeDescriptor(descriptorWriteQueue.element())
                            Log.d("test Pulse", "test: $test")
                        }
                    } ?: Log.e("DescriptorError", "Descriptor is null")
                }
            }


            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                val uuid = characteristic.uuid
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(
                            "BluetoothGattCallback",
                            "Read characteristic $uuid:\n${value.toHexString()}"
                        )
                    }

                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                    }

                    else -> {
                        Log.e(
                            "BluetoothGattCallback",
                            "Characteristic read failed for $uuid, error: $status"
                        )
                    }
                }
            }


            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                Log.d(
                    "onCharacteristicChanged",
                    "UUID: ${characteristic.uuid}, Value: ${characteristic.value}"
                )

                val bleServices = gatt.services // Retrieve the available services on the device
                val service2 = bleServices?.get(2) // Get the third service (index starts from 0)


                val pulseServiceUUID = service2?.uuid // Get the UUID of the service
                Log.e("PulseServiceUUID", "Pulse Service UUID: $pulseServiceUUID")


                val pulseCaract =
                    service2?.characteristics?.get(1) // Get the second characteristic of the service
                val pulseCaractUUID =
                    pulseCaract?.uuid // Get the UUID of the characteristic
                Log.e("PulseCharacteristicUUID", "Pulse Characteristic UUID: $pulseCaractUUID")


//                val serviceUUID1 =
//                    UUID.fromString("0000AAAA-cc7a-482a-984a-7f2ed5b3e58f") // Replace with your actual UUID
                val characteristic1UUID =
                    UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19") // Replace with your actual UUID

//                val service1 =
//                    gatt.getService(serviceUUID1) // Get the service using the service UUID
                val characteristic1 =
                    service2?.getCharacteristic(characteristic1UUID) // Get the characteristic using the characteristic UUID

//                readCharacteristic(gatt, characteristic1)
//                readCharacteristic(gatt, characteristic2)


                val accValue = characteristic1?.value?.let {
                    characteristic1.getIntValue(
                        BluetoothGattCharacteristic.FORMAT_UINT32,
                        0
                    )
                }
                if (characteristic1 != null) {
                    Log.d(
                        "CharacteristicValuee",
                        "UUID: ${characteristic1.uuid}, Value: $accValue"
                    )
                }


                val pulseValue = pulseCaract?.value?.let {
                    pulseCaract.getIntValue(
                        BluetoothGattCharacteristic.FORMAT_UINT8,
                        0
                    )
                }
                if (pulseCaract != null) {
                    Log.d(
                        "CharacteristicValuee",
                        "UUID: ${pulseCaract.uuid}, Value: $pulseValue"
                    )

                }


// Ajoutez autant de caractéristiques que vous voulez lire

                if (pulseCaract!!.uuid == pulseBluetoothGattCharacteristic?.uuid) {
                    Log.e("testje", "UUID pulse: ${characteristic.uuid}")
                    pulseBluetoothGattCharacteristic?.let {

                        it.value?.let { value ->
                            val newPulseValueBytes = value // Get the raw bytes

                            // Convert bytes to integer
                            val newPulseValue = newPulseValueBytes[0].toInt() and 0xFF

                            Log.e("PulseeValue", "Pulse Value: $newPulseValue")

// Get a reference to the Firebase database
                            val database = Firebase.database

// Use the unique id in the reference path
                            val pulseMaxRef =
                                database.getReference("users/course/${this@DeviceActivity.courseId}/vheartBeat")

// Set the maximum pulse value in Firebase
                            pulseMaxRef.setValue(newPulseValue)

// Update course.pulse.value only if newPulseValue is greater
                            if (newPulseValue.toFloat() > course.pulse.value) {
                                course.pulse.value = newPulseValue.toFloat()
                                Log.d("PulseValueMax", "Pulse Value stockéé: ${course.pulse.value}")
                            }
                        } ?: Log.e("PulseeValue", "pulseBluetoothGattCharacteristic value is null")
                    }
                } else {
                    Log.e("PulseValueVlaue", "UUID does not match: ${characteristic.uuid}")
                }

                if (characteristic.uuid == realSpeedBluetoothGattCharacteristic?.uuid) {
                    val newSpeed = convertLittleEndianToFloat(characteristic.value)

                    course.realTimeSpeed.value = newSpeed // Update the value
                    course.speedValues.value =
                        course.speedValues.value + newSpeed // Add the new speed to speedValues

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
                characteristicsQueue.add(characteristic1)
                characteristicsQueue.add(pulseCaract)
            }


            fun requestCharacteristic() {
                if (characteristicsQueue.isNotEmpty()) {
                    val characteristic = characteristicsQueue.peek()
                    bluetoothGatt?.readCharacteristic(characteristic)
                }
            }

            fun handlePulseValue(characteristic: BluetoothGattCharacteristic) {
                val pulseValue =
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                Log.d("PulseValue", "UUID pulse: ${characteristic.uuid}, Value: $pulseValue")
            }
        })
        bluetoothGatt?.connect()
    }

    fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }

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
    fun isCharacteristicReadable(characteristic: BluetoothGattCharacteristic): Boolean {
        return characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
    }

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


