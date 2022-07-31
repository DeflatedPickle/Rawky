package com.deflatedpickle.rawky.autosave.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

@ExperimentalSerializationApi
@Serializer(forClass = File::class)
object FileSerializer : KSerializer<File> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "Mode",
        kind = PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: File) =
        encoder.encodeString(value.absolutePath)

    override fun deserialize(decoder: Decoder) = File(decoder.decodeString())
}