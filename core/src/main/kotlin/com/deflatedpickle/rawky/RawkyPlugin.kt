@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventSerializeConfig
import com.deflatedpickle.rawky.api.TemplateSize
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.setting.RawkyDocument
import java.awt.Color

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
@Suppress("unused")
object RawkyPlugin {
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

        createTemplates()
    }

    private fun createTemplates() {
        TemplateSize("Atari Lynx", 160, 102)

        TemplateSize("Sega Game Gear", 160, 144)
        TemplateSize("Sega Nomad", 320, 224)

        TemplateSize("WonderSwan", 224, 144)
        TemplateSize("Xperia Play", 854, 480)
        TemplateSize("Epoch Game Pocket", 75, 64)
        TemplateSize("Gamate", 160, 152)
        TemplateSize("Microvision", 16, 16)
        TemplateSize("Watara Supervision", 160, 160)
        TemplateSize("TurboExpress", 400, 270)

        TemplateSize("Pandora", 800, 480)

        TemplateSize("Nintendo GameBoy", 160, 144)
        TemplateSize("Nintendo GameBoy Advance", 240, 160)
        TemplateSize("Nintendo DS", 256, 192)
        TemplateSize("New Nintendo 3DS (Top)", 400, 240)
        TemplateSize("New Nintendo 3DS (Bottom)", 320, 240)
        TemplateSize("Wii U GamePad", 854, 480)
        TemplateSize("Nintendo Switch Lite", 1280, 720)

        TemplateSize("PlayStation Portable", 480, 272)
        TemplateSize("PlayStation Vita", 960, 544)
    }
}