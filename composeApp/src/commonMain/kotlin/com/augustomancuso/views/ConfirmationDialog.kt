package com.augustomancuso.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.augustomancuso.models.InformSellModel
import com.augustomancuso.models.SellItemModel
import io.ktor.client.request.setBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@Composable
fun ConfirmationDialog(
    couroutine: CoroutineScope,
    device: DispositivosDto,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    finalPrice: Double,
    onNavigateHome: () -> Unit,
    selectedAdditionals: Set<String>,
    selectedPersonalizations: Map<String, String>
) {

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
                val client = HttpClient()
                couroutine.launch(Dispatchers.Default) {
                    try {
                        val success = purchaseDevice(
                            client,
                            device,
                            selectedPersonalizations,
                            selectedAdditionals,
                            finalPrice
                        )
                        if (success) {
                            println("SUCESS ${success}")
                            onNavigateHome()
                        }
                    } catch (e: Exception) {
                        println("Exception occurred: ${e.message}")
                    }
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


}


suspend fun purchaseDevice(
    client: HttpClient,
    device: DispositivosDto,
    selectedPersonalizations: Map<String, String>,
    selectedAdditionals: Set<String>,
    finalPrice: Double
): Boolean {
    return try {
        println("Dispositivo: $device")
        val sell = transformDispositivoDtoToInformSellModel(
            device,
            selectedPersonalizations,
            selectedAdditionals,
            finalPrice
        )
        println("VENTA: $sell")
        val response: HttpResponse = client.post("http://localhost:8080/api/device/inform-sell") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer " + TokenStorage.getToken())
            setBody(Json.encodeToString(sell))
        }
        println("Response: ${response.status}")
        response.status == HttpStatusCode.OK
    } catch (e: Exception) {
        println("ERROR ${e.message}")
        false
    }
}

fun transformDispositivoDtoToInformSellModel(
    device: DispositivosDto,
    selectedPersonalizations: Map<String, String>,
    selectedAdditionals: Set<String>,
    finalPrice: Double
): InformSellModel {
    val informSellModel = InformSellModel()
    informSellModel.idDispositivo = device.id
    informSellModel.precioFinal = finalPrice.toFloat()
    device.personalizaciones.forEach { personalization ->
        selectedPersonalizations[personalization.nombre]?.let { selectedOption ->
            val option =
                personalization.opciones.find { it.nombre == selectedOption.split(" - ")[0] }
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
            sellItemModel.precio = additional.precio ?: 0f
            informSellModel.adicionales += sellItemModel
        }
    }

    return informSellModel
}