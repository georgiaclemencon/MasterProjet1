package com.example.masterprojet1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DeviceConnectionService : Service() {

    private var deviceConnected: Boolean = false

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): DeviceConnectionService = this@DeviceConnectionService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Gérer la connexion à l'appareil ici
        connectToDevice()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // Gérer la déconnexion de l'appareil ici
        disconnectFromDevice()
    }

    private fun connectToDevice() {
        // Votre logique de connexion à l'appareil ici
        deviceConnected = true
    }

    private fun disconnectFromDevice() {
        // Votre logique de déconnexion de l'appareil ici
        deviceConnected = false
    }

    fun isDeviceConnected(): Boolean {
        return deviceConnected
    }
}