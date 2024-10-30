package com.augustomancuso.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.augustomancuso.models.DispositivosDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
/*
import kotlin.system.exitProcess
*/

@Composable
fun DeviceSaleView(client: HttpClient, token: String, userName: String, onPurchase: (DispositivosDto) -> Unit) {
    var devices by remember { mutableStateOf<List<DispositivosDto>>(emptyList()) }
    var jsonResponse by remember { mutableStateOf<String?>(null) }
    var selectedDevice by remember { mutableStateOf<DispositivosDto?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(token) {
        coroutineScope.launch {
            jsonResponse = fetchDevicesJson(client, token)
            jsonResponse?.let {
                devices = Json.decodeFromString(it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido, $userName") },
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White,
/*                actions = {
                    Button(onClick = { exitProcess(0) }) {
                        Text("Quit")
                    }
                }*/
            )
        }
    ) {
        if (selectedDevice != null) {
            DeviceDetailsView(selectedDevice!!, onPurchase) {
                selectedDevice = null
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color(0xFFF5F5F5))
            ) {
                LazyColumn {
                    items(devices) { device ->
                        DeviceItem(device) {
                            selectedDevice = it
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: DispositivosDto, onSelect: (DispositivosDto) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect(device) },
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(device.nombre, style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
            Text(device.descripcion, color = Color.Gray)
            Text("Precio Base: ${device.precioBase} ${device.moneda}", color = Color.Black)
        }
    }
}

suspend fun fetchDevicesJson(client: HttpClient, token: String): String? {
    val response: HttpResponse = client.get("http://localhost:8080/api/device") {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
    println("Response: ${response.status}")
    return if (response.status == HttpStatusCode.OK) {
        response.bodyAsText()
    } else {
        null
    }
}
