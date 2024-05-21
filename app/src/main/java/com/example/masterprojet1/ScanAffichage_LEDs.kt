package com.example.masterprojet1


import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp




@Composable
fun ScanActivityUI_LEDs(
    scanInteraction_LEDs: ScanComposableInteraction_LEDs
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Scan Activity LEDs", modifier = Modifier.padding(16.dp))
        Image(
            painter = painterResource(
                id = if (scanInteraction_LEDs.isSquareIcon.value) R.drawable.soundpacerunnerslogo else R.drawable.ampoule_vide
            ),
            contentDescription = "Scan Icon",
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
                .clickable(onClick = {
                    scanInteraction_LEDs.toggleButtonPlayScan_LEDs(scanInteraction_LEDs.deviceResults)
                })
        )

        Text(
            text = if (scanInteraction_LEDs.isScanning.value) "Scanning..." else "Click the icon to start scanning",
            modifier = Modifier.padding(16.dp)
        )
        if (scanInteraction_LEDs.isScanning.value) {
            LinearProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
        DisplayDevices_LEDs(scanInteraction_LEDs.isScanning, scanInteraction_LEDs.deviceResults )
    }
}


//
//@SuppressLint("UnrememberedMutableState")
//@Composable
//fun DisplayBluetoothStatus(scanComposableInteraction: ScanComposableInteraction) {
//    when (scanComposableInteraction.hasBLEIssue.value) {
//        "not_supported" -> BluetoothNotSupportedScreen()
//        "disabled" -> BluetoothDisabledScreen()
//        "enabled" -> {
//            ScanActivityUI(scanComposableInteraction)
//        }
//    }
//}

// Display the list of connected devices
//@Composable
//fun DisplayDevices(isScanning: Boolean, devices: List<String>) {
//    if (isScanning) {
//        devices.forEach { device ->
//            Text(text = device, modifier = Modifier.padding(16.dp))
//        }
//    }
//}

//@Composable
//fun ScanActivityContent(scanInteraction: ScanComposableInteraction) {
//    ScanActivityUI(scanInteraction)
//}

@Composable
fun BluetoothDisabledScreen_LEDs() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ampoule_vide),
            contentDescription = "Bluetooth Disabled Logo",
            modifier = Modifier.size(150.dp)
        )
        Text(text = "Votre Bluetooth n'est pas activé", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun BluetoothNotSupportedScreen_LEDs() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ampoule_vide),
            contentDescription = "Bluetooth Not Supported Logo",
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = "Votre appareil ne supporte pas le Bluetooth",
            modifier = Modifier.padding(16.dp)
        )
    }
}


//data class Device(val name: String, val macAddress: String, val distance: Int)
//
//val fakeDevices = listOf(
//    Device("Device 1", "00:00:00:00:00:00", 5),
//    Device("Device 2", "00:00:00:00:00:01", 10),
//    Device("Device 3", "00:00:00:00:00:02", 15),
//    Device("Device 4", "00:00:00:00:00:03", 20)
//)

class ScanComposableInteraction_LEDs(
    var isScanning: MutableState<Boolean>,
    var isSquareIcon: MutableState<Boolean>,
    var deviceResults: MutableList<ScanResult>,
    val hasBLEIssue: MutableState<String>,
//    val onIconClick: Unit,
    val bleScanManager: BleScanManager
) {
    fun toggleButtonPlayScan_LEDs(scanResults: List<ScanResult>) {
        if (isScanning.value) {
            isScanning.value = false
            stopBleScan_LEDs()
        } else {
            isScanning.value = true
            startBleScan_LEDs()
        }
        isSquareIcon.value = !isSquareIcon.value

        // If scanning is enabled, display the devices
//        if (isScanning.value) {
//            deviceResults.addAll(scanResults)
//        } else {
//            deviceResults.clear()
//        }toggleButtonPlayScan_LEDs
    }

    private fun startBleScan_LEDs() {
        deviceResults.clear()
        bleScanManager.scanBleDevices()
    }

    private fun stopBleScan_LEDs() {
        bleScanManager.stopBleScan()
    }
}


@Composable
@SuppressLint("MissingPermission")
fun DisplayDevices_LEDs(isScanning: MutableState<Boolean>, results: MutableList<ScanResult>) {
    val context = LocalContext.current // Get the current context
    LazyColumn {
        items(results) { result ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        // Create an Intent to start DeviceActivity
                        val intent = Intent(context, LEDsParamActivity::class.java)
                        // Put the device address as an extra in the Intent
                        intent.putExtra("device", result.device)
                        intent.putExtra("device_address", result.device.address)
                        intent.putExtra("device_name", result.device.name)
                        intent.putExtra("device_rssi", result.rssi)

                        // Start DeviceActivity
                        //val intentbis = Intent(context, AnkleBraceletParametrization::class.java)
                        // Put the device address as an extra in the Intent
                        //intentbis.putExtra("device", result.device)

                        context.startActivity(intent)
                    }
            ) {
                DisplayDeviceUnit_LEDs(result)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DistanceIndicator_LEDs(distance: Int) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(color = if (distance < 1) Color.Green else Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$distance m",
            color = Color.White,

            )
    }
}
@SuppressLint("MissingPermission")
@Composable
fun DisplayDeviceUnit_LEDs(device: ScanResult) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Nom du périphérique : ${device.device.name ?: "Inconnu"}")
        Text("Adresse MAC : ${device.device.address}")
        Text("Force du signal (RSSI) : ${device.rssi} dBm")
        Text("Services annoncés : ${device.scanRecord?.serviceUuids?.joinToString() ?: "Aucun"}")
        DistanceIndicator_LEDs(device.rssi)
    }
}

