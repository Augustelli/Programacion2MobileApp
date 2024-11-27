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
import com.augustomancuso.models.InformSellModel
import com.augustomancuso.models.VentaDetalleDto
import com.augustomancuso.models.VentaDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@Composable
fun DeviceSaleView(
    client: HttpClient,
    token: String,
    userName: String,
    onPurchase: (DispositivosDto) -> Unit
) {
    var devices by remember { mutableStateOf<List<DispositivosDto>>(emptyList()) }
    var jsonResponse by remember { mutableStateOf<String?>(null) }
    var selectedDevice by remember { mutableStateOf<DispositivosDto?>(null) }
    var showPurchasedDevices by remember { mutableStateOf(false) }
    var showPurchasedDevicesDetail by remember { mutableStateOf(false) }
    var purchasedDevices by remember { mutableStateOf<List<VentaDto>>(emptyList()) }
    var selectedSale by remember { mutableStateOf<VentaDto?>(null) }
    var selectedSaleDetail by remember { mutableStateOf<VentaDetalleDto?>(null) }
    var showModal by remember { mutableStateOf(false) }
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
                actions = {
                    Button(onClick = {
                        coroutineScope.launch {
                            purchasedDevices = fetchPurchasedDevices(client, token)
                            showPurchasedDevices = true
                        }
                    }) {
                        Text("Ver Compras")
                    }
                }
            )
        }
    ) {
        when {
            selectedDevice != null -> {
                DeviceDetailsView(selectedDevice!!, onPurchase) {
                    selectedDevice = null
                }
            }

            showPurchasedDevices -> {
                PurchasedDevicesView(purchasedDevices, onBack = { showPurchasedDevices = false }) { sale ->
                    coroutineScope.launch {
                        val saleDetails = fetchSaleDetails(client, sale.idVenta, token)
                        selectedSaleDetail = saleDetails
                        showModal = true
                    }
                }
            }

            else -> {
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

        if (showModal && selectedSaleDetail != null) {
            SaleDetailsModal(sale = selectedSaleDetail!!) {
                showModal = false
            }
        }
    }
}

@Composable
fun PurchasedDevicesView(purchasedDevices: List<VentaDto>, onBack: () -> Unit, onSelect: (VentaDto) -> Unit) {
    Column {
        Button(onClick = onBack) {
            Text("Volver")
        }
        LazyColumn {
            items(purchasedDevices) { sale ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 8.dp,
                    backgroundColor = Color(0xFFF5F5F5)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Venta: ${sale.nombre}",
                            style = MaterialTheme.typography.h6,
                            color = Color(0xFF6200EE)
                        )
                        Text(
                            "Descripcion: ${sale.descripcion}",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray
                        )
                        Text(
                            "Precio: ${sale.precio}",
                            style = MaterialTheme.typography.body2,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onSelect(sale) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)),
                        ) {
                            Text("Ver Detalles", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaleDetailsModal(sale: VentaDetalleDto, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Detalles de la Venta") },
        text = {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    Text("Nombre: ${sale.nombre}", style = MaterialTheme.typography.h6)
                    Text("Descripción: ${sale.descripcion}", style = MaterialTheme.typography.body1)
                    Text("Precio Base: ${sale.precioBase}", style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Text("Características:", style = MaterialTheme.typography.h6)
                    sale.caracteristicas.forEach { item ->
                        Text("${item.nombre}: ${item.descripcion}", style = MaterialTheme.typography.body2)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Text("Personalizaciones:", style = MaterialTheme.typography.h6)
                    sale.personalizaciones.forEach { item ->
                        Text("Tipo: ${item.nombre}", style = MaterialTheme.typography.body2)
                        Text("Nombre: ${item.opcion.nombre}", style = MaterialTheme.typography.body2)
                        Text("Descripción: ${item.opcion.descripcion}", style = MaterialTheme.typography.body2)
                        Text("Precio adicional: ${item.opcion.precioAdicional}", style = MaterialTheme.typography.body2)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Text("Adicionales:", style = MaterialTheme.typography.h6)
                    sale.adicionales.forEach { item ->
                        Text("Tipo: ${item.nombre}", style = MaterialTheme.typography.body2)
                        Text("Descripción: ${item.descripcion}", style = MaterialTheme.typography.body2)
                        Text("Precio: ${item.precio}", style = MaterialTheme.typography.body2)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}




@Composable
fun SaleDetailsView(sale: VentaDetalleDto, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Text(
            "${sale.nombre}",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF6200EE)
        )
        Text("Descripción: ${sale.descripcion}", color = Color.Black)
        Text("Precio Base: ${sale.precioBase}", color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Características:", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
        sale.caracteristicas.forEach { item ->
            Text("${item.nombre} ${item.descripcion}", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Personalizaciones:", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
        sale.personalizaciones.forEach { item ->
            Text("Tipo: ${item.nombre}", color = Color.Black)
            Text("Nombre: ${item.opcion.nombre}", color = Color.Black)
            Text("Descripción: ${item.opcion.descripcion}", color = Color.Black)
            Text("Precio adicional: ${item.opcion.precioAdicional}", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Adicionales:", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
        sale.adicionales.forEach { item ->
            Text("Tipo: ${item.nombre}", color = Color.Black)
            Text("Descripción: ${item.descripcion}", color = Color.Black)
            Text("Precio: ${item.precio}", color = Color.Black)

        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

suspend fun fetchPurchasedDevices(client: HttpClient, token: String): List<VentaDto> {
    val response: HttpResponse = client.get("http://localhost:8080/api/device/sells") {
        header(HttpHeaders.Authorization, "Bearer $token")

    }
    println("Response: ${response.status}")
    return if (response.status == HttpStatusCode.OK) {
        Json.decodeFromString(response.bodyAsText())

    } else {
        emptyList()
    }
}

suspend fun fetchSaleDetails(client: HttpClient, saleId: Int, token: String): VentaDetalleDto? {
    val response: HttpResponse = client.get("http://localhost:8080/api/device/sells/$saleId") {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
    return if (response.status == HttpStatusCode.OK) {
        println("Response: ${response.status}" + response.bodyAsText())
        Json.decodeFromString(response.bodyAsText())
    } else {
        null
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
    println("RESPUESTA DISPOSITIVOS: ${response.bodyAsText()}")
    return if (response.status == HttpStatusCode.OK) {
        response.bodyAsText()
    } else {
        null
    }
}
