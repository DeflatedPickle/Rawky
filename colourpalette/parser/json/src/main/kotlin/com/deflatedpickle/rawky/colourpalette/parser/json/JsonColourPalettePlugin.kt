/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.json

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "json_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses a few kinds of JSON formatted colour palettes
    """,
)
@Suppress("unused")
object JsonColourPalettePlugin : PaletteParser<Color> {
    override val name = "JSON"

    init {
        ColourPalettePlugin.registry["json"] = this

        FileUtils.copyResourcesRecursively(
            JsonColourPalettePlugin::class.java.getResource("/palette"),
            ColourPalettePlugin.folder
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        when (val json = Json.Default.parseToJsonElement(file.readText())) {
            is JsonArray -> {
                for (i in json) {
                    getColour(i)?.let { p ->
                        colours.put(p, null)
                    }
                }
            }
            is JsonObject -> {
                for ((k, v) in json) {
                    if (isColour(k)) {
                        getColour(k)?.let { p ->
                            colours.put(p, v.jsonPrimitive.content)
                        }
                    } else {
                        if (v is JsonArray) {
                            for (i in v) {
                                getColour(i)?.let { p ->
                                    colours.put(p, null)
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }

        return Palette(
            file.nameWithoutExtension,
            colours
        )
    }

    private fun isColour(s: String) =
        s.startsWith("#") ||
            (s.startsWith("rgb(") && s.endsWith(")")) ||
            (s.startsWith("hsb(") && s.endsWith(")"))

    private fun getColour(element: JsonElement): Color? {
        if (element is JsonPrimitive && element.isString) {
            return getColour(element.content)
        }

        return null
    }

    private fun getColour(s: String): Color? =
        if (s.startsWith("#")) {
            when (s.length) {
                4 -> Color.decode("#${s[1]}${s[1]}${s[2]}${s[2]}${s[3]}${s[3]}")
                7 -> Color.decode(s)
                else -> null
            }
        } else if (s.startsWith("rgb(") && s.endsWith(")")) {
            val rs = s.removeSurrounding("rgb(", ")").split(",").map { it.toInt() }
            Color(rs[0], rs[1], rs[2])
        } else if (s.startsWith("hsb(") && s.endsWith(")")) {
            val rs = s.removeSurrounding("hsb(", ")").split(",").map { it.toFloat() }
            Color.getHSBColor(rs[0], rs[1], rs[2])
        } else {
            null
        }
}
