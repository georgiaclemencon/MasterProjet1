package com.example.masterprojet1


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var gatt: BluetoothGatt? = null
    private var isConnecting by mutableStateOf(true) // État de la connexion

    private var characteristicValue: ByteArray? = byteArrayOf()
    private var firstcharacteristic: BluetoothGattCharacteristic? = null
    private var secondcharacteristic: BluetoothGattCharacteristic? = null

    private var services: List<BluetoothGattService>? = null
    private var service: BluetoothGattService? = null

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
        newCourseIntent.putExtra(
            "courseId",
            courseId
        ) // Replace "your_course_id" with the actual course id
        Log.e("DeviceActivity", "Putting courseId in intent: $courseId")
    }

    fun isDeviceConnected(): Boolean {
        return deviceConnectionService?.isDeviceConnected() ?: false
    }


    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<BluetoothDevice?>("device")
        Log.d("DeviceActivity", "Device: $device")

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
                DeviceDetail(this, it, mutableStateOf(deviceInteraction), course, device){
                    if (device != null) {
                        Log.e("avant le connect","avant le connect")
                        connectToDevice(device)
                    }
                }
            }
        }

        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        Log.e("deviceconnectotdevice","$device")
        Log.e("connect", "connectbis")
        gatt = device.connectGatt(this, false, gattCallback)
        Log.d("GattConnection", "Gatt: $gatt")
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.e("callbackgatt","Callback gatt")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("callbackgatt","Callback gatt connected")
                isConnecting = false // La connexion est établie
                this@DeviceActivity.gatt = gatt // Set the gatt here
                gatt?.discoverServices() // Découvrir les services après la connexion
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.e("onservices","OnServiceDiscovered")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                services = gatt?.services
                if (services != null && services!!.isNotEmpty()) {
                    Log.e("ServiceListSize", "Nombre de services découverts : ${services!!.size}")
                    if(services!!.size >= 2){
                        val SecondService = services!![1] // Troisième service (index 2)
                        Log.e("2eme srevicee","$SecondService")
                        val characteristics = SecondService.characteristics
                        if (characteristics.isNotEmpty()) {
                            val firstCharacteristic = characteristics[0] // Première caractéristique du troisième service
                            Log.e("2eme srevicee 1ere charac","$firstCharacteristic")
                            Log.e("charac","Acces à la première caractéristique du troisième service")
                            // Vous pouvez maintenant interagir avec la première caractéristique

                            // Activer les notifications pour cette caractéristique
                            gatt?.setCharacteristicNotification(firstCharacteristic, true)

                            // Rechercher le descripteur de notification pour activer les notifications
                            val descriptor = firstCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            // Activer les notifications pour ce descripteur
                            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt?.writeDescriptor(descriptor)


                        } else {
                            // Aucune caractéristique dans le troisième service
                            Log.e("charac","Aucune caractéristique dans le troisième service")
                        }
                    }
                    else{
                        Log.e("charac","sors du if")
                    }

                } else {
                    // L'appareil ne dispose pas de suffisamment de services
                    Log.e("charac","L'appareil ne dispose pas de suffisamment de services")
                }
            } else {
                // Erreur lors de la découverte des services
                Log.e("charac","Erreur lors de la découverte des services")
            }
        }
        @OptIn(ExperimentalStdlibApi::class)
        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")
            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val newValueHex = value.toHexString()
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: $newValueHex")
            }
        }
    }

    fun writetocharac(value: Int, nb: Int) {
        // Convertir l'entier en tableau de bytes
        Log.e("dans fonction writetocharac", "writetochara")
        Log.e("dans fonction writetocharac", "gatt : $gatt")
        Log.e("valuetobyte", "${value}")
        val byteArray = intToByteArray(value)
        Log.e("avant writee", "avantwritechar")
        // Écrire la valeur dans la caractéristique BLE

        writeValueToCharacteristic(byteArray, nb)
    }

    fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(value.toByte())
    }

    fun writeValueToCharacteristic(value: ByteArray, nb: Int) {
        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null

        if (gatt != null) {
            // Vérifiez si la liste des services est disponible
            if (services != null) {
                // Vous pouvez accéder aux services ici
                Log.e("serviceespasnull","seervices")
                service = gatt?.services?.get(3)
                Log.e("deuxiemeservice","$service")
                Log.e("uuid service","${service?.uuid}")
                // Troisième service (index 2)
                Log.e("write2","write2")

                // Vérifiez si le service et ses caractéristiques sont valides
                if (service != null && service!!.characteristics.isNotEmpty()) {
                    Log.e("write3","write3")
                    // Récupérez la première caractéristique du deuxieme service
                    val characteristic = service!!.characteristics[nb] // Première caractéristique du deuxieme service
                    Log.e("charac uuid","${characteristic.uuid}")
                    // Vérifiez si la caractéristique est valide
                    if (characteristic != null) {
                        // Écrivez la valeur dans la caractéristique
                        Log.e("write4","write4")

                        characteristic.value = value
                        Log.d("CharacteristicValue", "Valeur de la value : $value")
                        Log.d("CharacteristicValue", "Valeur de la caractéristique : ${characteristic.value.contentToString()}")
                        gatt?.writeCharacteristic(characteristic)
                    } else {
                        Log.e("writeValueToCharacteristic", "Caractéristique non valide")
                    }
                } else {
                    Log.e("writeValueToCharacteristic", "Aucune caractéristique dans le troisième service")
                }
            } else {
                Log.e("writeValueToCharacteristic", "Liste des services non disponible")
            }
        } else {
            Log.e("writeValueToCharacteristic", "Connexion Bluetooth non établie")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gatt?.disconnect()
        gatt?.close()
        unbindService(serviceConnection)
    }

    @SuppressLint("MissingPermission")
    fun writeDescriptor(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        gatt?.let { gatt ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, payload)
            } else {
                // Fall back to deprecated version of writeDescriptor for Android <13
                gatt.legacyDescriptorWrite(descriptor, payload)
            }
        } ?: error("Not connected to a BLE device!")
    }
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.S)
    @Suppress("DEPRECATION")
    private fun BluetoothGatt.legacyDescriptorWrite(
        descriptor: BluetoothGattDescriptor,
        value: ByteArray
    ) {
        descriptor.value = value
        writeDescriptor(descriptor)
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


