package com.augustomancuso.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.augustomancuso.models.DispositivosDto

@Composable
fun DeviceItemView(device: DispositivosDto, onSelect: (DispositivosDto) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onSelect(device) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(device.nombre, style = MaterialTheme.typography.h6)
            Text(device.descripcion)
            Text("Precio Base: ${device.precioBase} ${device.moneda} ")
        }
    }
}
