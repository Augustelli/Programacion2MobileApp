package com.augustomancuso.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.augustomancuso.models.DispositivosDto
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ConfirmationDialog(
    device: DispositivosDto,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Confirmar Compra") },
        text = {
            Column {
                Text("¿Está seguro de que desea comprar el siguiente dispositivo?")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dispositivo: ${device.nombre}")
                Text("Precio: ${device.precioBase} ${device.moneda}")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(InternalAPI::class)
suspend fun purchaseDevice(client: HttpClient, device: DispositivosDto, token: String): Boolean {
    println("Dispositivo: $device")
    val response: HttpResponse = client.post("http://localhost:8080/api/purchase") {
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer $token")
        body = Json.encodeToString(device)
    }
    return response.status == HttpStatusCode.OK
}