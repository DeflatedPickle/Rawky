/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.autosave.serializer

import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.autosave.util.FileType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@ExperimentalSerializationApi
@Serializer(forClass = FileType::class)
object FileTypeSerializer : KSerializer<FileType> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "FileType",
        kind = PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: FileType) =
        encoder.encodeSerializableValue(
            MapSerializer(String.serializer(), String.serializer()),
            mapOf(
                "handler" to value.handler.name,
                "extension" to value.extension,
            )
        )

    override fun deserialize(decoder: Decoder): FileType {
        val decode = decoder.decodeSerializableValue(
            MapSerializer(String.serializer(), String.serializer()),
        )

        return FileType(
            Exporter.registry[decode["handler"]!!]!!,
            decode["extension"]!!,
        )
    }
}
