package com.watermeter.equipmentrecorder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.watermeter.equipmentrecorder.ui.theme.WaterMeterEquipmentRecorderTheme

@Composable
fun HomeScreen() {
    WaterMeterEquipmentRecorderTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selamat datang di Water Meter & Equipment Recorder",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ini adalah layar beranda. Silahkan pilih menu dari navigasi bawah.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}