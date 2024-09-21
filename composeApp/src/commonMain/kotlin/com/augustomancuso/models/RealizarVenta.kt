package com.augustomancuso.models

import kotlin.time.TimeMark
import kotlin.time.TimeSource

data class RealizarVentaDto(
    var idDispositivo: Int,
    var personalizaciones: List<PersonalizacionDto>,
    var adicionales: List<AdicionalDto>,
    var precioFinal: Float,
    var fechaVenta: String = TimeSource.Monotonic.markNow().toString(),  // TODO Checkear formato de fecha tiene que ser asi "2024-08-10T20:15:00z"
)
