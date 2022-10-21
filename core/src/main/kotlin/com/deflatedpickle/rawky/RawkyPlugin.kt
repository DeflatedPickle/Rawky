@file:Suppress("SpellCheckingInspection")
@file:OptIn(InternalSerializationApi::class)

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventSerializeConfig
import com.deflatedpickle.marvin.extensions.div
import com.deflatedpickle.rawky.api.template.Template
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.setting.RawkyDocument
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "core",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        The core program
        <br>
        This provides the main API for Rawky
    """,
    type = PluginType.CORE_API,
    settings = RawkySettings::class,
)
object RawkyPlugin {
    private val templateFolder = (File(".") / "template").apply { mkdirs() }
    private val guideFolder = (File(".") / "guide").apply { mkdirs() }

    var document: RawkyDocument? = null
    var colour: Color = Color.CYAN
        set(value) {
            field = value
            EventChangeColour.trigger(value)
        }

    init {
        EventSerializeConfig.addListener {
            if ("core" in it.name) {
                if (Tool.isToolValid()) {
                    EventChangeTool.trigger(Tool.current)
                }
            }
        }

        FileUtils.copyResourcesRecursively(
            RawkyPlugin::class.java.getResource("/template"),
            templateFolder
        )

        for (i in templateFolder.walk()) {
            if (i.isFile && i.extension == "json") {
                val json = Json.Default.decodeFromString(Template::class.serializer(), i.readText())
                Template.registry[json.name] = json
            }
        }

        FileUtils.copyResourcesRecursively(
            RawkyPlugin::class.java.getResource("/guide"),
            guideFolder
        )

        for (i in guideFolder.walk()) {
            if (i.isFile && i.extension == "json") {
                val json = Json.Default.decodeFromString<List<Guide>>(i.readText())
                Guide.registry[i.nameWithoutExtension] = json
            }
        }
    }
}