package com.augustomancuso.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = AdicionalDtoSerializer::class)
data class AdicionalDto(
    var id: Int,
    var nombre: String,
    var descripcion: String,
    var precio: Float? = 0f,
    var precioGratis: Float? = 0f,
) {
    val precioOrDefault: Float
        get() = precio ?: 0f

    val precioGratisOrDefault: Float
        get() = precioGratis ?: 0f
}

object AdicionalDtoSerializer : KSerializer<AdicionalDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AdicionalDto") {
        element<Int>("id")
        element<String>("nombre")
        element<String>("descripcion")
        element<Float?>("precio")
        element<Float?>("precioGratis")
    }

    override fun serialize(encoder: Encoder, value: AdicionalDto) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.id)
            encodeStringElement(descriptor, 1, value.nombre)
            encodeStringElement(descriptor, 2, value.descripcion)
            encodeNullableSerializableElement(descriptor, 3, Float.serializer(), value.precio)
            encodeNullableSerializableElement(descriptor, 4, Float.serializer(), value.precioGratis)
        }
    }

    override fun deserialize(decoder: Decoder): AdicionalDto {
        return decoder.decodeStructure(descriptor) {
            var id = 0
            var nombre = ""
            var descripcion = ""
            var precio: Float? = null
            var precioGratis: Float? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> id = decodeIntElement(descriptor, index)
                    1 -> nombre = decodeStringElement(descriptor, index)
                    2 -> descripcion = decodeStringElement(descriptor, index)
                    3 -> precio = decodeNullableSerializableElement(descriptor, index, Float.serializer())
                    4 -> precioGratis = decodeNullableSerializableElement(descriptor, index, Float.serializer())
                    else -> throw SerializationException("Unknown index $index")
                }
            }

            AdicionalDto(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio ?: 0f,
                precioGratis = precioGratis ?: 0f
            )
        }
    }
}