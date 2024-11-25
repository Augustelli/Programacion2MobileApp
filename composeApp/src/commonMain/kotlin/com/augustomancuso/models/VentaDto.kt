package com.augustomancuso.models

import kotlinx.serialization.Serializable

@Serializable
data class VentaDto (
    var idVenta : Int,
    var codigo: String,
    var nombre: String,
    var descripcion: String,
    var precio: Float,

)
