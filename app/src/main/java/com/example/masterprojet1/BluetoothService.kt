package com.example.masterprojet1

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database

class BluetoothService : Service() {
    private var bluetoothGatt: BluetoothGatt? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice?) {
        bluetoothGatt = device?.connectGatt(this, true, object : BluetoothGattCallback() {
            // Your callback code here...
        })
        bluetoothGatt?.connect()
    }

    @SuppressLint("MissingPermission")
    fun disconnectFromDevice() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }


    fun updateDatabaseWithSpeedValues(newSpeed: Float) {
    // Get a reference to the speed list in Firebase
    val database = Firebase.database
    val speedListRef = database.getReference("users/course/vitesse")

    // Add the new speed value to the list
    speedListRef.setValue(newSpeed)
    Log.e("BluetoothService", "Speed values added to Firebase: $newSpeed")
}
}