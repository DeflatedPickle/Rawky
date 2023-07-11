/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.bucket.serializer

import com.deflatedpickle.rawky.tool.bucket.api.Fill
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Fill::class)
object FillSerializer : KSerializer<Fill?> {
    override val descriptor =
        PrimitiveSerialDescriptor(
            serialName = "Mode",
            kind = STRING,
        )

    override fun serialize(encoder: Encoder, value: Fill?) =
        encoder.encodeString(value?.name ?: "null")

    override fun deserialize(decoder: Decoder): Fill? {
        decoder.decodeString().let {
            return if (it == "null") {
                null
            } else {
                Fill.registry[it]
            }
        }
    }
}
