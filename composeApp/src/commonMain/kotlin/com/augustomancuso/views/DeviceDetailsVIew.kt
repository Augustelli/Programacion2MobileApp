package com.augustomancuso.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.augustomancuso.models.AdicionalDto
import com.augustomancuso.models.CaracteristicaDto
import com.augustomancuso.models.DispositivosDto
import com.augustomancuso.models.PersonalizacionDto

@Composable
fun DeviceDetailsView(
    device: DispositivosDto,
    onPurchase: (DispositivosDto) -> Unit,
    onGoBack: () -> Unit
) {
    var selectedPersonalizations by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedAdditionals by remember { mutableStateOf<Set<String>>(emptySet()) }
    var finalPrice by remember { mutableStateOf(device.precioBase.toDouble()) }
    var showError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var showConfirmationDialog by remember { mutableStateOf(false) }

    fun validatePersonalizations(): Boolean {
        return device.personalizaciones.all { it.nombre in selectedPersonalizations }
    }

    fun updateFinalPrice() {
        var price = device.precioBase.toDouble()
        val newRemainingAmounts = mutableMapOf<String, Float>()

        selectedPersonalizations.forEach { (_, value) ->
            val optionName = value.split(" - ")[0]
            val option =
                device.personalizaciones.flatMap { it.opciones }.find { it.nombre == optionName }
            option?.let {
                price += it.precioAdicional
            }
        }

        selectedAdditionals.forEach { idWithDescription ->
            val id = idWithDescription.split(" - ")[0]
            val additional = device.adicionales.find { it.id.toString() == id }
            additional?.let {
                if (price + it.precio > it.precioGratis && it.precioGratis != -1f) {
                    newRemainingAmounts[it.id.toString()] = 0f
                } else {
                    price += it.precio
                    newRemainingAmounts[it.id.toString()] =
                        (it.precioGratis - (price + it.precio)).toFloat()
                }
            }
        }

        finalPrice = price
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color(0xFF6200EE)
            ) {
                Text(
                    text = "Precio final: $finalPrice ${device.moneda}",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { showConfirmationDialog = true },
                    enabled = validatePersonalizations(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text("Comprar", color = Color(0xFF6200EE))
                }

                if (showConfirmationDialog) {
                    ConfirmationDialog(
                        device = device,
                        onConfirm = {
                            onPurchase(device)
                            showConfirmationDialog = false
                        },
                        onCancel = { showConfirmationDialog = false }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
                .background(Color(0xFFF5F5F5))
                .padding(bottom = 56.dp) // Add padding to avoid cutting off the last item
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onGoBack() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
            ) {
                Text("Volver", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Dispositivo: ${device.nombre}",
                style = MaterialTheme.typography.h5,
                color = Color(0xFF6200EE)
            )
            Text("Descripción: ${device.descripcion}", color = Color.Gray)
            Text("Precio Base: ${device.precioBase} USD", color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Caracteristicas", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
            device.caracteristicas.forEach { caracteristica ->
                CaracteristicaItem(caracteristica)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Personalizalo", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
            device.personalizaciones.forEach { personalization ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    PersonalizationItem(
                        personalization,
                        selectedPersonalizations
                    ) { key, value, description ->
                        selectedPersonalizations = selectedPersonalizations.toMutableMap()
                            .apply { put(key, "$value - $description") }
                        updateFinalPrice()
                        showError = !validatePersonalizations()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Adicionales", style = MaterialTheme.typography.h6, color = Color(0xFF6200EE))
            device.adicionales.forEach { additional ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    AdditionalItem(
                        additional,
                        selectedAdditionals,
                    ) { id, isSelected, description ->
                        selectedAdditionals = if (isSelected) {
                            selectedAdditionals + "$id - $description"
                        } else {
                            selectedAdditionals - "$id - $description"
                        }
                        updateFinalPrice()
                    }
                }
            }

            if (showError) {
                Text(
                    "Al menos 1 personalización de cada grupo se debe seleccionar",
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
fun CaracteristicaItem(caracteristica: CaracteristicaDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                caracteristica.nombre,
                style = MaterialTheme.typography.subtitle1,
                color = Color(0xFF6200EE)
            )
            Text(caracteristica.descripcion, color = Color.Gray)
        }
    }
}

@Composable
fun PersonalizationItem(
    personalization: PersonalizacionDto,
    selectedPersonalizations: Map<String, String>,
    onSelect: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            personalization.nombre,
            style = MaterialTheme.typography.subtitle1,
            color = Color(0xFF6200EE)
        )
        personalization.opciones.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPersonalizations[personalization.nombre]?.startsWith(option.nombre) == true,
                    onClick = {
                        onSelect(
                            personalization.nombre,
                            option.nombre,
                            option.descripcion
                        )
                    }
                )
                Text(
                    "${option.nombre} - ${option.descripcion} + ${option.precioAdicional} USD",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AdditionalItem(
    additional: AdicionalDto,
    selectedAdditionals: Set<String>,
    onSelect: (String, Boolean, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedAdditionals.contains("${additional.id} - ${additional.descripcion}"),
                onCheckedChange = { onSelect(additional.id.toString(), it, additional.descripcion) }
            )
            Text(
                "${additional.nombre} - ${additional.descripcion} | + ${additional.precio} USD",
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (additional.precioGratis != -1f) " ¡Sumá ${additional.precioGratis} USD para que sea gratis! " else "",
                color = Color.Green
            )
        }
    }
}
