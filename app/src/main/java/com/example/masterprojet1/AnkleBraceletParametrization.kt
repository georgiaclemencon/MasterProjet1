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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import java.nio.ByteBuffer
import java.util.UUID

class AnkleBraceletParametrization : ComponentActivity() {

    private lateinit var deviceInteraction: DeviceComposableInteraction

    private var bluetoothGatt: BluetoothGatt? = null

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

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        startService(intent)
        super.onCreate(savedInstanceState)

        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var context = LocalContext.current
                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Text("SPR MENU", modifier = Modifier.padding(16.dp))
                                Divider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Profile") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, ProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Start a training") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ScanActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Historique") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, DeviceActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Ankle Bracelet Parametrization") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(
                                            context,
                                            AnkleBraceletParametrization::class.java
                                        )
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "LEDs Ribbon Parametrization") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, DeviceActivity_LEDs::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Help") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, HelpActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Who are we ? What is SoundPaceRunners ?") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, CreatorsActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Contact Form") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, ContactForm::class.java)
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    ) {
                        val isStateConnected = remember { mutableStateOf(false) }

                        val device = intent.getParcelableExtra<BluetoothDevice>("device")
                        if (device != null) {
                            Log.e("devicejusteapresintent", "$device")
                        } else {
                            Log.e("DeviceActivity", "BluetoothDevice est null")
                        }

                        deviceInteraction = DeviceComposableInteraction(
                            IsConnected = isStateConnected.value,
                            deviceTitle = device?.name ?: "Device Unknown",
//                realTimeSpeed = mutableStateOf(0f) // Initialize realTimeSpeed with 0f
                        )

                        // Dans l'activité AnkleBraceletParametrization


                        var showAdditionalFields by remember { mutableStateOf(false) }
                        var showAdditionalFieldstwo by remember { mutableStateOf(false) }

                        var checkedState by remember { mutableStateOf(false) }
                        var checkedStatetwo by remember { mutableStateOf(false) }

                        var minSpeed by remember { mutableStateOf("7.30") }
                        var maxSpeed by remember { mutableStateOf("6") }
                        var active by remember { mutableStateOf("1") }


                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Log.e("device", "$device")
                            if (device != null) {
                                connectToDevice(device)
                            }
                            Text(
                                text = "Ankle Bracelet Parametrization",
                                //style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            TextField(
                                value = minSpeed,
                                onValueChange = { minSpeed = it },
                                label = { Text("Vitesse minimale (min/km)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            TextField(
                                value = maxSpeed,
                                onValueChange = { maxSpeed = it },
                                label = { Text("Vitesse maximale (min/km)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Button(
                                onClick = {
                                    Log.e("minSpeed.toFloat()","${minSpeed.toFloat()}")
                                    writeValueToCharacteristic(minSpeed, 0)
                                    //writetocharac_float(minSpeed.toFloat(), 0)
                                    Log.e("firstwrite", "firstwrite")
                                    Log.e("maxSpeed.toFloat()","${maxSpeed.toFloat()}")
                                    writeValueToCharacteristic(maxSpeed, 1)
                                    writeValueToCharacteristic(active, 2)
                                    //writeValueToCharacteristic_buz("1",0)
                                    //writetocharac_float(maxSpeed.toFloat(), 1)
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
        val intent = Intent(this, BluetoothService::class.java)
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
                this@AnkleBraceletParametrization.gatt = gatt // Set the gatt here
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
                        val SecondService = services!![2] // Troisième service (index 2)
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
/*
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

    fun writetocharac_float(value: Float, nb: Int) {
        // Convertir le float en tableau de bytes
        Log.e("dans fonction writetocharac", "writetochara")
        Log.e("dans fonction writetocharac", "gatt : $gatt")
        Log.e("valuetobyte", "$value")

        val byteArray = floatToByteArray(value)
        Log.e("avant writee", "avantwritechar")

        // Écrire la valeur dans la caractéristique BLE
        writeValueToCharacteristic(byteArray, nb)
    }

    fun floatToByteArray(value: Float): ByteArray {
        // Utiliser ByteBuffer pour convertir le float en un tableau de bytes
        return ByteBuffer.allocate(4).putFloat(value).array()
    }

    @SuppressLint("MissingPermission")
    fun writeValueToCharacteristic(value: ByteArray, nb: Int) {
        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null
        if (gatt != null) {
            // Vérifiez si la liste des services est disponible
            if (services != null) {
                // Vous pouvez accéder aux services ici
                Log.e("serviceespasnull","seervices")
                service = gatt?.services?.get(2)
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
    */

    @SuppressLint("MissingPermission")
    fun writeValueToCharacteristic(value: String, nb: Int) {
        // Convertir la chaîne en ByteArray
        val byteArray = value.toByteArray(Charsets.UTF_8)

        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null
        if (gatt != null) {
            // Vérifiez si la liste des services est disponible
            if (services != null) {
                // Vous pouvez accéder aux services ici
                Log.e("serviceespasnull","services")
                service = gatt?.services?.get(5)
                Log.e("deuxiemeservice","$service")
                Log.e("uuid service","${service?.uuid}")
                // Troisième service (index 2)
                Log.e("write2","write2")

                // Vérifiez si le service et ses caractéristiques sont valides
                if (service != null && service!!.characteristics.isNotEmpty()) {
                    Log.e("write3","write3")
                    val numberOfCharacteristics = service?.characteristics?.size ?: 0
                    Log.e("sizeecharac","$numberOfCharacteristics")
                    // Récupérez la première caractéristique du deuxième service
                    val characteristic = service!!.characteristics[nb] // Première caractéristique du deuxième service
                    Log.e("charac uuid","${characteristic.uuid}")
                    // Vérifiez si la caractéristique est valide
                    if (characteristic != null) {
                        // Écrivez la valeur dans la caractéristique
                        Log.e("write4","write4")

                        characteristic.value = byteArray
                        Log.d("CharacteristicValue", "Valeur de la chaîne : $value")
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

    @SuppressLint("MissingPermission")
    fun writeValueToCharacteristic_buz(value: String, nb: Int) {
        // Convertir la chaîne en ByteArray
        val byteArray = value.toByteArray(Charsets.UTF_8)

        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null
        if (gatt != null) {
            // Vérifiez si la liste des services est disponible
            if (services != null) {
                // Vous pouvez accéder aux services ici
                Log.e("serviceespasnull","services")
                service = gatt?.services?.get(4)
                Log.e("deuxiemeservice","$service")
                Log.e("uuid service","${service?.uuid}")
                // Troisième service (index 2)
                Log.e("write2","write2")

                // Vérifiez si le service et ses caractéristiques sont valides
                if (service != null && service!!.characteristics.isNotEmpty()) {
                    Log.e("write3","write3")
                    val numberOfCharacteristics = service?.characteristics?.size ?: 0
                    Log.e("sizeecharac","$numberOfCharacteristics")
                    // Récupérez la première caractéristique du deuxième service
                    val characteristic = service!!.characteristics[nb] // Première caractéristique du deuxième service
                    Log.e("charac uuid","${characteristic.uuid}")
                    // Vérifiez si la caractéristique est valide
                    if (characteristic != null) {
                        // Écrivez la valeur dans la caractéristique
                        Log.e("write4","write4")

                        characteristic.value = byteArray
                        Log.d("CharacteristicValue", "Valeur de la chaîne : $value")
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

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.disconnect()
        gatt?.close()
        unbindService(serviceConnection)
    }

    @SuppressLint("MissingPermission")
    fun closeBluetoothGatt() {
        deviceInteraction.IsConnected = false
        bluetoothGatt?.close()
        bluetoothGatt = null
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

//    fun writeData() {
//        val minSpeed = minSpeed.toInt()
//        val maxSpeed = maxSpeed.toInt()
//        val buzzer = checkedState
//        val heartRateSensor = checkedStatetwo
//
//        deviceConnectionService?.writeData(minSpeed, maxSpeed, buzzer, heartRateSensor)
//    }
}