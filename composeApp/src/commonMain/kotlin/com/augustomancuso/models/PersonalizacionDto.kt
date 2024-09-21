package com.augustomancuso.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class PersonalizacionDto(
    var id: Int,
    var nombre: String,
    var descripcion: String,
    var opciones: List<OpcionDto>,

    )