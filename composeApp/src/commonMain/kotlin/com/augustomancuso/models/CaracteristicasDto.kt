package com.augustomancuso.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class CaracteristicaDto(
    var id: Int,
    var nombre: String,
    var descripcion: String
)
