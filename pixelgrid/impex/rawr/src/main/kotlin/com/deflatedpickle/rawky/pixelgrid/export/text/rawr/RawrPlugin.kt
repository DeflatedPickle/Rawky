/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "SpellCheckingInspection", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.export.text.rawr

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.github.underscore.U
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import java.io.FileOutputStream

@Plugin(
    value = "rawr",
    author = "DeflatedPickle",
    version = "1.1.1",
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

    override val exporterExtensions: MutableMap<String, List<String>> = mutableMapOf()
    override val openerExtensions: MutableMap<String, List<String>> = mutableMapOf()

    init {
        Exporter.registry[name] = this
        Opener.registry[name] = this

        for (i in listOf(exporterExtensions, openerExtensions)) {
            i.putAll(
                mapOf(
                    "Rawky" to listOf("rawr"),
                    "Rawky Binary" to listOf("rawrxd")
                )
            )
        }
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun export(doc: RawkyDocument, file: File) {
        val serializer = doc::class.serializer() as KSerializer<Any>

        val out = FileOutputStream(file, false)

        when (file.extension) {
            "rawr" -> {
                val json = Json.Default
                val jsonData = json.encodeToString(serializer, doc)

                out.write(U.formatJson(jsonData).toByteArray())
            }
            "rawrxd" -> {
                val cbor = Cbor.Default
                val cborData = cbor.encodeToByteArray(serializer, doc)

                out.write(cborData)
            }
        }

        out.flush()
        out.close()
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun open(file: File) = when (file.extension) {
        "rawr" -> Json.Default.decodeFromString(RawkyDocument::class.serializer(), file.readText())
        "rawrxd" -> Cbor.Default.decodeFromByteArray(RawkyDocument::class.serializer(), file.readBytes())
        else -> RawkyDocument(children = mutableListOf())
    }
}
