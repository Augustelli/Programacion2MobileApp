package com.augustomancuso.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class AdicionalDto(
    var id: Int,
    var nombre: String,
    var descripcion: String,
    var precio: Float,
    var precioGratis: Float,
)
