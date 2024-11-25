package com.augustomancuso.models

import io.ktor.http.toHttpDate
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable

@Serializable
class InformSellModel {

    var idDispositivo : Int = 0;
    var personalizaciones: List<SellItemModel> = listOf();
    var adicionales: List<SellItemModel> = listOf();
    var precioFinal: Float = 0.0f;
    var fechaVenta: String = "";
}

@Serializable
class SellItemModel {
    var id: Int = 0
    var precio: Float = 0.0f
}