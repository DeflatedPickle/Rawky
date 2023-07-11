/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.mode.keyboard.serializer

import com.deflatedpickle.rawky.pixelgrid.mode.keyboard.util.KeyCombo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.event.KeyEvent

@OptIn(ExperimentalSerializationApi::class)
object KeySerializer : KSerializer<KeyCombo> {
    override val descriptor =
        PrimitiveSerialDescriptor(
            serialName = "Key",
            kind = PrimitiveKind.INT,
        )

    override fun serialize(encoder: Encoder, value: KeyCombo) {
        val code = mutableListOf<Int>()
        code.add(value.key)

        if (value.modifier != null) {
            code.add(0, value.modifier)
        }

        encoder.encodeString(
            code.joinToString("+") { k ->
                KeyEvent::class
                    .java
                    .declaredFields
                    .first { it.name.startsWith("VK_") && it.get(null) == k }
                    .name
            },
        )
    }

    override fun deserialize(decoder: Decoder): KeyCombo {
        val decode =
            decoder.decodeString().split("+").map {
                KeyEvent::class.java.getDeclaredField(it).get(null) as Int
            }

        return if (decode.size == 1) {
            KeyCombo(decode.first())
        } else {
            KeyCombo(
                decode.last(),
                decode.first(),
            )
        }
    }
}
