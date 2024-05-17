package com.example.masterprojet1

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import java.util.UUID

class AnkleBraceletParametrization : ComponentActivity() {

    private fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

    private fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
        properties and property != 0

    private var gatt: BluetoothGatt? = null
    private var isConnecting by mutableStateOf(true) // État de la connexion

    private var characteristicValue: ByteArray? = byteArrayOf()
    private var firstcharacteristic: BluetoothGattCharacteristic? = null
    private var secondcharacteristic: BluetoothGattCharacteristic? = null

    private var services: List<BluetoothGattService>? = null
    private var service: BluetoothGattService? = null

    private var characteristicrec: BluetoothGattCharacteristic? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private lateinit var deviceAddress: String

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

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = Intent(this, DeviceConnectionService::class.java)
        startService(intent)
        super.onCreate(savedInstanceState)

        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showAdditionalFields by remember { mutableStateOf(false) }
                    var showAdditionalFieldstwo by remember { mutableStateOf(false) }

                    var checkedState by remember { mutableStateOf(false) }
                    var checkedStatetwo by remember { mutableStateOf(false) }

                    var minSpeed by remember { mutableStateOf("") }
                    var maxSpeed by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ankle Bracelet Parametrization",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        TextField(
                            value = minSpeed,
                            onValueChange = { minSpeed = it },
                            label = { Text("Vitesse minimale") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        TextField(
                            value = maxSpeed,
                            onValueChange = { maxSpeed = it },
                            label = { Text("Vitesse maximale") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Button(
                            onClick = {
                                writetocharac(minSpeed.toInt(), 0)
                                Log.e("firstwrite", "firstwrite")
                                writetocharac(maxSpeed.toInt(), 1)
                                Log.e("secondwrite", "secondwrite")
                                finish()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("OK, faire la course")
                        }

                        Text(
                            text = "L'accélérometre est activé par défaut, obligatoire pour les différentes mesures",
                            //style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

    }

    fun writetocharac(value: Int, nb: Int) {
        // Convertir l'entier en tableau de bytes
        Log.e("dans fonction writetocharac","writetochara")
        val valueToWrite = byteArrayOf(value.toByte())
        // Écrire la valeur dans la caractéristique BLE
        writeValueToCharacteristic(valueToWrite, nb)
    }

    object AnkleBraceletUtils {
        @SuppressLint("MissingPermission")
        fun writeToCharac(context: Context, value: Int, nb: Int) {
            val ankleBraceletActivity = context as AnkleBraceletParametrization
            ankleBraceletActivity.writetocharac(value, nb)
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        Log.e("connect", "connectbis")
        gatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.e("callbackgatt","Callback gatt")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("callbackgatt","Callback gatt connected")
                isConnecting = false // La connexion est établie
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
                    if(services!!.size >= 3){
                        val thirdService = services!![2] // Troisième service (index 2)
                        val characteristics = thirdService.characteristics
                        if (characteristics.isNotEmpty()) {
                            firstcharacteristic = characteristics[0] // Première caractéristique du troisième service
                            secondcharacteristic = characteristics[1] // Première caractéristique du troisième service
                            Log.e("charac","Acces à la première caractéristique du troisième service")
                            // Vous pouvez maintenant interagir avec la première caractéristique

                            // Activer les notifications pour cette caractéristique
                            gatt?.setCharacteristicNotification(firstcharacteristic, true)
                            gatt?.setCharacteristicNotification(secondcharacteristic, true)

                            val FirstCharacteristic = firstcharacteristic
                            val SecondCharacteristic = secondcharacteristic

                            // Rechercher le descripteur de notification pour activer les notifications
                            val descriptor_first = FirstCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            val descriptor_second = SecondCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            // Activer les notifications pour ce descripteur
                            descriptor_first?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt?.writeDescriptor(descriptor_first)
                            descriptor_second?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt?.writeDescriptor(descriptor_second)

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

    @SuppressLint("MissingPermission")
    private fun writeValueToCharacteristic(value: ByteArray, nb: Int) {
        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null
        if (gatt != null) {
            // Récupérez le troisième service
            Log.e("write1", "write1")
            service = gatt?.services?.get(1)
            // Troisième service (index 2)
            Log.e("write2", "write2")

            // Vérifiez si le service et ses caractéristiques sont valides
            if (service != null && service!!.characteristics.isNotEmpty()) {
                Log.e("write3", "write3")
                // Récupérez la première caractéristique du troisième service
                val characteristic =
                    service!!.characteristics[nb] // Première caractéristique du troisième service
                //characteristicrec = service!!.characteristics[1]
                // Vérifiez si la caractéristique est valide
                if (characteristic != null) {
                    // Écrivez la valeur dans la caractéristique
                    Log.e("write4", "write4")
                    characteristic.value = value
                    //Log.d("CharacteristicValue", "Valeur de la value : $value")
                    //Log.d("CharacteristicValue", "Valeur de la caractéristique : ${characteristic.value?.contentToString()}")
                    gatt?.writeCharacteristic(characteristic)
                } else {
                    Log.e("writeValueToCharacteristic", "Caractéristique non valide")
                }
            } else {
                Log.e(
                    "writeValueToCharacteristic",
                    "Aucune caractéristique dans le troisième service"
                )
            }
        } else {
            Log.e("writeValueToCharacteristic", "Connexion Bluetooth non établie")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.disconnect()
        gatt?.close()
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

    @SuppressLint("MissingPermission")
    fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        Log.e("enable", "enablenotif")
        val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> {
                Log.e(
                    "ConnectionManager",
                    "${characteristic.uuid} doesn't support notifications/indications"
                )
                return
            }
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false) {
                Log.e(
                    "ConnectionManager",
                    "setCharacteristicNotification failed for ${characteristic.uuid}"
                )
                return
            }
            Log.e("writedescr", "writedescriptor")
            writeDescriptor(cccDescriptor, payload)
        } ?: Log.e(
            "ConnectionManager",
            "${characteristic.uuid} doesn't contain the CCC descriptor!"
        )
    }

    @SuppressLint("MissingPermission")
    fun disableNotifications(characteristic: BluetoothGattCharacteristic) {
        if (!characteristic.isNotifiable() && !characteristic.isIndicatable()) {
            Log.e(
                "ConnectionManager",
                "${characteristic.uuid} doesn't support indications/notifications"
            )
            return
        }

        val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, false) == false) {
                Log.e(
                    "ConnectionManager",
                    "setCharacteristicNotification failed for ${characteristic.uuid}"
                )
                return
            }
            writeDescriptor(cccDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        } ?: Log.e(
            "ConnectionManager",
            "${characteristic.uuid} doesn't contain the CCC descriptor!"
        )
    }

//    fun writeData() {
//        val minSpeed = minSpeed.toInt()
//        val maxSpeed = maxSpeed.toInt()
//        val buzzer = checkedState
//        val heartRateSensor = checkedStatetwo
//
//        deviceConnectionService?.writeData(minSpeed, maxSpeed, buzzer, heartRateSensor)
//    }
}