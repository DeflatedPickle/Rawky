/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.serializer

import com.deflatedpickle.rawky.api.ControlMode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ControlModeSerializer : KSerializer<ControlMode?> {
    override val descriptor =
        PrimitiveSerialDescriptor(
            serialName = "Control Mode",
            kind = PrimitiveKind.STRING,
        )

    override fun serialize(encoder: Encoder, value: ControlMode?) =
        encoder.encodeString(value?.name ?: "null")

    override fun deserialize(decoder: Decoder): ControlMode? {
        decoder.decodeString().let {
            return if (it == "null") {
                null
            } else {
                ControlMode.registry[it]
            }
        }
    }
}
