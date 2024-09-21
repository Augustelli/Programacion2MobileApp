package com.augustomancuso.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class DispositivosDto(
    var id: Int,
    var codigo: String,
    var nombre: String,
    var descripcion: String,
    var precioBase: Float,
    var moneda: String,
    var caracteristicas: List<CaracteristicaDto>,
    var personalizaciones: List<PersonalizacionDto>,
    var adicionales: List<AdicionalDto>,
)
