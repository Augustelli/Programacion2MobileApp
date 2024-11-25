package com.augustomancuso.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.augustomancuso.TokenStorage
import com.augustomancuso.models.DispositivosDto
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI

import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.augustomancuso.models.InformSellModel
import com.augustomancuso.models.SellItemModel


@Composable
fun ConfirmationDialog(
    device: DispositivosDto,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    finalPrice: Double,
    onNavigateHome: () -> Unit,
    selectedAdditionals: Set<String>,
    selectedPersonalizations: Map<String, String>
) {
    val coroutineScope = rememberCoroutineScope()
    val showSuccessDialog = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Confirmar Compra") },
        text = {
            Column {
                Text("¿Está seguro de que desea comprar el siguiente dispositivo?")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dispositivo: ${device.nombre}")
                Text("Precio: ${"%.2f".format(finalPrice)} ${device.moneda}")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                val client = HttpClient() // Ensure you have an instance of HttpClient
                val token = TokenStorage.getToken()
                if (token != null) {
                    coroutineScope.launch {
                        val success = purchaseDevice(client, device, selectedPersonalizations, selectedAdditionals, finalPrice)
                        if (success) {
                            showSuccessDialog.value = true
                        }
                    }
                } else {
                    // Handle the case where the token is not available
                }
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )

    if (showSuccessDialog.value) {
        Dialog(onDismissRequest = { showSuccessDialog.value = false }) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog.value = false },
                title = { Text("Compra Exitosa") },
                text = { Text("La compra se ha realizado con éxito.") },
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog.value = false
                        onNavigateHome()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@OptIn(InternalAPI::class)
suspend fun purchaseDevice(client: HttpClient, device: DispositivosDto, selectedPersonalizations: Map<String, String>, selectedAdditionals: Set<String>, finalPrice: Double): Boolean {
    println("Dispositivo: $device");
    var sell = transformDispositivoDtoToInformSellModel(device, selectedPersonalizations, selectedAdditionals, finalPrice)
    println("VENTA: " + sell)
    val response: HttpResponse = client.post("http://localhost:8080/api/device/inform-sell") {
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer " + TokenStorage.getToken())
        body = Json.encodeToString(sell)
        print("BODY: " + body)

    }
    println("Response: ${response.status}")
    return response.status == HttpStatusCode.OK
}

fun transformDispositivoDtoToInformSellModel(device: DispositivosDto, selectedPersonalizations: Map<String, String>, selectedAdditionals: Set<String>, finalPrice: Double): InformSellModel {
    val informSellModel = InformSellModel()
    informSellModel.idDispositivo = device.id
    informSellModel.precioFinal = finalPrice.toFloat()
    device.personalizaciones.forEach { personalization ->
        selectedPersonalizations[personalization.nombre]?.let { selectedOption ->
            val option = personalization.opciones.find { it.nombre == selectedOption.split(" - ")[0] }
            option?.let {
                val sellItemModel = SellItemModel()
                sellItemModel.id = it.id
                sellItemModel.precio = option.precioAdicional.toFloat()
                informSellModel.personalizaciones += sellItemModel
            }
        }
    }

    device.adicionales.forEach { additional ->
        if (selectedAdditionals.contains("${additional.id} - ${additional.descripcion}")) {
            val sellItemModel = SellItemModel()
            sellItemModel.id = additional.id
            sellItemModel.precio = additional.precio
            informSellModel.adicionales += sellItemModel
        }
    }

    return informSellModel
}