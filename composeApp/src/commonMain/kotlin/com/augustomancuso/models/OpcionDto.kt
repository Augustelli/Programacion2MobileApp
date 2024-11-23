package com.augustomancuso.models

import kotlinx.serialization.Serializable


@Serializable
data class OpcionDto (
    var id: Int,
    var codigo: String,
    var nombre: String,
    var descripcion: String,
    var precioAdicional: Float,
)
