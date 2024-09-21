package com.augustomancuso.models

data class VentaDetalleDto(
    var idVenta: Int,
    var idDispositivo: Number,
    var codigo: String,
    var nombre: String,
    var descripcion: String,
    var precioBase: Float,
    var moneda: String,
    var caracteristicas: List<CaracteristicaDto>,
    var personalizaciones: List<PersonalizacionDto>,
    var adicionales: List<AdicionalDto>,
)
