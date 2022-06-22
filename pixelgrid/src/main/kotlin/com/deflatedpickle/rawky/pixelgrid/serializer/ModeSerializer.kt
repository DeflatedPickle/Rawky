package com.deflatedpickle.rawky.pixelgrid.serializer

import com.deflatedpickle.rawky.pixelgrid.api.Mode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

@ExperimentalSerializationApi
@Serializer(forClass = Mode::class)
object ModeSerializer : KSerializer<Mode?> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "Color",
        kind = PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: Mode?) =
        encoder.encodeString(value?.name ?: "null")

    override fun deserialize(decoder: Decoder): Mode? {
        decoder.decodeString().let {
            return if (it == "null") {
                null
            } else {
                Mode.registry[it]
            }
        }
    }
}