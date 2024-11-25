package com.augustomancuso.models

import kotlinx.serialization.Serializable

@Serializable
data class VentaDetalleDto(
    var idVenta: Int,
    var idDispositivo: Int,
    var codigo: String,
    var nombre: String,
    var descripcion: String,
    var precioBase: Float,
    var moneda: String,
    //var caracteristicas: List<CaracteristicaDto>,
    var catacteristicas: List<CaracteristicaDto>,
    var personalizaciones: List<PersonalizacionDetalleDto>,
    var adicionales: List<AdicionalDto>,
)
