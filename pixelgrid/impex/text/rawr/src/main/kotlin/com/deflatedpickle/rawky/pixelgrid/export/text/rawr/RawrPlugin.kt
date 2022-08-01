/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNUSED_PARAMETER", "SpellCheckingInspection")

package com.deflatedpickle.rawky.pixelgrid.export.text.rawr

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.github.underscore.U
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import java.io.FileOutputStream

@Plugin(
    value = "rawr",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Export Rawr files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@pixel_grid#*",
    ],
)
object RawrPlugin : Exporter, Opener {
    override val name = "Rawr"
    override val extensions = listOf("rawr")

    init {
        Exporter.registry[name] = this
        Opener.registry[name] = this
    }

    @OptIn(InternalSerializationApi::class)
    override fun export(doc: RawkyDocument, file: File) {
        val serializer = doc::class.serializer() as KSerializer<Any>

        val json = Json.Default
        val jsonData = json.encodeToString(serializer, doc)

        val out = FileOutputStream(file, false)

        out.write(U.formatJson(jsonData).toByteArray())
        out.flush()
        out.close()
    }

    @OptIn(InternalSerializationApi::class)
    override fun open(file: File) = Json.Default.decodeFromString(RawkyDocument::class.serializer(), file.readText())
}
