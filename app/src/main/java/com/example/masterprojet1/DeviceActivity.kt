package com.example.masterprojet1


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.UUID


@SuppressLint("MissingPermission")
class DeviceActivity : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var deviceInteraction: DeviceComposableInteraction
    private lateinit var course: Course

    //private var currentLEDStateEnum = LEDStateEnum.NONE

    private var realSpeedBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null

    private lateinit var database: FirebaseDatabase

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<BluetoothDevice?>("device")

        database =
            FirebaseDatabase.getInstance("https://master-42ff9-default-rtdb.europe-west1.firebasedatabase.app/")

        setContent {
            val isStateConnected = remember { mutableStateOf(false) }
            val realTimeSpeed = mutableStateOf(0f)
            val speedValues = mutableStateOf(listOf<Int>())

            deviceInteraction = DeviceComposableInteraction(
                IsConnected = isStateConnected.value,
                deviceTitle = device?.name ?: "Device Unknown",
//                realTimeSpeed = mutableStateOf(0f) // Initialize realTimeSpeed with 0f
            )


            course = Course(
                date = Date(), // You need to provide a date here
                maxSpeed = 0f, // You need to provide a maxSpeed here
                realTimeSpeed = realTimeSpeed,
                speedValues = speedValues
            )

            DeviceDetail(this, mutableStateOf(deviceInteraction), course) {
                connectToDevice(device)
            }
        }
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
                    val newSpeed = intValue.toFloat()
                    course.realTimeSpeed.value = newSpeed // Update the value
                    course.speedValues.value =
                        course.speedValues.value + newSpeed.toInt() // Add the new speed to speedValues

                    //deviceInteraction.speedValues.value += newSpeed.toInt() // Add the new speed to speedValues
                    Log.e("RealTimeSpeed", "Real Time Speed: $newSpeed") // Log the real time speed
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

            database.getReference("users").child("speed").setValue(averageSpeed)


            averageSpeed.toFloat()
        } else {
            Log.d("AverageSpeed", "No speed values available")
            0f
        }
    }

    override fun onStop() {
        super.onStop()
        closeBluetoothGatt()
    }

    private fun closeBluetoothGatt() {
        deviceInteraction.IsConnected = false
        bluetoothGatt?.close()
        bluetoothGatt = null
    }


}