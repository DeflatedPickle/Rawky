/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourwheel

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.Color
import javax.swing.JMenu
import javax.swing.KeyStroke

@Plugin(
    value = "colour_wheel",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a wheel to select a colour
    """,
    type = PluginType.COMPONENT,
    component = ColourWheelPanel::class,
)
@Suppress("unused")
object ColourWheelPlugin {
    init {
        EventChangeTheme.addListener { ColourWheelPanel.updateUIRecursively() }

        EventChangeColour.addListener {
            ColourWheelPanel.colourPicker.color = it
            ColourWheelPanel.colourSelector.primary = it
        }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.TOOLS.name) as JMenu).apply {
                    add(
                        "Default Colours",
                        accelerator = KeyStroke.getKeyStroke("D"),
                        message = "Set the foreground colour to black and the background colour to white"
                    ) {
                        ColourWheelPanel.colourSelector.primary = Color.BLACK
                        ColourWheelPanel.colourSelector.secondary = Color.WHITE
                    }

                    add(
                        "Swap Colours",
                        accelerator = KeyStroke.getKeyStroke("X"),
                        message = "Swap foreground and background colours",
                    ) {
                        val cache = ColourWheelPanel.colourSelector.primary

                        ColourWheelPanel.colourSelector.primary = ColourWheelPanel.colourSelector.secondary
                        ColourWheelPanel.colourSelector.secondary = cache
                    }

                    addSeparator()
                }
            }
        }
    }
}
