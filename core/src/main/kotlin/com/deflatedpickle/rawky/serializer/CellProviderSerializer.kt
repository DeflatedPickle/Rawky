/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.serializer

import com.deflatedpickle.rawky.api.CellProvider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CellProviderSerializer : KSerializer<CellProvider<Any>?> {
    override val descriptor =
        PrimitiveSerialDescriptor(
            serialName = "Cell Provider",
            kind = PrimitiveKind.STRING,
        )

    override fun serialize(encoder: Encoder, value: CellProvider<Any>?) =
        encoder.encodeString(value?.name ?: "null")

    override fun deserialize(decoder: Decoder): CellProvider<Any>? {
        decoder.decodeString().let {
            return if (it == "null") {
                null
            } else {
                CellProvider.registry[it] as CellProvider<Any>
            }
        }
    }
}
