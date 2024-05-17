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
import java.util.Date
import java.util.UUID
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection

import android.content.Intent

import android.os.IBinder
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@SuppressLint("MissingPermission")
class DeviceActivity_LEDs : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var deviceInteraction: DeviceComposableInteraction
    private lateinit var course: Course

    private var bluetoothService: BluetoothService? = null
    private var device: BluetoothDevice? = null // Define device as a property of DeviceActivity

    private var isConnecting by mutableStateOf(false)
    private var isConnected by mutableStateOf(false)

    //private var currentLEDStateEnum = LEDStateEnum.NONE

    private var deviceConnectionService: DeviceConnectionService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DeviceConnectionService.LocalBinder
            deviceConnectionService = binder.getService()
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
    }

    override fun onStop() {
        super.onStop()
//        super.onStop()
//        unbindService(serviceConnection)
//        deviceConnectionService = null
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

            deviceInteraction = DeviceComposableInteraction(
                IsConnected = isStateConnected.value,
                deviceTitle = device?.name ?: "Device Unknown",
//                realTimeSpeed = mutableStateOf(0f) // Initialize realTimeSpeed with 0f
            )
            if (!isConnecting && !isConnected) {
                Text("Connexion en cours...")
                isConnecting = true
                connectToDevice(device)
            } else if (isConnected) {
                Text("Connexion établie")
                Button(onClick = {
                    val intent = Intent(this@DeviceActivity_LEDs, LEDsParamActivity::class.java)
                    startActivity(intent)
                }) {
                    Text("Paramêtrer les LEDs")
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
        })
        bluetoothGatt?.connect()
    }

    private fun connectionStateChange(gatt: BluetoothGatt?, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt?.discoverServices()
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            isConnected = true
            isConnecting = false
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

//    override fun onStop() {
//        super.onStop()
//        closeBluetoothGatt()
//    }

    private fun closeBluetoothGatt() {
        deviceInteraction.IsConnected = false
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}


