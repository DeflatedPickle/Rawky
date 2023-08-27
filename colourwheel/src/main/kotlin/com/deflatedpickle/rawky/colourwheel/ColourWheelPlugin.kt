/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourwheel

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.JMenuItem
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
    private val fillFgItem = JMenuItem(
        "Fill with Foreground Colour",
        message = "Reset all cells",
        enabled = false,
    ) {
        fill(ColourWheelPanel.colourSelector.primary)
    }

    private val fillBgItem = JMenuItem(
        "Fill with Background Colour",
        message = "Reset all cells",
        enabled = false,
    ) {
        fill(ColourWheelPanel.colourSelector.secondary)
    }

    init {
        EventChangeTheme.addListener { ColourWheelPanel.updateUIRecursively() }

        EventChangeColour.addListener {
            ColourWheelPanel.colourPicker.color = it
            ColourWheelPanel.colourSelector.primary = it
        }

        EventCreateDocument.addListener {
            for (i in listOf(fillFgItem, fillBgItem)) {
                i.isEnabled = CellProvider.current == PixelCellPlugin
            }
        }

        EventOpenDocument.addListener {
            for (i in listOf(fillFgItem, fillBgItem)) {
                i.isEnabled = CellProvider.current == PixelCellPlugin
            }
        }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.EDIT.name) as JMenu).apply {
                    add(fillFgItem)
                    add(fillBgItem)
                }

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

    private fun fill(color: Color) {
        RawkyPlugin.document?.let { doc ->
            for (frame in doc) {
                for (layer in frame) {
                    for (cell in layer.child) {
                        cell.content = color
                    }
                }
            }

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            EventUpdateGrid.trigger(layer.child)
        }
    }
}
